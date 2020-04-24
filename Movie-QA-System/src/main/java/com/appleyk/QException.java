package com.appleyk;


public class QException extends Exception {

    private final RetCode code;

    public QException(RetCode code) {
        this.code = code;
    }

    public RetCode getCode() {
        return code;
    }

}