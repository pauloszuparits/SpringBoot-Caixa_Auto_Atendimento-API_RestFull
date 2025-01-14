package com.example.springboot.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "Cabecalho da Nota", description = "Dados do cabecalho da nota para criação ou atualização")
@JsonIgnoreProperties(ignoreUnknown = false)
public record InvoiceHeaderRecordDto(
                                @Schema(description = "CPF do cliente", example = "85834532098")
                                String customerCpf
                                ,@Schema(description = "ID do método de pagamento", example = "1")
                                Long paymentId
                                ,@NotNull
                                @Schema(description = "Tipo de operação que será utilizado", example = "2")
                                Long optId
                                ) {
}
