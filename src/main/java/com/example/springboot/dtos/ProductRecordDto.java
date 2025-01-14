package com.example.springboot.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

@Schema(name = "Produto", description = "Dados do produto para criação ou atualização")
@JsonIgnoreProperties(ignoreUnknown = false)
public record ProductRecordDto(@NotBlank
                               @Schema(description = "Nome do produto", example = "Caixa de Ovos ( 4UN )")
                               String name
                            ,  @NotNull
                               @Schema(description = "Valor do produto", example = "5.60")
                               BigDecimal productValue
                            ,  @Schema(description = "Código de barras do produto", example = "90274930283")
                               Long barCode
                            ,  @Schema(description = "Peso do produto em kilos (KG)", example = "0.3")
                               BigDecimal weight
                            ,  @NotBlank
                               @Schema(description = "Se o produto estiver ativo Y, se não estiver N", example = "Y")
                               @Pattern(regexp = "Y|N", message = "O campo 'active' deve ser 'Y' ou 'N'")
                               String active) {
}
