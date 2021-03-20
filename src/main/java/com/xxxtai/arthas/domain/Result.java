package com.xxxtai.arthas.domain;

/**
 * Copyright (c) 2020, 2021, xxxtai. All rights reserved.
 *
 * @author xxxtai
 */
public class Result<T> {

    private boolean success;

    private T value;

    private String errorMsg;

    private String errorCode;

    public static <T> Result<T> buildSuccessResult(T value) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setValue(value);
        return result;
    }

    public static <T> Result<T> buildErrorResult(String errorMsg) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setErrorMsg(errorMsg);
        return result;
    }

    public static <T> Result<T> buildErrorResult(String errorCode, String errorMsg) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMsg(errorMsg);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
