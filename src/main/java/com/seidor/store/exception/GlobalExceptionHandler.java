package com.seidor.store.exception;


import com.seidor.store.dto.exception_dtos.ExceptionDTO;
import com.seidor.store.exception.my_exceptions.InsufficientStockException;
import com.seidor.store.exception.my_exceptions.InvalidRoleException;
import com.seidor.store.exception.my_exceptions.ResourceNotFoundException;
import com.seidor.store.utils.loggers.AppLogger;
import com.seidor.store.utils.loggers.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    private ResponseEntity<ExceptionDTO> buildErrorResponse (AppLogger logger,HttpStatus status,String message,String code,String backendMessage){
        ExceptionDTO error = new ExceptionDTO(
                status,
                message,
                code,
                backendMessage
        );

        logger.logError("Message: "+error.getMessage()+"/ Backend Message:"+error.getBackendMessage()+"/ Code:"+error.getCode());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleResourceNotFound(ResourceNotFoundException ex) {

        // Obtener la clase donde se lanzó la excepción
        //En esta accedo al servicio para identificar el archivo log
        // porque yo lanzo las excepciones en cada servicio manualmente
        String originClass = ex.getStackTrace()[0].getClassName();

        AppLogger logger = LoggerFactory.getLogger(originClass, null);
        return this.buildErrorResponse(logger,HttpStatus.NOT_FOUND,"Recurso no encontrado","RESOURCE_NOT_FOUND",ex.getMessage());


    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        //En esta accedo al path y no al servicio porque, estas excepciones se lanzan internamente
        //la unica forma de acceder a donde esta siendo lanzada era por el dto, por el controller o por el path
        String path = request != null ? request.getRequestURI() : null;
        AppLogger logger = LoggerFactory.getLogger(null, path);

        String validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return this.buildErrorResponse(logger,HttpStatus.BAD_REQUEST,"Error de validación","VALIDATION_ERROR",validationErrors);
    }



    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDTO> handleDataIntegrity(DataIntegrityViolationException ex) {
        AppLogger logger = LoggerFactory.getLogger(null, "/auth");
        return this.buildErrorResponse(logger,HttpStatus.CONFLICT,"Violación de integridad de datos","DATA_INTEGRITY",ex.getMostSpecificCause().getMessage());
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionDTO> handleBadCredentials(BadCredentialsException ex) {

        AppLogger logger = LoggerFactory.getLogger(null, "/auth");

        return this.buildErrorResponse(logger,HttpStatus.UNAUTHORIZED,"Credenciales incorrectas","BAD_CREDENTIALS",ex.getMessage());

    }


    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ExceptionDTO> handleInsufficientStock(InsufficientStockException ex) {

        String originClass = ex.getStackTrace()[0].getClassName();
        AppLogger logger = LoggerFactory.getLogger(originClass, null);
        return this.buildErrorResponse(logger,HttpStatus.BAD_REQUEST,"Stock insuficiente","INSUFFICIENT_STOCK",String.format("Producto %d, solicitado %d, disponible %d",
                ex.getProductId(), ex.getRequested(), ex.getAvailable()));

    }



    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ExceptionDTO> handleInvalidRoleException(InvalidRoleException ex) {
        AppLogger logger = LoggerFactory.getLogger(null, "/auth");
        return this.buildErrorResponse(logger,HttpStatus.BAD_REQUEST,"Error en los datos enviados","INVALID_ROLE",ex.getMessage());
    }



}