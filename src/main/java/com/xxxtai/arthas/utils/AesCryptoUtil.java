package com.xxxtai.arthas.utils;

import java.io.UnsupportedEncodingException;
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
            return Base64.getEncoder().encodeToString(encrypted) + "\n";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] generalRandomBytes(int size) {
        Random random = new Random();
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) random.nextInt(125);
        }
        return bytes;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String encodeString = encrypt("你好，hello moda, 测试中文 just do it，干你", "12230000000000000000000440000000", "98230000000000000000000000000000");
        System.out.println(encodeString);
    }
}