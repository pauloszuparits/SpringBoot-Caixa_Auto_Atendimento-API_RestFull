package com.example.springboot.controllers;

import com.example.springboot.dtos.CustomerRecordDto;
import com.example.springboot.models.CustomerModel;
import com.example.springboot.repositories.CustomerRepository;
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Cliente")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @Operation(summary = "Realiza o cadastro do cliente", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"cpf\": \"25414170045\", \"name\": \"Paulo Szuparits\", \"birthDate\": \"04/05/2001\" }")
                    )),
            @ApiResponse(responseCode = "409", description = "Cliente já cadastrado", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido\n- Valores fora do intervalo permitido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"cpf\": \"1234\", \"birthDate\": \"2001/05/04\" }")
                    )),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PostMapping(value = "/customers",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> saveCustomer(@RequestBody @Valid CustomerRecordDto customerRecordDto) {
        Optional<CustomerModel> customer = customerRepository.findById(customerRecordDto.cpf());
        if(customer.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Customer already exists");
        }

        var customerModel = new CustomerModel();
        BeanUtils.copyProperties(customerRecordDto, customerModel);

        Timestamp birthDate = convertStringToTimestamp(customerRecordDto.birthDate());
        customerModel.setBirthDate(birthDate);

        return ResponseEntity.status(HttpStatus.CREATED).body(customerRepository.save(customerModel));
    }

    @Operation(summary = "Busca todos os clientes cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso", content = @Content()),
            @ApiResponse(responseCode = "204", description = "Sem cliente para listar", content = @Content())
    })
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerModel>> getAllLocations(){
        List<CustomerModel> customersList= customerRepository.findAll();
        if(!customersList.isEmpty()){
            for(CustomerModel customer : customersList){
                String cpf = customer.getCpf();
                customer.add(linkTo(methodOn(CustomerController.class).getOneCustomer(cpf)).withSelfRel());
            }
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(customersList);
    }

    @Operation(summary = "Busca um cliente a partir de um CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente listado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content())
    })
    @GetMapping("/customers/{cpf}")
    public ResponseEntity<Object> getOneCustomer(@PathVariable(value = "cpf") String cpf){
        Optional<CustomerModel> customer = customerRepository.findById(cpf);
        if(customer.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found. ");
        }
        customer.get().add(linkTo(methodOn(CustomerController.class).getAllLocations()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(customer.get());
    }

    @Operation(summary = "Altera um cliente a partir de um CPF", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"cpf\": \"25414170045\", \"name\": \"Paulo Szuparits\", \"birthDate\": \"04/05/2001\" }")
                    )),
            @ApiResponse(responseCode = "409", description = "Cliente já cadastrado", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido\n- Valores fora do intervalo permitido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"cpf\": \"1234\", \"birthDate\": \"2001/05/04\" }")
                    )),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PutMapping(value = "/customers/{cpf}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateCustomer(@PathVariable(value = "cpf") String cpf,
                                              @RequestBody @Valid CustomerRecordDto customerRecordDto){

        Optional<CustomerModel> customer = customerRepository.findById(cpf);
        if(customer.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found");
        }
        var customerModel = customer.get();
        BeanUtils.copyProperties(customerRecordDto, customerModel);

        return ResponseEntity.status(HttpStatus.OK).body(customerRepository.save(customerModel));
    }

    @Operation(summary = "Deleta um cliente a partir de um CPF", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente deletado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content())
    })
    @DeleteMapping("/customers/{cpf}")
    public ResponseEntity<Object> deleteCustomer(@PathVariable(value = "cpf") String cpf) {
        Optional<CustomerModel> customer = customerRepository.findById(cpf);
        if (customer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found");
        }
        customerRepository.delete(customer.get());
        return ResponseEntity.status(HttpStatus.OK).body("Customer delete successfully");
    }


    private Timestamp convertStringToTimestamp(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return new Timestamp(sdf.parse(date).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid Birth Date", e);
        }
    }
}
