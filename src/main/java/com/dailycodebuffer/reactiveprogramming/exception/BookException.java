package com.dailycodebuffer.reactiveprogramming.exception;



//Custom Exception para manejar errores
public class BookException extends RuntimeException{
    public BookException(String message) {
        super(message);
    }
}
