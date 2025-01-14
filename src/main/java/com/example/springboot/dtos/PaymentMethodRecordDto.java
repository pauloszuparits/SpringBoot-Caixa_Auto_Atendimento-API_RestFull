package com.example.springboot.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(name = "Métodos de pagamento", description = "Dados relacionados ao metodo de pagamento que será utilizado")
@JsonIgnoreProperties(ignoreUnknown = false)
public record PaymentMethodRecordDto(@NotBlank
                           @Schema(description = "Usa-se D para tipo do pagamento débito, C para Crédito, e M para pagamento em dinheiro", example = "D")
                           @Pattern(regexp = "D|C|M", message = "O campo 'type' deve ser 'D' ou 'C'")
                           String type
                           , @Schema(description = "Bandeira do cartão que está sendo utilizado, caso o tipo de pagamento não seja em dinheiro", example = "MasterCard")
                           String cardBrand
                          ) {
    @AssertTrue(message = "A bandeira do cartão é obrigatória para pagamentos de tipo 'D' ou 'C'")
    public boolean isCardBrandValid() {
        return !"D".equals(type) && !"C".equals(type) || (cardBrand != null && !cardBrand.isBlank());
    }
}
