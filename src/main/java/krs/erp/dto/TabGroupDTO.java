package krs.erp.dto;

import java.util.List;
import krs.erp.model.ErpEntity;

public class TabGroupDTO {
    private Long id;
    private String name;
    private String code;
    private String icon;
    private Integer sequence;
    private String description;
    private List<ErpEntity> entities;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ErpEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<ErpEntity> entities) {
        this.entities = entities;
    }
}
