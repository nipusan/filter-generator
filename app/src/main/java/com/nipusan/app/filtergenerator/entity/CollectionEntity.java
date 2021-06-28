package com.nipusan.app.filtergenerator.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CollectionEntity {

    private String key;
    private String name;
    private String description;
    private String owner;

    public CollectionEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CollectionEntity(String name, String description, String owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
