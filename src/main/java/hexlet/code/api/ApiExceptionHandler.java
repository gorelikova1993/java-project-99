package hexlet.code.api;

import hexlet.code.exception.BadRequestException;
import hexlet.code.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class ApiExceptionHandler {
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex,
                                                                HttpServletRequest req) {
        return json(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }
    
    // Валидация @Valid — соберём сообщения полей
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                HttpServletRequest req) {
        var errors = new ArrayList<String>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.add(e.getField() + ": " + e.getDefaultMessage()));
        var msg = String.join("; ", errors);
        return json(HttpStatus.BAD_REQUEST, msg.isEmpty() ? "Validation error" : msg, req.getRequestURI());
    }
    
    // 404 из твоих ResourceNotFoundException и т.п.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex,
                                                              HttpServletRequest req) {
        return json(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }
    
    // Фоллбек: чтобы RA не видел пустой ответ и не показывал «Server communication error»
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(Exception ex,
                                                         HttpServletRequest req) {
        return json(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error: " + ex.getMessage(), req.getRequestURI());
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleRse(ResponseStatusException ex,
                                                         HttpServletRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", Optional.ofNullable(ex.getReason()).orElse(""));
        body.put("status", ex.getStatusCode().value());
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
    
    
    private ResponseEntity<Map<String, Object>> json(HttpStatus status, String message, String path) {
        var body = new LinkedHashMap<String, Object>();
        body.put("message", message);
        body.put("status", status.value());
        body.put("path", path);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
