package com.example.springboot.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "TB_INVHEADER")
public class InvoiceHeaderModel extends RepresentationModel<InvoiceHeaderModel> implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "customer_cpf", referencedColumnName = "cpf")
    private CustomerModel customer;

    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "idPmt")
    private PaymentMethodModel payment;

    @ManyToOne
    @JoinColumn(name = "opt_id", referencedColumnName = "idOpt")
    private OPTModel opt;

    private String confirmed;

    private BigDecimal totalAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(updatable = false)
    private Timestamp regDate;

    @PrePersist
    protected void onCreate() {
        this.regDate = Timestamp.from(Instant.now());
        this.confirmed = "N";
        this.totalAmount = BigDecimal.ZERO;
    }

    public Long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }

    public PaymentMethodModel getPayment() {
        return payment;
    }

    public void setPayment(PaymentMethodModel payment) {
        this.payment = payment;
    }

    public OPTModel getOpt() {
        return opt;
    }

    public void setOpt(OPTModel opt) {
        this.opt = opt;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getRegDate() {
        return regDate;
    }

    public void setRegDate(Timestamp regDate) {
        this.regDate = regDate;
    }
}
