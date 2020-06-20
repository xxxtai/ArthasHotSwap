package com.xxxtai.arthas.domain;

public class EncryptInfo {

    private byte[] originBytes;

    private String encryptContent;

    private String key;

    private String iv;

    public String getEncryptContent() {
        return encryptContent;
    }

    public void setEncryptContent(String encryptContent) {
        this.encryptContent = encryptContent;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public byte[] getOriginBytes() {
        return originBytes;
    }

    public void setOriginBytes(byte[] originBytes) {
        this.originBytes = originBytes;
    }
}
