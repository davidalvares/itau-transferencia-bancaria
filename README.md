# Transferência Bancária API

Projeto de API RESTful para gerenciamento de clientes e transferências bancárias, desenvolvido com **Spring Boot** e **Arquitetura Hexagonal**.

## 📋 Funcionalidades

- **Cadastro de Clientes**:
  - Criação de novos clientes informando nome, número da conta e saldo inicial.
  - Validação de dados básicos.
- **Listagem de Clientes**:
  - Exibição de todos os clientes registrados na base de dados.
- **Busca de Cliente**:
  - Consulta detalhada de um cliente específico pelo número da conta.
- **Transferência Bancária**:
  - Transferência de valores entre duas contas existentes.
  - **Validação de Limite**: Transferências acima de R$ 10.000,00 são rejeitadas automaticamente.
  - **Validação de Saldo**: Verificação se a conta de origem possui saldo suficiente.
  - **Validação de Contas**: Verificação da existência das contas de origem e destino.
  - **Persistência de Falhas**: Transferências mal sucedidas (erro de validação) são salvas no banco com status de ERRO e motivo detalhado.
  - **Atomicidade**: Atualização segura dos saldos de origem e destino em uma única transação.
- **Histórico de Transferências**:
  - Consulta de todas as transferências (sucesso ou erro) envolvendo uma conta específica, ordenadas por data (dependendo da implementação do repositório).

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3** (Web, Data JPA, Validation)
- **MySQL** (Produção/Dev) / **H2** (Testes)
- **Docker & Docker Compose**
- **OpenAPI/Swagger** (Documentação)
- **JUnit 5 & MockMvc** (Testes)

## ⚙️ Pré-requisitos

- Java 17+
- Maven (ou usar o `mvnw` incluso)
- Docker (opcional, para rodar o banco MySQL via Compose)

## 🏃 Como Executar

### 1. Clonar o repositório
```bash
git clone <url-do-repositorio>
cd transferencia
```

### 2. Configurar Banco de Dados
O projeto está configurado para usar o Docker Compose automaticamente. Certifique-se de que o Docker está rodando.
Caso prefira rodar sem Docker, ajuste o `application.properties` para apontar para seu banco de dados MySQL local.

### 3. Rodar a aplicação
Linux/Mac:
```bash
./mvnw spring-boot:run
```
Windows:
```cmd
mvnw.cmd spring-boot:run
```

A aplicação estará disponível em `http://localhost:8081/transferencia-api`.

## 📖 Documentação da API (Swagger)

Após iniciar a aplicação, acesse a documentação interativa em:
👉 **[http://localhost:8081/transferencia-api/swagger-ui.html](http://localhost:8081/transferencia-api/swagger-ui.html)**

Aqui você pode testar todos os endpoints diretamente pelo navegador.

## 📫 Coleção do Postman

Um arquivo de coleção do Postman está incluído na raiz do projeto (`postman_collection.json`). Você pode importá-lo no Postman para testar os endpoints facilmente.

## 🧪 Como Rodar os Testes

O projeto possui **testes de integração** configurados para rodar com banco em memória (H2), não sendo necessário Docker para esta etapa.

Para executar os testes:
Linux/Mac:
```bash
./mvnw test
```
Windows:
```cmd
mvnw.cmd test
```

## 📂 Estrutura do Projeto (Arquitetura Hexagonal)

- `domain`: Regras de negócio, entidades e portas (interfaces).
- `application`: Casos de uso e serviços.
- `infrastructure`: Adaptadores de entrada (Controllers) e saída (Persistência, Configurações).

---
Desenvolvido como parte do desafio técnico.
