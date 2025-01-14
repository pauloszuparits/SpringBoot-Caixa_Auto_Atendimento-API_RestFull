package com.example.springboot.models;

import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "TB_ITEMS",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sequential", "invoiceNumber"}))
@IdClass(ItemId.class)
public class ItemModel extends RepresentationModel<ItemModel> implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sequential;

    @Id
    @ManyToOne
    @JoinColumn(name = "invoiceNumber", referencedColumnName = "invoiceNumber")
    private InvoiceHeaderModel invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "idProduct", referencedColumnName = "idProduct")
    private ProductModel idProduct;

    private BigDecimal qty;

    public Long getSequential() {
        return sequential;
    }


    public InvoiceHeaderModel getInvoiceHeader() {
        return invoiceNumber;
    }

    public void setInvoiceHeader(InvoiceHeaderModel invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public ProductModel getProduct() {
        return idProduct;
    }

    public void setProduct(ProductModel idProduct) {
        this.idProduct = idProduct;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }
}
