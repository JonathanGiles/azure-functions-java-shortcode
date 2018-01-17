package net.jonathangiles.azure.shorturl.util;

import java.util.Random;

public class Utils {

    public static String generateKey(int length) {
        System.out.println("Generating key of length " + length);
        String key = "";
        Random random = new Random();

        for (int i = 1; i <= length; i++) {
            // TODO start using characters a-Z as well as ints!
            key += random.nextInt(10);
        }

        System.out.println("Returning key " + key);

        return key;
    }
}
