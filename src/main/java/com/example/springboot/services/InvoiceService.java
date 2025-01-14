package com.example.springboot.services;

import com.example.springboot.models.InvoiceHeaderModel;
import com.example.springboot.models.ItemModel;
import com.example.springboot.repositories.InvoiceHeaderRepository;
import com.example.springboot.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceHeaderRepository invoiceHeaderRepository;

    @Autowired
    private ItemRepository itemRepository;

    public void updateTotalAmount(Long invoiceNumber){
        List<ItemModel> itemList = itemRepository.findByInvoiceNumber(invoiceHeaderRepository.findById(invoiceNumber).get());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for(ItemModel item : itemList){
            BigDecimal itemValueTotal = item.getProduct().getproductValue().multiply(item.getQty());
            totalAmount = itemValueTotal.add(totalAmount);
        }
        Optional<InvoiceHeaderModel> invoiceHeader = invoiceHeaderRepository.findById(invoiceNumber);
        if(invoiceHeader.isEmpty()) {
            return;
        }
        var invoiceModel = invoiceHeader.get();

        invoiceModel.setTotalAmount(totalAmount);
        invoiceHeaderRepository.save(invoiceModel);

    }
}
