package com.example.springboot.controllers;

import com.example.springboot.dtos.PaymentMethodRecordDto;
import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.PaymentMethodModel;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.PaymentMethodRepository;
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
@Tag(name = "Método de Pagamento")
public class PaymentMethodController {

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Operation(summary = "Realiza o cadastro do método de pagamento", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Método de pagamento cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"type\": \"D\", \"cardBrand\": \"MasterCard\" }")

                    )),
            @ApiResponse(responseCode = "409", description = "Campos 'cardBrand' e 'Type' já foram cadastrados", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido\n- Valores fora do intervalo permitido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"type\": \"C\" }")
                    )),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PostMapping(value = "/paymentMethod", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentMethodModel> savePaymentMethod(@RequestBody @Valid PaymentMethodRecordDto paymentMethodRecordDto){
        var paymentMethodModel = new PaymentMethodModel();
        BeanUtils.copyProperties(paymentMethodRecordDto, paymentMethodModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMethodRepository.save(paymentMethodModel));
    }

    @Operation(summary = "Busca todos os métodos de pagamento cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Métodos de pagamento listados com sucesso", content = @Content()),
            @ApiResponse(responseCode = "204", description = "Sem métodos de pagamento para listar", content = @Content())
    })
    @GetMapping("/paymentMethod")
    public ResponseEntity<List<PaymentMethodModel>> getAllPaymentMethod(){
        List<PaymentMethodModel> paymentList= paymentMethodRepository.findAll();
        if(!paymentList.isEmpty()){
            for(PaymentMethodModel payment : paymentList){
                Long id = payment.getIdPmt();
                payment.add(linkTo(methodOn(PaymentMethodController.class).getOnePaymentMethod(id)).withSelfRel());
            }
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(paymentList);
    }

    @Operation(summary = "Busca um método de pagamento a partir de um ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Método de pagamento listado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Método de pagamento não encontrado", content = @Content())
    })
    @GetMapping("/paymentMethod/{id}")
    public ResponseEntity<Object> getOnePaymentMethod(@PathVariable(value = "id") Long id){
        Optional<PaymentMethodModel> payment = paymentMethodRepository.findById(id);
        if(payment.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment Method not found. ");
        }
        payment.get().add(linkTo(methodOn(PaymentMethodController.class).getAllPaymentMethod()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(payment.get());
    }

    @Operation(summary = "Altera um método de pagamento a partir de um ID", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Método de pagamento atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"type\": \"C\", \"cardBrand\": \"Visa\" }")
                    )),
            @ApiResponse(responseCode = "409", description = "Campos 'cardBrand' e 'Type' já foram cadastrados", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido\n- Valores fora do intervalo permitido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"type\": \"C\" }")
                    )),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PutMapping(value = "/paymentMethod/{id}", consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Object> updatePaymentMethod(@PathVariable(value = "id") Long id,
                                                @RequestBody @Valid PaymentMethodRecordDto paymentMethodRecordDto){

        Optional<PaymentMethodModel> payment = paymentMethodRepository.findById(id);
        if(payment.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment Method Not Found");
        }
        var paymentMethodModel = payment.get();
        BeanUtils.copyProperties(paymentMethodRecordDto, paymentMethodModel);

        return ResponseEntity.status(HttpStatus.OK).body(paymentMethodRepository.save(paymentMethodModel));
    }

    @Operation(summary = "Deleta um método de pagamento a partir de um ID", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Método de pagamento deletado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Método de pagamento não encontrado", content = @Content()),
            @ApiResponse(responseCode = "409", description = "Método de pagamento sendo usado em outra tabela", content = @Content())
    })
    @DeleteMapping("/paymentMethod/{id}" )
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") Long id) {
        Optional<PaymentMethodModel> payment = paymentMethodRepository.findById(id);
        if (payment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment Method Not Found");
        }
        paymentMethodRepository.delete(payment.get());
        return ResponseEntity.status(HttpStatus.OK).body("Payment Method delete successfully");
    }
}
