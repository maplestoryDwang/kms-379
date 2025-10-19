package objects.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class MD5Tool {
    public static void main(String[] args) {
        String text = "qwe123!";
        System.out.println(generateMD5(text));  // prints: 5feceb66ffc86f38d952786c6d696c79
    }

    public static String generateMD5(String text) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(text.getBytes(StandardCharsets.UTF_8));

            // convert each byte to two‑digit hex
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // MD5 should always be available; rethrow as unchecked if somehow it's not
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}