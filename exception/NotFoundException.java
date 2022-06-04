package com.sploot.api.exception;


public class NotFoundException extends RuntimeException{

    public NotFoundException() {
        super("Required records not found");
    }
    public NotFoundException(String msg) {
        super(msg);
    }

}
