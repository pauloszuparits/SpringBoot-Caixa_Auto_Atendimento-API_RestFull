# API RestFul - Caixa Auto Atendimento

Bem-vindo ao repositório da API RestFul para o sistema de Caixa Auto Atendimento! Aqui você encontrará informações detalhadas sobre as funcionalidades, estrutura e propósito desta API.

## Acesse a Documentação Completa
Para testes e acesso à documentação da API, visite:

**[Swagger UI - Caixa Auto Atendimento](https://springboot-caixa-auto-atendimento-api.onrender.com/swagger-ui/index.html)**

> **Nota:** Esta API está hospedada em um servidor gratuito, o que pode causar um tempo de inicialização de 3 a 5 minutos. Por favor, aguarde durante este período.

---

## Funcionalidades da API

A API oferece diversas funcionalidades para cadastro, consulta e gerenciamento de dados:

### 1. Cadastro e Consulta de Clientes
- **Requisitos para Cadastro:**
  - CPF válido
  - Nome do cliente
  - Data de nascimento
- **Consulta:** Por meio do CPF.

### 2. Cadastro e Consulta de Produtos
- **Requisitos para Cadastro:**
  - Nome do produto
  - Valor
  - Código de barras único
  - Peso
  - Status de atividade (Y/N)
- **Consulta:** Por UUID gerado automaticamente.

### 3. Gerenciamento de Estoque
- **Requisitos para Cadastro:**
  - UUID de um produto já cadastrado
  - Quantidade em estoque
- **Consulta:** Por UUID do produto.

### 4. Cadastro de Métodos de Pagamento
- **Requisitos para Cadastro:**
  - Tipo (Dinheiro, Cartão de Crédito/Débito)
  - Bandeira do cartão (quando aplicável)
- **Consulta:** Por ID gerado automaticamente.

### 5. Gerenciamento de Notas
#### Cabeçalho da Nota
- **Requisitos para Cadastro:**
  - CPF do cliente
  - Método de pagamento
  - Tipo de Operação
- **Consulta:** Por ID único gerado automaticamente.

#### Itens da Nota
- **Requisitos para Cadastro:**
  - ID do cabeçalho da nota
  - Código de barras do produto
  - Quantidade
- **Consulta:** Por sequencial gerado para o cabeçalho da nota.

### 6. Tipos de Operação
- **Requisitos para Cadastro:**
  - Tipo (Compra ou Venda)
  - Atualiza Estoque (Y/N)
  - Status de atividade (Y/N)

---

## Estrutura do Código

O código é organizado em seis categorias principais:

### 1. Controllers
- **Responsabilidade:** Gerenciam as requisições HTTP e retornam respostas adequadas.
- **Principais Tarefas:**
  - Mapear endpoints da API (e.g., `@RestController`, `@RequestMapping`).
  - Delegar a lógica para os services.
  - Retornar respostas no formato JSON.

### 2. DTOs (Data Transfer Objects)
- **Responsabilidade:** Transferir dados entre camadas da aplicação.
- **Principais Tarefas:**
  - Garantir a segurança e a consistência ao expor apenas os campos necessários.

### 3. Exceptions
- **Responsabilidade:** Tratar erros de forma centralizada.
- **Principais Tarefas:**
  - Criar exceções personalizadas (e.g., `DataIntegrityViolationException`).
  - Configurar handlers globais (e.g., `@ControllerAdvice`, `@ExceptionHandler`).

### 4. Models
- **Responsabilidade:** Representar a estrutura de dados no banco.
- **Principais Tarefas:**
  - Mapear tabelas com anotações como `@Entity`, `@Table`.
  - Definir relações (e.g., `@OneToMany`, `@ManyToOne`).

### 5. Repositories
- **Responsabilidade:** Abstração das operações no banco de dados.
- **Principais Tarefas:**
  - Gerenciar operações CRUD e consultas personalizadas.

### 6. Services
- **Responsabilidade:** Contém a lógica de negócio.
- **Principais Tarefas:**
  - Implementar regras de negócio.
  - Coordenar a interação entre controllers e repositories.

---

## Tecnologias Utilizadas
- **Java**: Linguagem principal.
- **Spring Boot**: Framework para desenvolvimento.
- **Hibernate**: ORM para interação com o banco de dados.
- **Swagger**: Documentação da API.
- **Postgree**: Banco de dados para armazenamento dos dados.

---

Agradecemos por utilizar a API! Caso encontre algum problema ou tenha sugestões.

