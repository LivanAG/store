package com.seidor.store.dto.auth_dtos;

import com.seidor.store.model.Role;
import com.seidor.store.model.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterResponseDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String dni;
    private Integer phone;
    private String email;
    private Role role;


    public RegisterResponseDTO(User user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.dni = user.getDni();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.role = user.getRole();
    }


}
