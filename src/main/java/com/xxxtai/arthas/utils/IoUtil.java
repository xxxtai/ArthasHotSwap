package com.xxxtai.arthas.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class IoUtil {

    public static String getFile(ClassLoader classLoader, String filePath) throws Exception{
        InputStream in = classLoader.getResourceAsStream(filePath);
        if (in == null) {
            throw new IOException(filePath + " can not be found ");
        }
        InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }
}
