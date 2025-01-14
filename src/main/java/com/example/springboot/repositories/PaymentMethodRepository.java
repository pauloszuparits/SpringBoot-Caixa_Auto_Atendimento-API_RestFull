package com.example.springboot.repositories;

import com.example.springboot.models.CustomerModel;
import com.example.springboot.models.PaymentMethodModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodModel, Long> {

}
