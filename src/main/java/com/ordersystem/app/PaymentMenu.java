package com.ordersystem.app;

import com.ordersystem.exception.PaymentExceedsBalanceException;
import com.ordersystem.model.Payment;
import com.ordersystem.model.PaymentMethod;
import com.ordersystem.service.PaymentService;
import com.ordersystem.util.ConsoleInputHelper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Console menu for recording payments against orders (AC8).
 *
 * <p>Menu options:
 * <pre>
 *   1. Record a payment
 *   2. View payments for an order
 *   0. Back to main menu
 * </pre>
 *
 * <p>Exceptions caught and displayed as user-friendly messages:
 * <ul>
 *   <li>{@link PaymentExceedsBalanceException} — amount exceeds remaining balance</li>
 *   <li>{@link IllegalArgumentException}       — blank input or invalid payment method</li>
 *   <li>{@link IllegalStateException}          — OrderService not yet implemented (stub guard)</li>
 *   <li>{@link NumberFormatException}          — non-numeric amount entered by user</li>
 *   <li>{@link RuntimeException}               — DB/JDBC not ready (DbConnection stub)</li>
 * </ul>
 */
public class PaymentMenu {

    private final PaymentService     paymentService;
    private final ConsoleInputHelper inputHelper;

    // NOTE: ConsoleInputHelper is currently a stub — all its methods return null/0.
    // This Scanner is injected as a fallback for direct console reading until
    // ConsoleInputHelper is implemented by its owner.
    //
    // Every location that uses scanner directly is marked:
    //   // TODO (ConsoleInputHelper): replace with inputHelper.readXxx(...)
    //
    // When ConsoleInputHelper is ready:
    //   1. Remove the scanner field and constructor parameter.
    //   2. Swap each marked scanner call for the indicated inputHelper call.
    //   3. Remove the private readLine() helper at the bottom of this class.
    //
    // The Scanner is passed in (not created here) so that the single System.in
    // stream is shared across all menus — Main.java creates one Scanner for the
    // whole application and passes it to every menu via ConsoleInputHelper or directly.
    private final Scanner scanner;

    // ---------------------------------------------------------------- Constructor

    /**
     * @param paymentService the Payment business-logic layer (must not be null)
     * @param inputHelper    console input utility — stored for future use once implemented
     * @param scanner        shared console Scanner — used directly while inputHelper is a stub
     */
    public PaymentMenu(PaymentService paymentService,
                       ConsoleInputHelper inputHelper,
                       Scanner scanner) {
        if (paymentService == null) throw new IllegalArgumentException("paymentService must not be null");
        if (inputHelper    == null) throw new IllegalArgumentException("inputHelper must not be null");
        if (scanner        == null) throw new IllegalArgumentException("scanner must not be null");
        this.paymentService = paymentService;
        this.inputHelper    = inputHelper;
        this.scanner        = scanner;
    }

    // -------------------------------------------------------------- Public API

    /**
     * Displays the Payment menu and dispatches user choices in a loop
     * until the user selects 0 (Back).
     */
    public void show() {
        boolean running = true;
        while (running) {
            printMenuHeader();

            // TODO (ConsoleInputHelper): replace the line below with:
            //     String choice = inputHelper.readString("Choice: ");
            String choice = readLine();

            switch (choice) {
                case "1" -> handleRecordPayment();
                case "2" -> handleViewPayments();
                case "0" -> running = false;
                default  -> printError("Invalid option '" + choice + "'. Please enter 1, 2, or 0.");
            }
        }
    }

    // ----------------------------------------------------------- Private handlers

