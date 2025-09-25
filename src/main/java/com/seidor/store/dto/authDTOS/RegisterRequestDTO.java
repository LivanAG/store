package com.seidor.store.dto.authDTOS;

import com.seidor.store.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequestDTO {

    @NotBlank(message = "El username es obligatorio")
    private String username;
    @NotBlank(message = "El password es obligatorio")
    private String password;

    private String firstName;
    private String lastName;

    private String dni;

    private Integer phone;

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotNull(message = "El role es obligatorio")
    private Role role;

}
