package com.seidor.store.exception;


import com.seidor.store.dto.authDTOS.AuthResponseDTO;
import com.seidor.store.dto.exceptionDTOS.ExceptionDTO;
import com.seidor.store.exception.myExceptions.InsufficientStockException;
import com.seidor.store.exception.myExceptions.ResourceNotFoundException;
import com.seidor.store.exception.myExceptions.UnauthorizedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("Recurso no encontrado");
        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                "RESOURCE_NOT_FOUND",
                ex.getMessage()
        );
        ThreadContext.clearAll();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleValidationException(MethodArgumentNotValidException ex) {
        String validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        logger.info("Error de validación: {}", validationErrors);
        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.BAD_REQUEST,
                "Error de validación",
                "VALIDATION_ERROR",
                validationErrors
        );
        ThreadContext.clearAll();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDTO> handleDataIntegrity(DataIntegrityViolationException ex) {
        logger.error("Violación de integridad de datos");
        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.CONFLICT,
                "Violación de integridad de datos",
                "DATA_INTEGRITY",
                ex.getMostSpecificCause().getMessage()
        );
        ThreadContext.clearAll();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionDTO> handleBadCredentials(BadCredentialsException ex) {
        logger.info("Datos incorrectos en el logueo");
        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.UNAUTHORIZED,
                "Credenciales incorrectas",
                "BAD_CREDENTIALS",
                ex.getMessage()
        );
        ThreadContext.clearAll();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ExceptionDTO> handleInsufficientStock(InsufficientStockException ex) {
        logger.info("Stock insuficiente para el producto {}", ex.getProductId());
        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.BAD_REQUEST,
                "Stock insuficiente",
                "INSUFFICIENT_STOCK",
                String.format("Producto %d, solicitado %d, disponible %d",
                        ex.getProductId(), ex.getRequested(), ex.getAvailable())
        );
        ThreadContext.clearAll();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleGeneric(Exception ex) {
        logger.error("Error inesperado controlado", ex);
        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.BAD_REQUEST,
                "Error en la petición",
                "GENERIC_ERROR",
                ex.getMessage()
        );
        ThreadContext.clearAll();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}