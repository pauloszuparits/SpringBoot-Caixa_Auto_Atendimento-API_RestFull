package com.example.springboot.repositories;

import com.example.springboot.models.CustomerModel;
import com.example.springboot.models.InvoiceHeaderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceHeaderRepository extends JpaRepository<InvoiceHeaderModel, Long> {
}
