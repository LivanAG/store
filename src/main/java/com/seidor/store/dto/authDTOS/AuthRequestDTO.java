package com.seidor.store.dto.authDTOS;

import jakarta.validation.constraints.NotBlank;

public class AuthRequestDTO {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El password es obligatorio")
    private String password;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
