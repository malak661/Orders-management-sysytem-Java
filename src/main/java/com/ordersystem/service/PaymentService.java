package com.ordersystem.service;

// OrderNotFoundException is referenced only in Javadoc @throws and a commented-out line.
// TODO: Uncomment the import below when the null-guard stub is replaced with the real
//       OrderNotFoundException throw once OrderService.getOrderById() is implemented.
// import com.ordersystem.exception.OrderNotFoundException;
import com.ordersystem.exception.PaymentExceedsBalanceException;
import com.ordersystem.model.Order;
// OrderStatus is referenced only in a commented-out transitionStatus call.
// TODO: Uncomment the import below when OrderService.transitionStatus() is implemented
//       and the corresponding call in recordPayment() is uncommented.
// import com.ordersystem.model.OrderStatus;
import com.ordersystem.model.Payment;
import com.ordersystem.model.PaymentMethod;
import com.ordersystem.model.PaymentStatus;
import com.ordersystem.repository.PaymentRepository;
import com.ordersystem.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business Logic Layer for Payment operations (AC8).
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Record a payment against an order, rejecting overpayments.</li>
 *   <li>Compute the remaining unpaid balance for an order.</li>
 *   <li>Retrieve all payments belonging to an order.</li>
 *   <li>Transition the order to {@link OrderStatus#PAID} once the balance
 *       reaches zero (requires {@link OrderService} — see TODO below).</li>
 * </ul>
 *
 * <p><b>Dependency on OrderService:</b><br>
 * This service needs {@link OrderService} for two things:
 * <ol>
 *   <li>Fetching the order's {@code total} field to calculate the remaining
 *       balance ({@link OrderService#getOrderById(String)}).</li>
 *   <li>Transitioning the order status to {@link OrderStatus#PAID} when the
 *       balance reaches zero ({@link OrderService#transitionStatus(String, OrderStatus)}).</li>
 * </ol>
 * Both methods are currently stubs in {@link OrderService} (return {@code null}).
 * Until they are implemented by the Order module owner, this service guards
 * every call with null-checks and clear TODO comments rather than failing silently.
 *
 * TODO: Once OrderService.getOrderById() and OrderService.transitionStatus()
 *       are implemented, remove the defensive null-checks in this class that
 *       are marked with "STUB GUARD".
 */
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // NOTE: OrderService is injected via the constructor (see below).
    //       It is required for two operations:
    //         1. Fetching the order total to compute the remaining balance.
    //         2. Transitioning the order status to PAID when the balance reaches zero.
    //       Both of these are guarded with null-checks until OrderService is fully implemented.
    //       TODO (Main.java owner): wire as:
    //           new PaymentService(paymentRepository, orderService)
    private final OrderService orderService;

    /**
     * Preferred constructor — accepts both dependencies.
     *
     * @param paymentRepository persistence layer for payments (must not be null)
     * @param orderService      used to fetch order totals and update order status
     *                          (must not be null; may be a stub during development)
     */
    public PaymentService(PaymentRepository paymentRepository, OrderService orderService) {
        if (paymentRepository == null) throw new IllegalArgumentException("paymentRepository must not be null");
        if (orderService      == null) throw new IllegalArgumentException("orderService must not be null");
        this.paymentRepository = paymentRepository;
        this.orderService      = orderService;
    }

    // ------------------------------------------------------------------ API

    /**
     * Records a payment against an order.
     *
     * <p>Business rules enforced:
     * <ol>
     *   <li>{@code amount} must be positive ({@code > 0}).</li>
     *   <li>{@code method} must be a valid {@link PaymentMethod} name
     *       (case-insensitive).</li>
     *   <li>{@code amount} must not exceed the current remaining balance
     *       (throws {@link PaymentExceedsBalanceException} otherwise).</li>
     *   <li>The {@link Payment} is created with status {@link PaymentStatus#COMPLETED}
     *       because recording a payment means it has already been received.</li>
     *   <li>If the remaining balance drops to zero after this payment, the order
     *       is transitioned to {@link OrderStatus#PAID} via {@link OrderService}.</li>
     * </ol>
     *
     * @param orderId the ID of the order being paid
     * @param amount  the payment amount (must be {@code > 0})
     * @param method  the payment method string (e.g. "CASH", "CARD", "BANK_TRANSFER")
     * @return the persisted {@link Payment} record
     * @throws IllegalArgumentException        if {@code amount} ≤ 0 or {@code method} is invalid
     * @throws OrderNotFoundException          if no order exists with {@code orderId}
     *                                         (once OrderService is implemented)
     * @throws PaymentExceedsBalanceException  if {@code amount} exceeds the remaining balance
     */
    public Payment recordPayment(String orderId, BigDecimal amount, String method) {

        // --- 1. Validate inputs -------------------------------------------

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId must not be blank.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }

        PaymentMethod paymentMethod = parseMethod(method);

        // --- 2. Check remaining balance ------------------------------------

        BigDecimal remaining = getRemainingBalance(orderId);

        // STUB GUARD: getRemainingBalance returns null when OrderService is a stub.
        // TODO: Remove this guard once OrderService.getOrderById() is implemented.
        if (remaining == null) {
            throw new IllegalStateException(
                "Cannot record payment: order total is unavailable because " +
                "OrderService.getOrderById() is not yet implemented. " +
                "Wire a fully implemented OrderService into PaymentService.");
        }

        if (amount.compareTo(remaining) > 0) {
            throw new PaymentExceedsBalanceException(amount, remaining);
        }

        // --- 3. Build and persist the Payment record ----------------------

        Payment payment = new Payment(
                IdGenerator.generate(),
                orderId,
                amount,
                paymentMethod,
                PaymentStatus.COMPLETED,   // payment is being recorded as received
                LocalDateTime.now()
        );

        Payment saved = paymentRepository.save(payment);

        // --- 4. Transition order to PAID if fully settled -----------------

        BigDecimal newRemaining = remaining.subtract(amount);

        if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {
            // TODO: Uncomment the line below once OrderService.transitionStatus() is implemented.
            //       The call is intentionally left as a comment so that the rest of the payment
            //       flow works end-to-end even while OrderService is still a stub.
            //
            // orderService.transitionStatus(orderId, OrderStatus.PAID);
        }

        return saved;
    }

    /**
     * Calculates the unpaid balance remaining on an order.
     *
     * <p>Formula: {@code remaining = order.total − Σ(amount of COMPLETED payments)}
     *
     * <p>Only {@link PaymentStatus#COMPLETED} payments are counted.
     * {@link PaymentStatus#PENDING} and {@link PaymentStatus#FAILED} payments
     * do not reduce the balance.
     *
     * @param orderId the ID of the order
     * @return the remaining balance, or {@code null} if the order total cannot
     *         be retrieved (i.e. {@link OrderService} is not yet implemented)
     * @throws IllegalArgumentException if {@code orderId} is blank
     * @throws OrderNotFoundException   if no order exists with {@code orderId}
     *                                  (once OrderService is implemented)
     */
    public BigDecimal getRemainingBalance(String orderId) {

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId must not be blank.");
        }

        // TODO: Replace the block below with a proper order lookup once
        //       OrderService.getOrderById() is implemented.
        //       Expected code:
        //           Order order = orderService.getOrderById(orderId);
        //           if (order == null) throw new OrderNotFoundException(orderId);
        //           BigDecimal orderTotal = order.getTotal();
        //
        // STUB GUARD: OrderService.getOrderById() currently returns null.
        // We call it and return null early so callers can detect the stub state.
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            // Stub state — cannot compute balance without the order total.
            return null;
        }

        BigDecimal orderTotal = order.getTotal();
        if (orderTotal == null) {
            // Order exists but its total has not been calculated yet.
            throw new IllegalStateException(
                "Order '" + orderId + "' has a null total. " +
                "Ensure OrderService.calculateTotal() is called before recording payments.");
        }

        // Sum only COMPLETED payments — PENDING/FAILED do not count.
        BigDecimal paid = paymentRepository
                .findByOrderId(orderId)
                .stream()
                .filter(p -> PaymentStatus.COMPLETED.equals(p.getStatus()))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return orderTotal.subtract(paid);
    }

    /**
     * Returns all payment records associated with a given order, ordered
     * by timestamp ascending (as returned by the repository).
     *
     * @param orderId the ID of the order
     * @return a non-null list; empty if no payments have been recorded yet
     * @throws IllegalArgumentException if {@code orderId} is blank
     */
    public List<Payment> getPaymentsForOrder(String orderId) {

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId must not be blank.");
        }

        return paymentRepository.findByOrderId(orderId);
    }

    // --------------------------------------------------------- Private helpers

    /**
     * Converts a raw method string (from console input) to a {@link PaymentMethod} enum.
     * Accepts case-insensitive input, e.g. "cash", "CARD", "bank_transfer".
     *
     * @param method the raw input string
     * @return the matching {@link PaymentMethod}
     * @throws IllegalArgumentException if the string does not match any enum constant
     */
    private PaymentMethod parseMethod(String method) {
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException(
                "Payment method must not be blank. Valid values: CASH, CARD, BANK_TRANSFER.");
        }
        try {
            return PaymentMethod.valueOf(method.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Unknown payment method: '" + method + "'. Valid values: CASH, CARD, BANK_TRANSFER.");
        }
    }
}
