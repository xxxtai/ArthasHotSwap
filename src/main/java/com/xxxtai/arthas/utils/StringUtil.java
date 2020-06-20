package com.xxxtai.arthas.utils;

public class StringUtil {
    private static final char[] bcdLookup = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 将字节数组转化为十六进制字符串表示
     *
     * @param bytes
     * @return
     */
    public static final String bytes2Hex(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            sb.append(bcdLookup[(bytes[i] >>> 4) & 0x0f]);
            sb.append(bcdLookup[bytes[i] & 0x0f]);
        }

        return sb.toString();
    }

    /**
     * 将十六进制字符串转化为字节数组表示
     *
     * @param s
     * @return
     */
    public static final byte[] hex2Bytes(String s) {
        byte[] bytes = new byte[s.length() / 2];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }

        return bytes;
    }

}
