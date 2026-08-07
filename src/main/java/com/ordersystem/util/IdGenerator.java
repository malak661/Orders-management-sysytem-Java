package com.ordersystem.util;

import java.util.UUID;

/**
 * Utility class for generating unique IDs for entities.
 */
public class IdGenerator {

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
