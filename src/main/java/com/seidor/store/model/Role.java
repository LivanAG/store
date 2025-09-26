package com.seidor.store.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.seidor.store.exception.myExceptions.InvalidRoleException;

public enum Role {
    ADMIN,
    CLIENT;

    @JsonCreator
    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Rol inválido. Solo se permiten ADMIN o CLIENT");
        }
    }
}
