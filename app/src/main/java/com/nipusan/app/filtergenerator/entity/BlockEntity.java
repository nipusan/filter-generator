package com.nipusan.app.filtergenerator.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class BlockEntity {

    private String key;
    private Integer type;
    private String overallProject;
    private String name;
    private String description;
    private String idProject;
    private String owner;

    public BlockEntity(Integer type, String overallProject, String name, String description, String idProject, String owner) {
        this.type = type;
        this.overallProject = overallProject;
        this.name = name;
        this.description = description;
        this.idProject = idProject;
        this.owner = owner;
    }
}
