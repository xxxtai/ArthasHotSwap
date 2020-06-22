package com.xxxtai.arthas.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.xxxtai.arthas.dialog.MyToolWindow;

public class IoUtil {

    public static String getResourceFile(ClassLoader classLoader, String filePath) throws Exception {
        try (InputStream in = classLoader.getResourceAsStream(filePath)) {
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

    public static byte[] getTargetClass(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("the file of " + filePath + " does not exist ");
        }
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) file.length());
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len;
            while ((len = bufferedInputStream.read(buffer, 0, buf_size)) > 0) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static String printStackTrace(Throwable t) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        t.printStackTrace(printStream);
        try {
            printStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return new String(byteArrayOutputStream.toByteArray());
    }
}
