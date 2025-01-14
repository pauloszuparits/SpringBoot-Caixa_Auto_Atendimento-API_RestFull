package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import com.example.springboot.services.StockService;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Produto")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private StockService stockService;

    @Operation(summary = "Realiza o cadastro do produto", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"name\": \"Produto Exemplo\", \"productValue\": 8.65, \"barCode\": 123456, \"weight\": 0.2, \"active\": \"Y\" }")
                    )),
            @ApiResponse(responseCode = "409", description = "Campo 'barCode' já cadastrado", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido\n- Valores fora do intervalo permitido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"productValue\": \"b\", \"barCode\": \"123DSA\", \"weight\": \"L\", \"active\": \"B\" }")
                    )),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PostMapping(value = "/products", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto){
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);

        ProductModel productModelSaved = productRepository.save(productModel);

        stockService.createStock(productModelSaved.getIdProduct());

        return ResponseEntity.status(HttpStatus.CREATED).body(productModelSaved);
    }

    @Operation(summary = "Busca todos os produtos cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos listados com sucesso", content = @Content()),
            @ApiResponse(responseCode = "204", description = "Sem produtos para listar", content = @Content())
    })
    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts(){
        List<ProductModel> productsList= productRepository.findAll();
        if(!productsList.isEmpty()){
            for(ProductModel product : productsList){
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }

    @Operation(summary = "Busca um produto a partir de um ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto listado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Formato UUID inválido", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content())
    })
    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> product = productRepository.findById(id);
        if(product.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found. ");
        }
        product.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(product.get());
    }

    @Operation(summary = "Altera um produto a partir de um ID", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"name\": \"Produto Exemplo\", \"productValue\": 8.65, \"barCode\": 123456, \"weight\": 0.2, \"active\": \"Y\" }")
                    )),
            @ApiResponse(responseCode = "409", description = "Campo 'barCode' já cadastrado", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido\n- Valores fora do intervalo permitido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"productValue\": \"b\", \"barCode\": \"123DSA\", \"weight\": \"L\", \"active\": \"B\" }")
                    )),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PutMapping(value = "/products/{id}", consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
                                                @RequestBody @Valid ProductRecordDto productRecordDto){

        Optional<ProductModel> product = productRepository.findById(id);
        if(product.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }
        var productModel = product.get();
        BeanUtils.copyProperties(productRecordDto, productModel);

        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    @Operation(summary = "Deleta um produto a partir de um ID", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Formato UUID inválido", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content()),
            @ApiResponse(responseCode = "409", description = "Produto sendo usado em outra tabela", content = @Content())
    })
    @DeleteMapping("/products/{id}" )
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }
        productRepository.delete(product.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product delete successfully");
    }
}
