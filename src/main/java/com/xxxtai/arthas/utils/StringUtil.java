package com.xxxtai.arthas.utils;

public class StringUtil {
    private static final char[] bcdLookup = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String bytes2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        for (byte aByte : bytes) {
            sb.append(bcdLookup[(aByte >>> 4) & 0x0f]);
            sb.append(bcdLookup[aByte & 0x0f]);
        }

        return sb.toString();
    }

    public static byte[] hex2Bytes(String s) {
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
