package com.example.springboot;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API Auto-atendimento - Caixa Supermercado", version = "1", description = "Api desenvolvida para gerenciar auto-atendimentos de caixas de supermercado"))
@EntityScan(basePackages = "com.example.springboot.models")
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

}
