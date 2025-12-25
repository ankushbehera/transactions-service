package com.pismo.transactions.exception;

public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException(String message){
        super(message);
    }
}