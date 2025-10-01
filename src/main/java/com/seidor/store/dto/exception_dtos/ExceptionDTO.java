package com.seidor.store.dto.exception_dtos;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionDTO {

    private int httpStatus;
    private String message;
    private String code;
    private String backendMessage;

    public ExceptionDTO(HttpStatus status, String message, String code, String backendMessage) {
        this.httpStatus = status.value();
        this.message = message;
        this.code = code;
        this.backendMessage = backendMessage;
    }

}
