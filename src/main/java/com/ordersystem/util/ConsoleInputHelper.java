package com.ordersystem.util;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Utility class for reading and validating console input.
 */
public class ConsoleInputHelper {

    private final Scanner scanner;

    public ConsoleInputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine();
        return value == null ? "" : value.trim();
    }

    public int readInt(String prompt) {
        while (true) {
            String raw = readString(prompt);
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    public BigDecimal readBigDecimal(String prompt) {
        while (true) {
            String raw = readString(prompt);
            try {
                return new BigDecimal(raw);
            } catch (NumberFormatException e) {
                System.out.println("Invalid decimal value. Please enter a valid number.");
            }
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String raw = readString(prompt + " (y/n): ");
            if (raw.equalsIgnoreCase("y") || raw.equalsIgnoreCase("yes")) {
                return true;
            }
            if (raw.equalsIgnoreCase("n") || raw.equalsIgnoreCase("no")) {
                return false;
            }
            System.out.println("Please answer 'y' or 'n'.");
        }
    }
}
