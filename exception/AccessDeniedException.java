package com.sploot.api.exception;

import lombok.Data;

@Data
public class AccessDeniedException extends RuntimeException{


    public AccessDeniedException() {
        super("User can only manage his/her records");
    }
    public AccessDeniedException(String msg) {
        super(msg);
    }

}
