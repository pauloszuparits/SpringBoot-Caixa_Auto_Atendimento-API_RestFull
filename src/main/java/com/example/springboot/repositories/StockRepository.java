package com.example.springboot.repositories;

import com.example.springboot.models.ProductModel;
import com.example.springboot.models.StockId;
import com.example.springboot.models.StockModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<StockModel, StockId> {
}
