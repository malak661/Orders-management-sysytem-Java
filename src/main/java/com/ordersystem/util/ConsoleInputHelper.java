package com.ordersystem.util;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Utility class for reading and validating console input.
 * TODO: implement readString(prompt)
 * TODO: implement readInt(prompt) -> with retry on invalid input
 * TODO: implement readBigDecimal(prompt) -> with retry on invalid input
 * TODO: implement readYesNo(prompt)
 */
public class ConsoleInputHelper {

    private final Scanner scanner;

    public ConsoleInputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readString(String prompt) {
        // TODO
        return null;
    }

    public int readInt(String prompt) {
        // TODO
        return 0;
    }

    public BigDecimal readBigDecimal(String prompt) {
        // TODO
        return null;
    }

    public boolean readYesNo(String prompt) {
        // TODO
        return false;
    }
}
