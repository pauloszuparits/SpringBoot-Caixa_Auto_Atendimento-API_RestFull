package com.example.springboot.models;

import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "TB_OPT")
@SequenceGenerator(name = "opt_seq", sequenceName = "OPT_SEQ", initialValue = 1000, allocationSize = 10)
public class OPTModel extends RepresentationModel<OPTModel> implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "opt_seq")
    private Long idOpt;

    private String type;
    private String updateStock;
    private String active;

    public Long getIdOpt() {
        return idOpt;
    }

    public void setIdOpt(Long idOpt) {
        this.idOpt = idOpt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdateStock() {
        return updateStock;
    }

    public void setUpdateStock(String updateStock) {
        this.updateStock = updateStock;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
