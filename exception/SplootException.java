package com.sploot.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SplootException extends RuntimeException {

  private HttpStatus code;
  private String message;

  public SplootException(String message, HttpStatus code) {
    this.code = code;
    this.message = message;
  }
}
