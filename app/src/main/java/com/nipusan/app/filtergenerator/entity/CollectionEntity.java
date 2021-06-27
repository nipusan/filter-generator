package com.nipusan.app.filtergenerator.entity;

public class CollectionEntity {

    private String key;
    private String name;
    private String description;
    private String owner;

    public CollectionEntity() {
    }

    public CollectionEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CollectionEntity(String name, String description, String owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "CollectionEntity{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", owner='" + owner + '\'' +
                '}';
    }
}
