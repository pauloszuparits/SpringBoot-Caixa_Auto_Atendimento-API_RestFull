package com.example.springboot.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "TB_PMT", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type", "cardBrand"})
})
public class PaymentMethodModel extends RepresentationModel<PaymentMethodModel> implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPmt;

    private String type;
    private String cardBrand;

    public Long getIdPmt() {
        return idPmt;
    }

    public void setIdPmt(Long idPmt) {
        this.idPmt = idPmt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }
}
