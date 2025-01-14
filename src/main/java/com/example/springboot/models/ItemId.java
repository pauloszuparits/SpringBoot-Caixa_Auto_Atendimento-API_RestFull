package com.example.springboot.models;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ItemId implements Serializable {
    private Long invoiceNumber;
    private Long sequential;

    public ItemId() {}

    public ItemId(Long invoiceNumber, Long sequential) {
        this.invoiceNumber = invoiceNumber;
        this.sequential = sequential;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemId itemID = (ItemId) o;
        return Objects.equals(invoiceNumber, itemID.invoiceNumber) && Objects.equals(sequential, itemID.sequential);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceNumber, sequential);
    }
}
