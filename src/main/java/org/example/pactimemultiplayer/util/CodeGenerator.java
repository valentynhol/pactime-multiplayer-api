package org.example.pactimemultiplayer.util;

import java.security.SecureRandom;

public class CodeGenerator {

    private static final String ALLOWED = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALLOWED.charAt(RANDOM.nextInt(ALLOWED.length())));
        }
        return sb.toString();
    }
}
