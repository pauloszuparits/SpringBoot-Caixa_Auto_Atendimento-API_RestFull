package com.example.springboot.controllers;

import com.example.springboot.dtos.ItemRecordDto;
import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.*;
import com.example.springboot.repositories.*;
import com.example.springboot.services.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Item")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InvoiceHeaderRepository invoiceHeaderRepository;


    @Autowired
    private InvoiceService invoiceService;



    @Operation(summary = "Realiza o cadastro do item do pedido"
            , method = "POST"
            , description = "O item deve possuir um cabeçalho da nota, um produto e uma quantidade deste item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"invoiceNumber\": 7, \"barCode\": 123456, \"qty\": 10 }")
                    )),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"invoiceNumber\": \"7\", \"barCode\": \"abs\" }")
                    )),
            @ApiResponse(responseCode = "404", description = "Produto/Cabecalho da nota não encontrados", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PostMapping(value = "/invoice/item", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> saveItem(@RequestBody @Valid ItemRecordDto itemRecordDto) {

        Optional<ProductModel> product = productRepository.findByBarCode(itemRecordDto.barCode());
        if(product.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }else{
            if(product.get().getActive().equals("N")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Product is not active");
            }
        }
        Optional<InvoiceHeaderModel> invoiceHeader = invoiceHeaderRepository.findById(itemRecordDto.invoiceNumber());
        if(invoiceHeader.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice Header Not Found");
        }else{
            if(invoiceHeader.get().getConfirmed().equals("S")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Invoice already confirmed");
            }
        }

        var itemModel = new ItemModel();

        itemModel.setProduct(product.get());
        itemModel.setInvoiceHeader(invoiceHeader.get());


        BeanUtils.copyProperties(itemRecordDto, itemModel);

        ItemModel savedItem = itemRepository.save(itemModel);

        invoiceService.updateTotalAmount(itemRecordDto.invoiceNumber());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    @Operation(summary = "Busca todos os itens de todos os pedidos já realizados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itens listados com sucesso", content = @Content()),
            @ApiResponse(responseCode = "204", description = "Sem itens para listar", content = @Content())
    })
    @GetMapping("/invoice/item")
    public ResponseEntity<List<ItemModel>> getAllItems(){
        List<ItemModel> itemList= itemRepository.findAll();
        if(!itemList.isEmpty()){
            for(ItemModel item : itemList){
                Long sequencial = item.getSequential();
                Long invoiceNumber = item.getInvoiceHeader().getInvoiceNumber();

                item.add(linkTo(methodOn(ItemController.class).getOneItem(sequencial, invoiceNumber)).withSelfRel());
            }
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(itemList);
    }

    @Operation(summary = "Busca todos os itens de um pedido pelo número do invoice")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itens listados com sucesso", content = @Content()),
            @ApiResponse(responseCode = "204", description = "Sem itens para listar", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Invoice não encontrado", content = @Content())
    })
    @GetMapping("/invoice/items/{invoiceNumber}")
    public ResponseEntity<List<ItemModel>> getAllItemsOfOneInvoice(
            @PathVariable("invoiceNumber") Long invoiceNumber) {

        List<ItemModel> itemList = itemRepository.findByInvoiceNumber(invoiceHeaderRepository.findById(invoiceNumber).get());

        if (itemList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        for (ItemModel item : itemList) {
            Long sequencial = item.getSequential();
            item.add(linkTo(methodOn(ItemController.class).getOneItem(sequencial, invoiceNumber)).withSelfRel());
        }

        return ResponseEntity.status(HttpStatus.OK).body(itemList);
    }

    @Operation(summary = "Busca um item a partir de um sequencial e de um numero unico do cabeçalho respectivamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itens listado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Sequencial/número único não encontrados", content = @Content())
    })
    @GetMapping("/invoice/item/{sequencial}/{invoiceNumber}")
    public ResponseEntity<Object> getOneItem(@PathVariable(value = "sequencial") Long sequencial
                                            , @PathVariable(value = "invoiceNumber") Long invoiceNumber){
        Optional<ItemModel> item = itemRepository.findById(new ItemId(sequencial, invoiceNumber));
        if(item.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found. ");
        }
        item.get().add(linkTo(methodOn(ItemController.class).getAllItems()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(item.get());
    }

    @Operation(summary = "Altera um item a partir de um sequencial e um numero unico de cabeçalho respectivamente", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"invoiceNumber\": 7, \"barCode\": \"123456\", \"qty\": 10 }")
                    )),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido\n- Valores fora do intervalo permitido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"invoiceNumber\": \"7\", \"barCode\": \"abs\" }")
                    )),
            @ApiResponse(responseCode = "404", description = "Item/produto/Cabeçalho da nota não encontrados", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PutMapping("/invoice/item/{sequencial}/{invoiceNumber}")
    public ResponseEntity<Object> updateItem(@PathVariable(value = "sequencial") Long sequencial
                                                 , @PathVariable(value = "invoiceNumber") Long invoiceNumber
                                                ,  @RequestBody @Valid ItemRecordDto itemRecordDto){

        Optional<ProductModel> product = productRepository.findByBarCode(itemRecordDto.barCode());
        if(product.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }
        Optional<InvoiceHeaderModel> invoiceHeader = invoiceHeaderRepository.findById(itemRecordDto.invoiceNumber());
        if(invoiceHeader.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice Header Not Found");
        }
        Optional<ItemModel> itemModel = itemRepository.findById(new ItemId(sequencial, invoiceNumber));
        if(itemModel.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item Not Found");
        }

        BeanUtils.copyProperties(itemRecordDto, itemModel.get());

        return ResponseEntity.status(HttpStatus.OK).body(itemRepository.save(itemModel.get()));
    }

    @Operation(summary = "Deleta um um item a partir de um sequencial e de um numero unico do cabeçalho respectivamente", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itens listado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Sequencial/número único não encontrados", content = @Content())
    })
    @DeleteMapping("/invoice/item/{sequencial}/{invoiceNumber}")
    public ResponseEntity<Object> deleteItem(@PathVariable(value = "sequencial") Long sequencial
                                            , @PathVariable(value = "invoiceNumber") Long invoiceNumber) {
        Optional<ItemModel> item = itemRepository.findById(new ItemId(sequencial, invoiceNumber));
        if (item.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item Not Found");
        }
        Optional<InvoiceHeaderModel> invoiceHeader = invoiceHeaderRepository.findById(invoiceNumber);
        if(invoiceHeader.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice Header Not Found");
        }else{
            if(invoiceHeader.get().getConfirmed().equals("S")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Invoice already confirmed");
            }
        }
        itemRepository.delete(item.get());

        invoiceService.updateTotalAmount(item.get().getInvoiceHeader().getInvoiceNumber());
        return ResponseEntity.status(HttpStatus.OK).body("Item delete successfully");
    }

}
