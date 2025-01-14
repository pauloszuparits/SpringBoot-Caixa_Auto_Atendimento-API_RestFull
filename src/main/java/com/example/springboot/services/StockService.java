package com.example.springboot.services;

import com.example.springboot.models.*;
import com.example.springboot.repositories.InvoiceHeaderRepository;
import com.example.springboot.repositories.ItemRepository;
import com.example.springboot.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StockService {

    @Autowired
    private InvoiceHeaderRepository invoiceHeaderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private StockRepository stockRepository;

    public void updateStock(Long invoiceNumber) throws Exception {
        Optional<InvoiceHeaderModel> invoiceHeader = invoiceHeaderRepository.findById(invoiceNumber);
        if(invoiceHeader.isEmpty()) {
            throw new Exception("Invoice Header does not exist");
        }
        String updateStock = invoiceHeader.get().getOpt().getUpdateStock();
        if(updateStock.equals("N")){
            return;
        }
        String type = invoiceHeader.get().getOpt().getType();

        List<ItemModel> itemList = itemRepository.findByInvoiceNumber(invoiceHeaderRepository.findById(invoiceNumber).get());

        for(ItemModel item : itemList){
            BigDecimal qty = item.getQty();

            StockId stockId = new StockId();
            stockId.setIdProduct(item.getProduct().getIdProduct());

            Optional<StockModel> stockModel = stockRepository.findById(stockId);
            if(stockModel.isEmpty()){
                throw new Exception("Stock does not exist for the product " + item.getProduct().getIdProduct());
            }
            BigDecimal qtyInStock = new BigDecimal(stockModel.get().getQtyInStock());
            if(type.equals("P")){
                stockModel.get().setQtyInStock(qtyInStock.add(qty).intValue());
            }else{
                if(qty.compareTo(qtyInStock)<0){
                    stockModel.get().setQtyInStock(qtyInStock.subtract(qty).intValue());
                }else{
                    throw new Exception("Not enough stock available for the product " + item.getProduct().getIdProduct());
                }
            }
        }
    }

    public void createStock(UUID idProduct){
        StockModel stockModel = new StockModel();
        StockId stockId = new StockId();
        stockId.setIdProduct(idProduct);

        stockModel.setStockId(stockId);
        stockModel.setQtyInStock(0);

        stockRepository.save(stockModel);
    }
}
