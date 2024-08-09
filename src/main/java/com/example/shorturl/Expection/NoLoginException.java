package com.example.shorturl.Expection;

public class NoLoginException extends RuntimeException{
    public NoLoginException(String message) {
        super(message);
    }
}
