package com.seidor.store.exception;


import com.seidor.store.dto.authDTOS.AuthResponseDTO;
import com.seidor.store.dto.exceptionDTOS.ExceptionDTO;
import com.seidor.store.exception.myExceptions.InsufficientStockException;
import com.seidor.store.exception.myExceptions.InvalidRoleException;
import com.seidor.store.exception.myExceptions.ResourceNotFoundException;
import com.seidor.store.exception.myExceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Arrays;
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

        // Obtener la clase donde se lanzó la excepción
        //En esta accedo al servicio para identificar el archivo log
        // porque yo lanzo las excepciones en cada servicio manualmente
        String originClass = ex.getStackTrace()[0].getClassName();

        Logger logger;

        if(originClass.equals("com.seidor.store.service.SellService")) {
            logger = LogManager.getLogger("sellLogger");
        }
        else if(originClass.equals("com.seidor.store.service.ProductService")) {
            logger = LogManager.getLogger("productLogger");
        }
        else{
            logger = LogManager.getLogger("authLogger");
        }
        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                "RESOURCE_NOT_FOUND",
                ex.getMessage()
        );

        logger.error("Message: "+error.getMessage()+"/ Backend Message:"+error.getBackendMessage()+"/ Code:"+error.getCode() , originClass);
        ThreadContext.clearAll();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        Logger logger;
        String originClass = ex.getStackTrace()[0].getClassName();
        //En esta accedo al path y no al servicio porque, estas excepciones se lanzan internamente
        //la unica forma de acceder a donde esta siendo lanzada era por el dto, por el controller o por el path
        if(path.equals("/sell")) {
            logger = LogManager.getLogger("sellLogger");
        }
        else if(path.equals("/product")) {
            logger = LogManager.getLogger("productLogger");
        }
        else{
            logger = LogManager.getLogger("authLogger");
        }

        MethodArgumentNotValidException a = ex;

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
        final Logger logger = LogManager.getLogger("authLogger");

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

        final Logger logger = LogManager.getLogger("authLogger");

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

        final Logger logger = LogManager.getLogger("com.seidor.store.service.SellService");

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



    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ExceptionDTO> handleInvalidRoleException(InvalidRoleException ex) {
        final Logger logger = LogManager.getLogger("authLogger");

        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.BAD_REQUEST,
                "Error en los datos enviados",
                "INVALID_ROLE",
                ex.getMessage()
        );

        logger.error("Error de rol inválido: {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }



}