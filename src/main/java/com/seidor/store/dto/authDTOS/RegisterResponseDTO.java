package com.seidor.store.dto.authDTOS;

import com.seidor.store.model.Role;
import com.seidor.store.model.Sell;
import com.seidor.store.model.User;

import java.util.HashSet;
import java.util.Set;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


}
