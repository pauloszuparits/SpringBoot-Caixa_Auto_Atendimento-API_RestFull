package com.example.springboot.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "Estoque", description = "Dados do estoque para criação ou atualização - Os dados devem fazer referência ao produto cadastrado")
@JsonIgnoreProperties(ignoreUnknown = false)
public record StockRecordDto(
        @NotNull
        @Schema(description = "ID (UUID) Produto", example = "5c16df1e-6afe-4c44-98ed-90a783d4deb6")
        UUID idProduct,
        @Schema(description = "Quantidade no estoque", example = "10")
        Integer qtyInStock
) {
}


