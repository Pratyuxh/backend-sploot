package com.sploot.api.constant.enums;

import org.springframework.http.HttpStatus;

public enum ErrorMessage {
    RESOURCE_NOT_FOUND, INVALID_REQUEST, INTERNAL_SERVER_ERROR, BAD_REQUEST;

    public static ErrorMessage getErrorFromStatusCode(HttpStatus code){
       if(code.equals(HttpStatus.BAD_REQUEST)){
           return ErrorMessage.BAD_REQUEST;
       } else if(code.equals(HttpStatus.NOT_FOUND)){
           return ErrorMessage.RESOURCE_NOT_FOUND;
       } else{
           return ErrorMessage.INTERNAL_SERVER_ERROR;
       }
    }
}