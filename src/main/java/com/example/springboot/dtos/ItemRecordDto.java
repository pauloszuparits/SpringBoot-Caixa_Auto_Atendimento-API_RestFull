package com.example.springboot.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "Item", description = "Dados para criação ou atualização do item da nota")
@JsonIgnoreProperties(ignoreUnknown = false)
public record ItemRecordDto(@NotNull
                            @Schema(description = "Número do pedido", example = "101")
                            Long invoiceNumber
                            ,@NotNull
                             @Schema(description = "Código de barras do produto", example = "123456")
                             Long barCode
                            , @NotNull
                             @Schema(description = "Quantidade do produto selecionado", example = "2")
                            BigDecimal qty) {
}