    /**
     * Option 1 — Record a payment against an order.
     *
     * <p>Steps:
     * <ol>
     *   <li>Prompt for order ID.</li>
     *   <li>Display the current remaining balance so the user knows the maximum.</li>
     *   <li>Prompt for payment amount.</li>
     *   <li>Prompt for payment method (shows valid choices).</li>
     *   <li>Call {@link PaymentService#recordPayment} and confirm success.</li>
     * </ol>
     */
    private void handleRecordPayment() {
        printSectionHeader("Record a Payment");

        // ── Step 1: Order ID ──────────────────────────────────────────────────
        System.out.print("  Enter Order ID: ");
        // TODO (ConsoleInputHelper): replace the two lines above/below with:
        //     String orderId = inputHelper.readString("  Enter Order ID: ");
        String orderId = readLine();

        if (orderId.isBlank()) {
            printError("Order ID cannot be blank.");
            return;
        }

        // ── Step 2: Show remaining balance (informational — non-fatal if unavailable) ──
        try {
            BigDecimal balance = paymentService.getRemainingBalance(orderId);
            if (balance != null) {
                System.out.println("  Remaining balance : " + balance);
            } else {
                printWarning("Remaining balance is unavailable because OrderService is not yet implemented.");
                printWarning("Payment recording will be attempted but will also fail for the same reason.");
                printWarning("This will resolve once OrderService.getOrderById() is implemented.");
            }
        } catch (IllegalArgumentException e) {
            // Blank orderId already caught above; this covers edge cases from service validation.
            printError("Input error while fetching balance: " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            // DB not ready, or another unexpected error — show message but don't abort;
            // let the user attempt the payment and see the more specific error there.
            printWarning("Could not fetch balance: " + e.getMessage());
        }

        // ── Step 3: Payment amount ────────────────────────────────────────────
        System.out.print("  Enter amount    : ");
        // TODO (ConsoleInputHelper): replace the two lines above/below with:
        //     BigDecimal amount = inputHelper.readBigDecimal("  Enter amount    : ");
        BigDecimal amount;
        String amountRaw = readLine();
        try {
            amount = new BigDecimal(amountRaw);
        } catch (NumberFormatException e) {
            printError("'" + amountRaw + "' is not a valid number. Please enter a value such as 150.00");
            return;
        }

        // ── Step 4: Payment method ────────────────────────────────────────────
        String validMethods = Arrays.stream(PaymentMethod.values())
                .map(Enum::name)
                .collect(Collectors.joining(" | "));
        System.out.println("  Valid methods   : " + validMethods);
        System.out.print("  Enter method    : ");
        // TODO (ConsoleInputHelper): replace the two lines above/below with:
        //     String method = inputHelper.readString("  Enter method    : ");
        String method = readLine();

        // ── Step 5: Record the payment ────────────────────────────────────────
        try {
            Payment saved = paymentService.recordPayment(orderId, amount, method);
            System.out.println();
            System.out.println("  [OK] Payment recorded successfully.");
            System.out.println();
            printPaymentDetail(saved);

        } catch (PaymentExceedsBalanceException e) {
            // Exception message is already user-readable (built in the exception class).
            printError(e.getMessage());

        } catch (IllegalArgumentException e) {
            // Covers: invalid method name, zero/negative amount, blank orderId from service.
            printError("Input error: " + e.getMessage());

        } catch (IllegalStateException e) {
            // Covers the STUB GUARD in PaymentService — OrderService not yet ready.
            printError("System not ready — " + e.getMessage());

        } catch (RuntimeException e) {
            // Covers JDBC / DB errors from the repository layer.
            printError("Unexpected system error: " + e.getMessage());
        }
    }

    /**
     * Option 2 — View all payments recorded against an order.
     *
     * <p>Steps:
     * <ol>
     *   <li>Prompt for order ID.</li>
     *   <li>Fetch and display the full payment list.</li>
     *   <li>Display the current remaining balance.</li>
     * </ol>
     */
    private void handleViewPayments() {
        printSectionHeader("View Payments for an Order");

        // ── Step 1: Order ID ──────────────────────────────────────────────────
        System.out.print("  Enter Order ID: ");
        // TODO (ConsoleInputHelper): replace the two lines above/below with:
        //     String orderId = inputHelper.readString("  Enter Order ID: ");
        String orderId = readLine();

        if (orderId.isBlank()) {
            printError("Order ID cannot be blank.");
            return;
        }

        // ── Step 2 & 3: Fetch payments and remaining balance ──────────────────
        try {
            List<Payment> payments = paymentService.getPaymentsForOrder(orderId);

            System.out.println();
            if (payments.isEmpty()) {
                System.out.println("  No payments found for order: " + orderId);
            } else {
                System.out.println("  Payments for order [" + orderId + "]:");
                System.out.println("  " + "─".repeat(58));
                for (int i = 0; i < payments.size(); i++) {
                    System.out.println("  Payment #" + (i + 1));
                    printPaymentDetail(payments.get(i));
                    if (i < payments.size() - 1) {
                        System.out.println();
                    }
                }
                System.out.println("  " + "─".repeat(58));
            }

            // Remaining balance — separate try so a balance fetch failure
            // does not hide the payment list that was already printed successfully.
            try {
                BigDecimal balance = paymentService.getRemainingBalance(orderId);
                if (balance != null) {
                    System.out.println("  Remaining balance : " + balance);
                } else {
                    printWarning("Remaining balance unavailable (OrderService not yet implemented).");
                }
            } catch (RuntimeException balEx) {
                printWarning("Could not compute remaining balance: " + balEx.getMessage());
            }

        } catch (IllegalArgumentException e) {
            printError("Input error: " + e.getMessage());

        } catch (IllegalStateException e) {
            printError("System not ready — " + e.getMessage());

        } catch (RuntimeException e) {
            printError("Unexpected system error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────── Display helpers

    private void printMenuHeader() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║           PAYMENT MENU               ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.println("  ║  1. Record a payment                 ║");
        System.out.println("  ║  2. View payments for an order       ║");
        System.out.println("  ║  0. Back                             ║");
        System.out.println("  ╚══════════════════════════════════════╝");
        System.out.print("  Choice: ");
    }

    private void printSectionHeader(String title) {
        System.out.println();
        System.out.println("  ── " + title + " ──");
    }

    /**
     * Prints a formatted block for a single {@link Payment}.
     * Each field is on its own line with a consistent label width.
     */
    private void printPaymentDetail(Payment p) {
        System.out.println("    ID        : " + p.getId());
        System.out.println("    Order ID  : " + p.getOrderId());
        System.out.println("    Amount    : " + p.getAmount());
        System.out.println("    Method    : " + p.getMethod());
        System.out.println("    Status    : " + p.getStatus());
        System.out.println("    Timestamp : " + (p.getTimestamp() != null ? p.getTimestamp() : "N/A"));
    }

    private void printError(String message) {
        System.out.println();
        System.out.println("  [ERROR] " + message);
    }

    private void printWarning(String message) {
        System.out.println("  [WARN]  " + message);
    }

    /**
     * Reads a trimmed, non-null line from the console.
     *
     * <p>TODO (ConsoleInputHelper): This helper becomes redundant once
     * {@link ConsoleInputHelper#readString(String)} is implemented.
     * At that point, remove this method and replace all call sites
     * with the appropriate {@code inputHelper.readXxx(prompt)} call.
     */
    private String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "";
    }
}
