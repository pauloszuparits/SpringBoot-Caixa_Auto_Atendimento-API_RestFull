package com.example.springboot.models;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class StockId implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID idProduct;  // Chave primária da tabela ProductModel

    // Getters e Setters
    public UUID getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(UUID idProduct) {
        this.idProduct = idProduct;
    }

    // equals() e hashCode() para garantir a unicidade da chave composta
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockId stockId = (StockId) o;
        return idProduct.equals(stockId.idProduct);
    }

    @Override
    public int hashCode() {
        return idProduct.hashCode();
    }
}
