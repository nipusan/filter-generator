package com.nipusan.app.filtergenerator.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FieldEntity {

    private String key;
    private Integer type;
    private String name;
    private String description;
    private String value;
    private String idBlock;
    private String owner;

    public FieldEntity(Integer type, String name, String description, String value, String idBlock, String owner) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.value = value;
        this.idBlock = idBlock;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
