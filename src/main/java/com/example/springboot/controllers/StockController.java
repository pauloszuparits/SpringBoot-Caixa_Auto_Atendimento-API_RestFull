package com.example.springboot.controllers;

import com.example.springboot.dtos.StockRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.models.StockId;
import com.example.springboot.models.StockModel;
import com.example.springboot.repositories.ProductRepository;
import com.example.springboot.repositories.StockRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Estoque")
public class StockController {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ProductRepository productRepository;

    @Operation(summary = "Realiza o cadastro do estoque do produto"
            , method = "POST"
            , description = "O estoque do produto deve possuir um produto como sua chaves primária")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estoque cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"idProduct\": \"4ad20f33-5943-4d90-a631-f2576058395c\"}")
                    )),
            @ApiResponse(responseCode = "409", description = "Estoque já cadastrado", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"idProduct\": 1234}")
                    )),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PostMapping(value = "/stock", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> saveStock(@RequestBody @Valid StockRecordDto stockRecordDto) {

        Optional<ProductModel> product = productRepository.findById(stockRecordDto.idProduct());
        if(product.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }

        StockId stockId = new StockId();
        stockId.setIdProduct(product.get().getIdProduct());

        Optional<StockModel> existingStock = stockRepository.findById(stockId);
        if (existingStock.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Stock already exists for this Product");
        }

        var stockModel = new StockModel();
        stockModel.setStockId(stockId);

        return ResponseEntity.status(HttpStatus.CREATED).body(stockRepository.save(stockModel));
    }

    @Operation(summary = "Busca todos os estoques cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoques listados com sucesso", content = @Content()),
            @ApiResponse(responseCode = "204", description = "Sem estoques para listar", content = @Content())
    })
    @GetMapping("/stock")
    public ResponseEntity<List<StockModel>> getAllStock(){
        List<StockModel> stockList= stockRepository.findAll();
        if(!stockList.isEmpty()){
            for(StockModel stock : stockList){
                UUID productId = stock.getStockId().getIdProduct();

                stock.add(linkTo(methodOn(StockController.class).getOneStock(productId)).withSelfRel());
            }
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(stockList);
    }

    @Operation(summary = "Busca um estoque a partir de um UID do produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque listado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Formato UUID inválido", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content())
    })
    @GetMapping("/stock/{idProduct}")
    public ResponseEntity<Object> getOneStock(@PathVariable(value = "idProduct") UUID idProduct){
        Optional<ProductModel> product = productRepository.findById(idProduct);
        if(product.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }

        StockId stockId = new StockId();
        stockId.setIdProduct(idProduct);

        Optional<StockModel> stock = stockRepository.findById(stockId);
        if (stock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stock not found.");
        }

        stock.get().add(linkTo(methodOn(StockController.class).getAllStock()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(stock.get());
    }

    @Operation(summary = "Deleta um estoque a partir de um id do produto", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque deletado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Formato UUID inválido", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content())
    })
    @DeleteMapping("/stock/{idProduct}")
    public ResponseEntity<Object> deleteStock(@PathVariable(value = "idProduct") UUID idProduct) {
        Optional<ProductModel> product = productRepository.findById(idProduct);
        if(product.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }

        StockId stockId = new StockId();
        stockId.setIdProduct(idProduct);

        Optional<StockModel> stock = stockRepository.findById(stockId);
        if (stock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stock not found.");
        }
        stockRepository.delete(stock.get());
        return ResponseEntity.status(HttpStatus.OK).body("Stock delete successfully");
    }

    @PutMapping(value = "/stock/{idProduct}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateStock(@RequestBody @Valid StockRecordDto stockRecordDto
                                            ,@PathVariable(value = "idProduct") UUID idProduct) {

        Optional<ProductModel> product = productRepository.findById(idProduct);
        if(product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }

        StockId stockId = new StockId();
        stockId.setIdProduct(idProduct);

        Optional<StockModel> existingStock = stockRepository.findById(stockId);
        if (existingStock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stock Not Found for Product");
        }

        StockModel stockModel = existingStock.get();
        stockModel.setQtyInStock(stockRecordDto.qtyInStock());

        stockRepository.save(stockModel);

        return ResponseEntity.status(HttpStatus.OK).body("Stock updated successfully");
    }

}
