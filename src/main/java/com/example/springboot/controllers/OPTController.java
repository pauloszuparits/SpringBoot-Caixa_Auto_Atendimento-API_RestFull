package com.example.springboot.controllers;

import com.example.springboot.dtos.OPTRecordDto;
import com.example.springboot.models.OPTModel;
import com.example.springboot.repositories.OPTRepository;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "OPT's - Tipos de Operação")
public class OPTController {

    @Autowired
    OPTRepository optRepository;


    @Operation(summary = "Realiza o cadastro do OPT", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OPT cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"type\": \"P\", \"updateStock\": \"Y\", \"active\": \"Y\" }")
                    )),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido\n- Valores fora do intervalo permitido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"type\": 1, \"updateStock\": \"B\", \"active\": \"C\" }")
                    )),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PostMapping(value = "/opts", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OPTModel> saveOPT(@RequestBody @Valid OPTRecordDto optRecordDto){
        var optModel = new OPTModel();
        BeanUtils.copyProperties(optRecordDto, optModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(optRepository.save(optModel));
    }


    @Operation(summary = "Busca todos os OPT's cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OPT's listados com sucesso", content = @Content()),
            @ApiResponse(responseCode = "204", description = "Sem OPT para listar", content = @Content())
    })
    @GetMapping("/opts")
    public ResponseEntity<List<OPTModel>> getAllOpts(){
        List<OPTModel> optsList= optRepository.findAll();
        if(!optsList.isEmpty()){
            for(OPTModel opt : optsList){
                Long id = opt.getIdOpt();
                opt.add(linkTo(methodOn(OPTController.class).getOneOpt(id)).withSelfRel());
            }
        }else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(optsList);
    }

    @Operation(summary = "Busca um OPT a partir de um ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OPT listado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "404", description = "OPT não encontrado", content = @Content())
    })
    @GetMapping("/opts/{id}")
    public ResponseEntity<Object> getOneOpt(@PathVariable(value = "id") Long id){
        Optional<OPTModel> opt = optRepository.findById(id);
        if(opt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OPT not found. ");
        }
        opt.get().add(linkTo(methodOn(OPTController.class).getAllOpts()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(opt.get());
    }

    @Operation(summary = "Altera um OPT a partir de um ID", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OPT cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"type\": \"P\", \"updateStock\": \"Y\", \"active\": \"Y\" }")
                    )),
            @ApiResponse(responseCode = "400", description = "Requisição inválida: \n- Campos obrigatórios não preenchidos\n- Formato de dados inválido\n- Valores fora do intervalo permitido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"type\": 1, \"updateStock\": \"B\", \"active\": \"C\" }")
                    )),
            @ApiResponse(responseCode = "404", description = "OPT não encontrado", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content())
    })
    @PutMapping(value = "/opts/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateOpt(@PathVariable(value = "id") Long id,
                                              @RequestBody @Valid OPTRecordDto optRecordDto){

        Optional<OPTModel> opt = optRepository.findById(id);
        if(opt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OPT Not Found");
        }
        var optModel = opt.get();
        BeanUtils.copyProperties(optRecordDto, optModel);

        return ResponseEntity.status(HttpStatus.OK).body(optRepository.save(optModel));
    }

    @Operation(summary = "Deleta um OPT a partir de um ID", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OPT deletado com sucesso", content = @Content()),
            @ApiResponse(responseCode = "404", description = "OPT não encontrado", content = @Content())
    })
    @DeleteMapping("/opts/{id}")
    public ResponseEntity<Object> deleteOpt(@PathVariable(value = "id") Long id) {
        Optional<OPTModel> opt = optRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OPT Not Found");
        }
        optRepository.delete(opt.get());
        return ResponseEntity.status(HttpStatus.OK).body("OPT delete successfully");
    }
}
