package com.example.ManagerApp.app.rest.error;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.ManagerApp.service.dto.response.Response;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Response<Void>> handleAllException(Exception ex) {
        Response<Void> res = new Response<>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setError("Internal Server...");
        res.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(value = NotFoundAlertException.class)
    public ResponseEntity<Response<Void>> handleNotFoundException(NotFoundAlertException ex) {
        Response<Void> res = new Response<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("Resource not found...");
        res.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(value = BadRequestAlertException.class)
    public ResponseEntity<Response<Void>> handleBadRequestException(BadRequestAlertException ex) {
        Response<Void> res = new Response<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Bad request...");
        res.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<Response<Void>> handleAccessDeniedException(
            AccessDeniedException ex) {

        Response<Void> res = new Response<>();

        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setError("Forbidden");
        res.setMessage("You do not have permission");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    );
                });

        Response<Map<String, String>> res = new Response<>();

        res.setStatusCode(HttpStatus.BAD_REQUEST.value());

        res.setError("Validation failed");

        res.setMessage("Invalid request");

        res.setData(errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(res);
    }

    @ExceptionHandler(value = AccountLockedException.class)
    public ResponseEntity<Response<Void>> handleAccountLockedException(
            AccountLockedException ex) {

        Response<Void> res = new Response<>();

        res.setStatusCode(HttpStatus.LOCKED.value()); // 423
        res.setError("Account Locked");
        res.setMessage(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(res);
    }
}
