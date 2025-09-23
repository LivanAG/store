package com.seidor.store.dto;

import com.seidor.store.model.Storage;

public class ProductRequestDTO {

    private String name;
    private String description;
    private Storage storage;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}
