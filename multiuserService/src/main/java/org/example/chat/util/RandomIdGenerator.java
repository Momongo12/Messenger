package org.example.chat.util;

import java.util.UUID;

final public class RandomIdGenerator {

    public static String generate() {
        return UUID.randomUUID().toString().substring(0, 4);
    }

    private RandomIdGenerator () {}
}
