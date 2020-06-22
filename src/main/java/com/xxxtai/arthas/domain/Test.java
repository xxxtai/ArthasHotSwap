package com.xxxtai.arthas.domain;

public class Test {

    public static void main(String[] args) {
        String str = "\njjj/fd+/";
        System.out.println(str.replaceAll("[\n/+]", "x"));
    }
}
