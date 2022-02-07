package de.byjoker.myjfql.util;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IDGenerator {

    private static final Random random = new Random();

    public static String generate(String letters, int length) {
        return IntStream.range(0, length).mapToObj(i -> String.valueOf(letters.charAt(random.nextInt(letters.length())))).collect(Collectors.joining());
    }

    public static String generateMixed(int length) {
        return generate("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", length);
    }

    public static String generateString(int length) {
        return generate("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", length);
    }

    public static String generateDigits(int length) {
        return generate("0123456789", length);
    }


}
