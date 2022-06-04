package com.sploot.api.exception;

import com.sploot.api.constant.enums.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllException(Exception ex) {
    List<String> details = new ArrayList<>();
    details.add(ex.getLocalizedMessage());
    log.error("Exception occurred ", ex);
    ErrorResponse error = new ErrorResponse(ErrorMessage.INTERNAL_SERVER_ERROR, details);
    return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(SplootException.class)
  public ResponseEntity<Object> handleSplootException(SplootException ex) {
    List<String> details = new ArrayList<>();
    details.add(ex.getMessage());
    ErrorResponse error = new ErrorResponse(ErrorMessage.getErrorFromStatusCode(ex.getCode()), details);
    return new ResponseEntity(error, ex.getCode());
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    List<String> details = new ArrayList<>();
    for (ObjectError error : ex.getBindingResult().getAllErrors()) {
      details.add(error.getDefaultMessage());
    }
    ErrorResponse error = new ErrorResponse(ErrorMessage.INVALID_REQUEST, details);
    return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
  }
}