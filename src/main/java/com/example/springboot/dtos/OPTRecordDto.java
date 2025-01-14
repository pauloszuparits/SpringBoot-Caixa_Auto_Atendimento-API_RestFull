package com.example.springboot.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(name = "OPT's", description = "Dados do OPT para criação ou atualização")
@JsonIgnoreProperties(ignoreUnknown = false)
public record OPTRecordDto(@NotBlank
                           @Schema(description = "Usa-se P para operações de Compra, S para venda", example = "S")
                           @Pattern(regexp = "P|S", message = "O campo 'type' deve ser 'P' ou 'S'")
                           String type
                          ,@NotBlank
                           @Schema(description = "Se o OPT atualiza estoque ao realizar a operação Y, se não N", example = "N")
                           @Pattern(regexp = "Y|N", message = "O campo 'updateStock' deve ser 'Y' ou 'N'")
                           String updateStock
                          ,@NotBlank
                           @Schema(description = "Se o OPT estiver ativo Y, se não estiver N", example = "Y")
                           @Pattern(regexp = "Y|N", message = "O campo 'active' deve ser 'Y' ou 'N'")
                           String active) {
}
