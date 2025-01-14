package com.example.springboot.models;

import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;


@Entity
@Table(name = "TB_STOCK")
public class StockModel extends RepresentationModel<StockModel> implements Serializable {
    private static final long serialVersionUID = 1;

    @EmbeddedId
    private StockId stockId;

    private Integer qtyInStock;

    public StockModel() {
        this.qtyInStock = 0;
    }

    public Integer getQtyInStock() {
        return qtyInStock;
    }

    public void setQtyInStock(Integer qtyInStock) {
        this.qtyInStock = qtyInStock;
    }

    public StockId getStockId() {
        return stockId;
    }

    public void setStockId(StockId stockId) {
        this.stockId = stockId;
    }
}
