package com.example.springboot.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

import java.sql.Timestamp;

@Schema(name = "Cliente", description = "Dados do cliente para criação ou atualização")
@JsonIgnoreProperties(ignoreUnknown = false)
public record CustomerRecordDto(@NotNull
                                @Schema(description = "CPF do cliente", example = "25414170045")
                                @CPF(message = "CPF inválido")
                                String cpf
                                , @NotBlank
                                @Schema(description = "Nome do cliente", example = "José da Silva")
                                String name
                                ,@Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Data de nascimento deve estar no formato dd/MM/yyyy")
                                @Schema(description = "Data de nascimento do cliente (dd/MM/yyyy)", example = "04/05/2001")
                                String birthDate) {
}
