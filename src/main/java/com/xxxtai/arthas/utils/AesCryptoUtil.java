package com.xxxtai.arthas.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesCryptoUtil {
    public static String encrypt(String sSrc, String sKey, String sIv) {
        try {
            byte[] keyBytes = StringUtil.hex2Bytes(sKey);
            byte[] ivBytes = StringUtil.hex2Bytes(sIv);
            return encrypt(sSrc.getBytes(StandardCharsets.UTF_8), keyBytes, ivBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encrypt(byte[] contentBytes, byte[] keyBytes, byte[] ivBytes) {
        try {
            SecretKeySpec sKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);
            byte[] encrypted = cipher.doFinal(contentBytes);
            String origin = Base64.getEncoder().encodeToString(encrypted);
            return new String(formatWithLineBreak(origin.getBytes(), 64));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] formatWithLineBreak(byte[] origin, int lineLength) {
        byte lineBreak = '\n';
        int lineSum = origin.length / lineLength;
        int mod = origin.length % lineLength;
        lineSum = mod == 0 ? lineSum : lineSum + 1;
        byte[] bytesWithLineBreak = new byte[origin.length + lineSum];
        int lineCount = 0;
        for (int i = 0; i < bytesWithLineBreak.length; i++) {
            boolean isEnd = false;
            if (i == bytesWithLineBreak.length - 1 ) {
                isEnd = true;
            }

            if (((lineCount + 1) * (lineLength + 1) - 1) == i) {
                isEnd = i != lineCount;
            }
            if (isEnd) {
                bytesWithLineBreak[i] = lineBreak;
                lineCount++;
                continue;
            }
            bytesWithLineBreak[i] = origin[i - lineCount];
        }
        return bytesWithLineBreak;
    }

    public static byte[] generalRandomBytes(int size) {
        Random random = new Random();
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) random.nextInt(125);
        }
        return bytes;
    }
}
