package com.example.springboot.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleInvalidUUID(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleInvalidPathern(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();

        if (ex.getCause() instanceof InvalidFormatException cause) {
            cause.getPath().forEach(ref -> {
                errors.put(ref.getFieldName(), "O valor fornecido para o campo é inválido.");
            });
        } else {
            errors.put("error", "Erro de leitura da mensagem. Verifique os dados enviados.");
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<Object> handleUnknownField(UnrecognizedPropertyException ex) {
        String error = "Campo desconhecido: " + ex.getPropertyName();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();


        String message = "Erro de integridade de dados: Campos podem ser estar sendo referenciados em outra tabela";


        if (ex.getRootCause() != null) {
            String causeMessage = ex.getRootCause().getMessage();
            if (causeMessage.contains("Unique index or primary key violation")) {
                String fieldName = extractFieldName(causeMessage);
                message = "Erro de integridade de dados: o campo '" + fieldName + "' contém um valor que já existe.";
            }
        }
        errors.put("error", message);
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }


    private String extractFieldName(String causeMessage) {
        if (causeMessage.contains("(") && causeMessage.contains(")")) {
            int startIndex = causeMessage.indexOf('(') + 1;
            int endIndex = causeMessage.indexOf(')');
            return causeMessage.substring(startIndex, endIndex);
        }
        return "campo desconhecido";
    }

}
