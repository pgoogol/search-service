package com.pgoogol.searchservice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {

    private static Logger log = LogManager.getLogger(RequestExceptionHandler.class);

    @ExceptionHandler({IllegalArgumentException.class})
    protected ResponseEntity handleIllegalArgException(IllegalArgumentException exception, WebRequest request) {
        return handleBadRequest(exception, request);
    }

    @ExceptionHandler({IOException.class})
    protected ResponseEntity handleIOException(IllegalArgumentException exception, WebRequest request) {
        return handleServerErrorRequest(exception, request);
    }

    @ExceptionHandler({Throwable.class})
    protected ResponseEntity handleAllException(Throwable exception, WebRequest request) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorMap(HttpStatus.INTERNAL_SERVER_ERROR, request, exception.getMessage()));
    }

    @Override
    protected ResponseEntity handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(x -> "'" + prepareFieldOrObjectName(x) + "' field is incorrect: " + x.getDefaultMessage())
                .collect(Collectors.toList());

        return new ResponseEntity<>(buildErrorMap(status, request, errors), headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(x -> "'" + prepareFieldOrObjectName(x) + "' field is incorrect: " + x.getDefaultMessage())
                .collect(Collectors.toList());


        return new ResponseEntity<>(buildErrorMap(status, request, errors), headers, status);
    }

    private String prepareFieldOrObjectName(ObjectError objectError) {
        if (objectError instanceof FieldError) {
            return ((FieldError) objectError).getField();
        } else {
            return objectError.getObjectName();
        }
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException exception,
                                                                          HttpHeaders headers,
                                                                          HttpStatus status,
                                                                          WebRequest request) {
        return handleBadRequest(exception, request);
    }

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        return handleBadRequest(exception, request);
    }

    private ResponseEntity<Object> handleBadRequest(Exception exception, WebRequest request) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorMap(HttpStatus.BAD_REQUEST, request, exception.getMessage()));
    }

    private ResponseEntity<Object> handleServerErrorRequest(Exception exception, WebRequest request) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorMap(HttpStatus.INTERNAL_SERVER_ERROR, request, exception.getMessage()));
    }

    private Map<String, Object> buildErrorMap(HttpStatus status, WebRequest request, String error) {
        Map<String, Object> body = buildErrorMap(status, (ServletWebRequest) request);
        body.put("message", error);
        return body;
    }

    private Map<String, Object> buildErrorMap(HttpStatus status, WebRequest request, List<String> errors) {
        Map<String, Object> body = buildErrorMap(status, (ServletWebRequest) request);
        body.put("message", errors);
        return body;
    }

    private Map<String, Object> buildErrorMap(HttpStatus status, ServletWebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("path", request.getRequest().getRequestURI());

        return body;
    }
}
