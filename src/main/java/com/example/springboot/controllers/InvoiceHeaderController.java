package com.example.springboot.controllers;

import com.example.springboot.dtos.InvoiceHeaderRecordDto;
import com.example.springboot.models.*;
import com.example.springboot.repositories.CustomerRepository;
import com.example.springboot.repositories.InvoiceHeaderRepository;
import com.example.springboot.repositories.OPTRepository;
import com.example.springboot.repositories.PaymentMethodRepository;
import com.example.springboot.services.InvoiceService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Cabecalho Da Nota")
public class InvoiceHeaderController {

    @Autowired
    InvoiceHeaderRepository invoiceHeaderRepository;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OPTRepository optRepository;

    @Autowired
    private StockService stockService;

    @Operation(summary = "Realiza o cadastro do cabeçalho da nota", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cabeçalho da nota cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"customerCpf\": \"10167462024\", \"optId\": 1000 }")
                    )),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido\n- Valores fora do intervalo permitido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"customerCpf\": \"1016462024\" }")
                    )),
            @ApiResponse(responseCode = "401", description = "CPF do cliente, ID do pagamento ou ID da operação não enconttrados",
                    content = @Content()),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PostMapping(value = "/invoice/header",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> saveInvoiceHeader(@RequestBody @Valid InvoiceHeaderRecordDto invoiceHeaderRecordDto){
        var invoiceHeaderModel = new InvoiceHeaderModel();

        Optional<OPTModel> opt = optRepository.findById(invoiceHeaderRecordDto.optId());
        if(opt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OPT Not Found");
        }else{
            if(opt.get().getActive().equals("N")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("OPT is not active");
            }
            invoiceHeaderModel.setOpt(opt.get());
        }

        if(invoiceHeaderRecordDto.paymentId() != null){
            Optional<PaymentMethodModel> paymentMethod = paymentMethodRepository.findById(invoiceHeaderRecordDto.paymentId());
            if(paymentMethod.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment Method Not Found");
            }else{
                invoiceHeaderModel.setPayment(paymentMethod.get());
            }
        }

        if(invoiceHeaderRecordDto.customerCpf() != null){
            Optional<CustomerModel> customerModel = customerRepository.findById(invoiceHeaderRecordDto.customerCpf());
            if(customerModel.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found");
            }else{
                invoiceHeaderModel.setCustomer(customerModel.get());
            }
        }

        BeanUtils.copyProperties(invoiceHeaderRecordDto, invoiceHeaderModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceHeaderRepository.save(invoiceHeaderModel));
    }

    @Operation(summary = "Busca todos os cabeçalhos de nota cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cabecalhos da nota listados com sucesso", content = @Content()),
            @ApiResponse(responseCode = "204", description = "Sem cabeçalho da nota para listar", content = @Content())
    })
    @GetMapping("/invoice/header")
    public ResponseEntity<List<InvoiceHeaderModel>> getAllInvoiceHeaders(){
        List<InvoiceHeaderModel> invoiceHeaderList= invoiceHeaderRepository.findAll();
        if(!invoiceHeaderList.isEmpty()){
            for(InvoiceHeaderModel invoiceHeader : invoiceHeaderList){
                Long id = invoiceHeader.getInvoiceNumber();
                invoiceHeader.add(linkTo(methodOn(InvoiceHeaderController.class).getOneInvoiceHeader(id)).withSelfRel());
            }
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(invoiceHeaderList);
    }

    @Operation(summary = "Busca um cabeçalho de nota a partir de um ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cabeçalho de nota listado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Cabeçalho de nota não encontrado", content = @Content())
    })
    @GetMapping("/invoice/header/{invoiceNumber}")
    public ResponseEntity<Object> getOneInvoiceHeader(@PathVariable(value = "invoiceNumber") Long invoiceNumber){
        Optional<InvoiceHeaderModel> invoiceHeader = invoiceHeaderRepository.findById(invoiceNumber);
        if(invoiceHeader.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice Header not found. ");
        }
        invoiceHeader.get().add(linkTo(methodOn(InvoiceHeaderController.class).getAllInvoiceHeaders()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(invoiceHeader.get());
    }

    @Operation(summary = "Altera um local a partir de um ID", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cabeçalho da nota atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"customerCpf\": \"10167462024\", \"optId\": 1000 }")
                    )),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido\n- Cabeçalho da nota já confirmado\n- Valores fora do intervalo permitido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"customerCpf\": \"1016462024\" }")
                    )),
            @ApiResponse(responseCode = "401", description = "CPF do cliente, ID do pagamento ou ID da operação não enconttrados",
                    content = @Content()),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PutMapping(value = "/invoice/header/{invoiceNumber}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateInvoiceHeader(@PathVariable(value = "invoiceNumber") Long invoiceNumber,
                                              @RequestBody @Valid InvoiceHeaderRecordDto invoiceHeaderRecordDto){

        Optional<InvoiceHeaderModel> invoiceHeader = invoiceHeaderRepository.findById(invoiceNumber);
        if(invoiceHeader.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice Header Not Found");
        }
        var invoiceHeaderModel = invoiceHeader.get();


        if(!Objects.equals(invoiceHeaderRecordDto.optId(), invoiceHeaderModel.getOpt().getIdOpt())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("OPT Cannot be changed");
        }

        if(invoiceHeaderRecordDto.paymentId() != null){
            Optional<PaymentMethodModel> paymentMethod = paymentMethodRepository.findById(invoiceHeaderRecordDto.paymentId());
            if(paymentMethod.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment Method Not Found");
            }
        }

        if(invoiceHeaderRecordDto.customerCpf() != null){
            Optional<CustomerModel> customerModel = customerRepository.findById(invoiceHeaderRecordDto.customerCpf());
            if(customerModel.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found");
            }
        }
        BeanUtils.copyProperties(invoiceHeaderRecordDto, invoiceHeaderModel);

        return ResponseEntity.status(HttpStatus.OK).body(invoiceHeaderRepository.save(invoiceHeaderModel));
    }

    @Operation(summary = "Deleta um cabeçalho da nota a partir de um ID", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cabeçalho da nota deletado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Cabeçalho da nota não encontrado", content = @Content())
    })
    @DeleteMapping("/invoice/header/{invoiceNumber}")
    public ResponseEntity<Object> deleteInvoiceHeader(@PathVariable(value = "invoiceNumber") Long invoiceNumber) {
        Optional<InvoiceHeaderModel> invoiceHeader = invoiceHeaderRepository.findById(invoiceNumber);
        if (invoiceHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice Header Not Found");
        }else{
            if(invoiceHeader.get().getConfirmed().equals("S")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Invoice Header already confirmed");
            }
        }
        invoiceHeaderRepository.delete(invoiceHeader.get());
        return ResponseEntity.status(HttpStatus.OK).body("Invoice Header delete successfully");
    }

    @Operation(summary = "Atualiza o método de pagamento do cabeçalho da nota", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cabeçalho da nota atualizado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Cabeçalho da nota, ou método de pagamento não encontrado", content = @Content())
    })
    @PutMapping(value = "/invoice/header/{invoiceNumber}/{paymentMethod}")
    public ResponseEntity<Object> updateInvoiceHeaderPaymentMethod(@PathVariable(value = "invoiceNumber") Long invoiceNumber,
                                                                   @PathVariable(value = "paymentMethod") Long paymentMethodId){

        Optional<InvoiceHeaderModel> invoiceHeader = invoiceHeaderRepository.findById(invoiceNumber);
        if(invoiceHeader.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice Header Not Found");
        }else{
            if(invoiceHeader.get().getConfirmed().equals("S")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Invoice Header already confirmed");
            }
        }
        Optional<PaymentMethodModel> paymentMethod = paymentMethodRepository.findById(paymentMethodId);
        if(paymentMethod.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment Method Not Found");
        }

        var invoiceHeaderModel = invoiceHeader.get();
        invoiceHeaderModel.setPayment(paymentMethod.get());

        return ResponseEntity.status(HttpStatus.OK).body(invoiceHeaderRepository.save(invoiceHeaderModel));
    }

    @Operation(summary = "Confirma uma nota", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cabeçalho da nota confirmado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Cabeçalho da nota já confirmado ou valor da nota zerado", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Cabeçalho da nota não encontrado", content = @Content())
    })
    @PutMapping(value = "/invoice/header/{invoiceNumber}/confirm")
    public ResponseEntity<Object> confirmInvoiceHeader(@PathVariable(value = "invoiceNumber") Long invoiceNumber){

        Optional<InvoiceHeaderModel> invoiceHeader = invoiceHeaderRepository.findById(invoiceNumber);
        if(invoiceHeader.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice Header Not Found");
        }else{
            if(invoiceHeader.get().getConfirmed().equals("S")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Invoice Header already confirmed");
            }
            if(invoiceHeader.get().getTotalAmount().compareTo(BigDecimal.ZERO)<=0){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Total Amount can't be ZERO");
            }
            if(invoiceHeader.get().getPayment() == null){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Payment method is required");
            }
        }
        //TODO - UPDATE DE ESTOQUE
        try{
            stockService.updateStock(invoiceHeader.get().getInvoiceNumber());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        var invoiceHeaderModel = invoiceHeader.get();
        invoiceHeaderModel.setConfirmed("S");

        return ResponseEntity.status(HttpStatus.OK).body(invoiceHeaderRepository.save(invoiceHeaderModel));
    }


}
