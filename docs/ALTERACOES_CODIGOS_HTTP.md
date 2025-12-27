# Resumo das Alterações - Códigos HTTP Corretos

## Data: 2025-12-27

## Objetivo
Implementar a utilização correta dos padrões de códigos de resposta HTTP para as APIs, garantindo que cada operação retorne o código semântico apropriado.

## Alterações Realizadas

### 1. Novas Exceptions Criadas

#### `ContaJaExisteException.java`
- **Localização**: `domain/exception/ContaJaExisteException.java`
- **Propósito**: Representar conflito quando uma conta já existe no sistema
- **Código HTTP**: 409 Conflict
- **Uso**: Tentativa de cadastro com conta já existente (ex: conta "12345")

#### `DadosInvalidosException.java`
- **Localização**: `domain/exception/DadosInvalidosException.java`
- **Propósito**: Representar dados semanticamente inválidos
- **Código HTTP**: 422 Unprocessable Entity
- **Uso**: Validações de dados que não podem ser processados

### 2. Alterações no `ClienteService.java`

**Antes**:
```java
throw new IllegalArgumentException(MensagensConstant.CONTA_JA_EXISTE);
```

**Depois**:
```java
throw new ContaJaExisteException(MensagensConstant.CONTA_JA_EXISTE);
```

**Impacto**: Agora retorna HTTP 409 Conflict ao invés de 400 Bad Request para contas duplicadas.

### 3. Alterações no `TransferenciaController.java`

**Antes**:
```java
return ResponseEntity.ok(transferencia); // 200 OK
```

**Depois**:
```java
return ResponseEntity.status(HttpStatus.CREATED).body(transferencia); // 201 Created
```

**Impacto**: Transferências bem-sucedidas agora retornam HTTP 201 Created, seguindo as melhores práticas REST.

### 4. Melhorias no `GlobalExceptionHandler.java`

#### Novos Handlers Adicionados:
1. **`handleContaJaExiste`**: 409 Conflict
2. **`handleDadosInvalidos`**: 422 Unprocessable Entity
3. **`handleIllegalArgument`**: 400 Bad Request
4. **`handleValidationErrors`**: 422 Unprocessable Entity (Bean Validation)

#### Melhorias na Estrutura de Resposta:
- Adicionado campo `path` em todas as respostas de erro
- Injeção de `HttpServletRequest` para capturar o URI da requisição
- Tratamento detalhado de erros de validação com mapeamento de campos

**Estrutura de resposta padrão**:
```json
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Conta já existe",
  "path": "/clientes"
}
```

**Estrutura de resposta para validação**:
```json
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Erro de validação dos dados",
  "errors": {
    "numeroConta": "não deve estar em branco",
    "valor": "deve ser maior que zero"
  },
  "path": "/transferencias"
}
```

### 5. Atualização dos Testes

#### `ClienteServiceTest.java`
- Atualizado teste `deveLancarExcecaoQuandoContaJaExiste()`
- Mudança de `IllegalArgumentException` para `ContaJaExisteException`
- Todos os testes passando ✅

## Mapeamento Completo de Códigos HTTP

| Operação | Endpoint | Sucesso | Código |
|----------|----------|---------|--------|
| Cadastrar Cliente | POST /clientes | Sucesso | 201 Created |
| Listar Clientes | GET /clientes | Sucesso | 200 OK |
| Buscar Cliente | GET /clientes/conta/{id} | Sucesso | 200 OK |
| Buscar Cliente | GET /clientes/conta/{id} | Não encontrado | 404 Not Found |
| Cadastrar Cliente | POST /clientes | Conta já existe | 409 Conflict |
| Realizar Transferência | POST /transferencias | Sucesso | 201 Created |
| Realizar Transferência | POST /transferencias | Saldo insuficiente | 400 Bad Request |
| Realizar Transferência | POST /transferencias | Limite excedido | 400 Bad Request |
| Realizar Transferência | POST /transferencias | Conta não encontrada | 404 Not Found |
| Qualquer | Qualquer | Dados inválidos | 422 Unprocessable Entity |
| Qualquer | Qualquer | Conflito de concorrência | 409 Conflict |
| Qualquer | Qualquer | Erro interno | 500 Internal Server Error |

## Exemplo Prático

### Cenário: Tentativa de cadastro com conta já existente (12345)

**Request**:
```http
POST /clientes
Content-Type: application/json

{
  "nome": "Maria Santos",
  "numeroConta": "12345",
  "saldo": 500.00
}
```

**Response**: `409 Conflict`
```json
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Conta já existe",
  "path": "/clientes"
}
```

**Log do servidor**:
```
ERROR c.i.a.t.a.s.ClienteService - Tentativa de cadastro com conta já existente: 12345
```

## Documentação Criada

- **`docs/CODIGOS_HTTP.md`**: Documentação completa com todos os códigos HTTP, exemplos e boas práticas

## Benefícios

1. ✅ **Semântica correta**: Cada código HTTP reflete precisamente o resultado da operação
2. ✅ **Melhor experiência do desenvolvedor**: Respostas claras e consistentes
3. ✅ **Rastreabilidade**: Campo `path` facilita debugging
4. ✅ **Validação detalhada**: Erros de validação mostram exatamente quais campos falharam
5. ✅ **Conformidade REST**: Seguindo as melhores práticas da arquitetura REST
6. ✅ **Separação de responsabilidades**: Exceptions de domínio mapeadas na camada de infraestrutura

## Testes

- ✅ Todos os testes unitários passando
- ✅ Teste específico para `ContaJaExisteException` atualizado e funcionando
- ✅ Compilação bem-sucedida com Java 21

## Próximos Passos Sugeridos

1. Adicionar testes de integração para validar os códigos HTTP retornados pelos controllers
2. Atualizar a coleção do Postman com exemplos de cada código de resposta
3. Considerar adicionar validações com Bean Validation nos DTOs
4. Documentar os endpoints com Swagger/OpenAPI incluindo os códigos de resposta
