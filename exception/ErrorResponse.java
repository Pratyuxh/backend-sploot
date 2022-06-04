package com.sploot.api.exception;

import com.sploot.api.constant.enums.ErrorMessage;
import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {

  public ErrorResponse(ErrorMessage message, List<String> details) {
    super();
    this.message = message;
    this.details = details;
  }
  private ErrorMessage message;
  private List<String> details;
}