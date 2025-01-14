package com.example.springboot.repositories;

import com.example.springboot.models.InvoiceHeaderModel;
import com.example.springboot.models.ItemId;
import com.example.springboot.models.ItemModel;
import com.example.springboot.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<ItemModel, ItemId> {
    List<ItemModel> findByInvoiceNumber(InvoiceHeaderModel invoiceNumber);
}
