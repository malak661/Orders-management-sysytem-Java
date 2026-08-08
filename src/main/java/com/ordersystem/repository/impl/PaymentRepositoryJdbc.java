package com.ordersystem.repository.impl;

import com.ordersystem.model.Payment;
import com.ordersystem.model.PaymentMethod;
import com.ordersystem.model.PaymentStatus;
import com.ordersystem.repository.PaymentRepository;
import com.ordersystem.util.DbConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SQLite/JDBC implementation of PaymentRepository.
 *
 * Column mapping (see resources/schema.sql):
 *   id        TEXT PRIMARY KEY
 *   order_id  TEXT NOT NULL
 *   amount    TEXT NOT NULL   -- BigDecimal stored as TEXT to preserve precision
 *   method    TEXT NOT NULL   -- PaymentMethod enum name
 *   status    TEXT NOT NULL   -- PaymentStatus enum name
 *   timestamp TEXT            -- LocalDateTime stored as ISO-8601 string
 *
 * NOTE: PaymentRepository has no update() method.
 * TODO: Add update(Payment) to PaymentRepository interface if payment status
 *       needs to move from PENDING → COMPLETED / FAILED after recording.
 *       Until then, set the final status at save time.
 */
public class PaymentRepositoryJdbc implements PaymentRepository {

    // ------------------------------------------------------------------ SQL

    private static final String SQL_INSERT =
            "INSERT INTO payments (id, order_id, amount, method, status, timestamp) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT id, order_id, amount, method, status, timestamp " +
            "FROM payments WHERE id = ?";

    private static final String SQL_FIND_BY_ORDER_ID =
            "SELECT id, order_id, amount, method, status, timestamp " +
            "FROM payments WHERE order_id = ? ORDER BY timestamp ASC";

    private static final String SQL_FIND_ALL =
            "SELECT id, order_id, amount, method, status, timestamp " +
            "FROM payments ORDER BY timestamp ASC";

    // ---------------------------------------------------------- Interface API

    /**
     * Persists a new Payment record.
     * The caller is responsible for setting all fields (id, status, timestamp)
     * before calling save — this method performs a straight INSERT.
     *
     * @return the same {@code payment} object on success.
     * @throws RuntimeException wrapping any {@link SQLException}, or if the
     *         database connection is not yet available (DbConnection stub).
     */
    @Override
    public Payment save(Payment payment) {
        // TODO: DbConnection.getConnection() currently returns null.
        //       This method will work correctly once DbConnection is implemented
        //       by whoever owns the util package.
        try (Connection conn = requireConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, payment.getId());
            ps.setString(2, payment.getOrderId());
            ps.setString(3, payment.getAmount().toPlainString());
            ps.setString(4, payment.getMethod().name());
            ps.setString(5, payment.getStatus().name());
            ps.setString(6, payment.getTimestamp() != null
                    ? payment.getTimestamp().toString()
                    : null);

            ps.executeUpdate();
            return payment;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save payment id=" + payment.getId(), e);
        }
    }

    /**
     * Finds a single Payment by its primary key.
     *
     * @return {@link Optional#empty()} if no row found.
     * @throws RuntimeException wrapping any {@link SQLException}, or if the
     *         database connection is not yet available (DbConnection stub).
     */
    @Override
    public Optional<Payment> findById(String id) {
        // TODO: depends on DbConnection.getConnection() — see note in save().
        try (Connection conn = requireConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find payment by id=" + id, e);
        }
    }

    /**
     * Returns all Payment records for a given order, ordered by timestamp ascending.
     * An order may have multiple partial payments (partial payment scenario).
     *
     * @return an empty list if the order has no payments yet.
     * @throws RuntimeException wrapping any {@link SQLException}, or if the
     *         database connection is not yet available (DbConnection stub).
     */
    @Override
    public List<Payment> findByOrderId(String orderId) {
        // TODO: depends on DbConnection.getConnection() — see note in save().
        try (Connection conn = requireConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ORDER_ID)) {

            ps.setString(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                return collectRows(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find payments for orderId=" + orderId, e);
        }
    }

    /**
     * Returns every Payment record in the database, ordered by timestamp ascending.
     * Intended primarily for reporting.
     *
     * @return an empty list if the payments table is empty.
     * @throws RuntimeException wrapping any {@link SQLException}, or if the
     *         database connection is not yet available (DbConnection stub).
     */
    @Override
    public List<Payment> findAll() {
        // TODO: depends on DbConnection.getConnection() — see note in save().
        try (Connection conn = requireConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)) {

            // ResultSet is in its own nested try block — consistent with all
            // other methods and avoids ambiguous close ordering in the outer block.
            try (ResultSet rs = ps.executeQuery()) {
                return collectRows(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all payments", e);
        }
    }

    // --------------------------------------------------------- Private helpers

    /**
     * Obtains a JDBC {@link Connection} from {@link DbConnection#getConnection()}
     * and fails fast with a clear {@link RuntimeException} if the connection is
     * {@code null} (i.e. DbConnection is still a stub).
     *
     * <p>Without this guard, a {@code null} connection would cause a
     * {@link NullPointerException} on the subsequent {@code conn.prepareStatement()}
     * call, which would bypass the {@code catch (SQLException)} block and propagate
     * as an unwrapped NPE — giving the caller no useful diagnostic information.
     *
     * <p>TODO: Remove the null check once DbConnection.getConnection() is implemented;
     *          it will never return null from a real JDBC driver.
     *
     * @throws RuntimeException if the connection is null or a {@link SQLException} occurs
     */
    private Connection requireConnection() throws SQLException {
        Connection conn = DbConnection.getConnection();
        if (conn == null) {
            throw new RuntimeException(
                "Database connection is not available. " +
                "DbConnection.getConnection() returned null — " +
                "implement DbConnection before using the repository.");
        }
        return conn;
    }

    /**
     * Maps the current row of a {@link ResultSet} to a {@link Payment} object.
     * Does NOT call {@link ResultSet#next()}; the caller is responsible for
     * positioning the cursor.
     */
    private Payment mapRow(ResultSet rs) throws SQLException {
        String id        = rs.getString("id");
        String orderId   = rs.getString("order_id");
        String amountStr = rs.getString("amount");
        String methodStr = rs.getString("method");
        String statusStr = rs.getString("status");
        String tsStr     = rs.getString("timestamp");

        BigDecimal    amount    = new BigDecimal(amountStr);
        PaymentMethod method    = PaymentMethod.valueOf(methodStr);
        PaymentStatus status    = PaymentStatus.valueOf(statusStr);
        LocalDateTime timestamp = (tsStr != null && !tsStr.isEmpty())
                ? LocalDateTime.parse(tsStr)
                : null;

        return new Payment(id, orderId, amount, method, status, timestamp);
    }

    /**
     * Drains a {@link ResultSet} into a {@link List} of {@link Payment} objects.
     */
    private List<Payment> collectRows(ResultSet rs) throws SQLException {
        List<Payment> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapRow(rs));
        }
        return results;
    }
}
