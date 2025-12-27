# Códigos de Resposta HTTP - API de Transferência Bancária

## Visão Geral
Este documento descreve os códigos de status HTTP utilizados nas APIs do projeto, seguindo as melhores práticas REST.

## Códigos de Sucesso (2xx)

### 200 OK
**Quando usar**: Requisições GET bem-sucedidas e operações que retornam dados existentes.

**Endpoints**:
- `GET /clientes` - Listar todos os clientes
- `GET /clientes/conta/{numeroConta}` - Buscar cliente por número de conta
- `GET /transferencias/conta/{numeroConta}` - Buscar histórico de transferências

**Exemplo de resposta**:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "nome": "João Silva",
  "numeroConta": "12345",
  "saldo": 1000.00
}
```

### 201 Created
**Quando usar**: Criação bem-sucedida de novos recursos.

**Endpoints**:
- `POST /clientes` - Cadastrar novo cliente
- `POST /transferencias` - Realizar transferência

**Exemplo de resposta**:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "contaOrigem": "12345",
  "contaDestino": "67890",
  "valor": 500.00,
  "status": "SUCESSO",
  "dataHora": "2025-12-27T19:00:00"
}
```

**Headers**:
- `Location: /clientes/conta/12345` (para criação de cliente)

---

## Códigos de Erro do Cliente (4xx)

### 400 Bad Request
**Quando usar**: Validações de regras de negócio que impedem o processamento da requisição.

**Cenários**:
- Saldo insuficiente para transferência
- Valor de transferência excede o limite de R$ 10.000,00
- Argumentos inválidos genéricos

**Exemplo de resposta**:
```json
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Saldo insuficiente",
  "path": "/transferencias"
}
```

### 404 Not Found
**Quando usar**: Recurso solicitado não existe.

**Cenários**:
- Conta de origem não encontrada
- Conta de destino não encontrada
- Cliente não encontrado

**Exemplo de resposta**:
```json
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Conta de origem não encontrada",
  "path": "/transferencias"
}
```

### 409 Conflict
**Quando usar**: Conflito com o estado atual do recurso.

**Cenários**:
- Tentativa de cadastro com conta já existente (exemplo: conta "12345")
- Conflito de concorrência (Optimistic Locking)

**Exemplo de resposta**:
```json
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Conta já existe",
  "path": "/clientes"
}
```

### 422 Unprocessable Entity
**Quando usar**: Dados enviados estão sintaticamente corretos, mas semanticamente inválidos.

**Cenários**:
- Erros de validação do Bean Validation (@Valid)
- Dados inválidos que não podem ser processados

**Exemplo de resposta com validação**:
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

---

## Códigos de Erro do Servidor (5xx)

### 500 Internal Server Error
**Quando usar**: Erros inesperados do servidor.

**Cenários**:
- Exceções não tratadas
- Falhas de sistema

**Exemplo de resposta**:
```json
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Ocorreu um erro interno no servidor.",
  "path": "/transferencias"
}
```

---

## Mapeamento de Exceções

| Exception | HTTP Status | Código | Descrição |
|-----------|-------------|--------|-----------|
| `ContaJaExisteException` | CONFLICT | 409 | Tentativa de cadastro com conta já existente |
| `ContaNaoEncontradaException` | NOT_FOUND | 404 | Conta não encontrada |
| `SaldoInsuficienteException` | BAD_REQUEST | 400 | Saldo insuficiente para transferência |
| `LimiteTransferenciaExcedidoException` | BAD_REQUEST | 400 | Valor excede R$ 10.000,00 |
| `DadosInvalidosException` | UNPROCESSABLE_ENTITY | 422 | Dados inválidos |
| `IllegalArgumentException` | BAD_REQUEST | 400 | Argumento inválido |
| `MethodArgumentNotValidException` | UNPROCESSABLE_ENTITY | 422 | Erro de validação Bean Validation |
| `ObjectOptimisticLockingFailureException` | CONFLICT | 409 | Conflito de concorrência |
| `Exception` (genérica) | INTERNAL_SERVER_ERROR | 500 | Erro inesperado |

---

## Estrutura Padrão de Resposta de Erro

Todas as respostas de erro seguem o seguinte formato:

```json
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Mensagem descritiva do erro",
  "path": "/endpoint-que-gerou-erro"
}
```

Para erros de validação (422), inclui-se também:

```json
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Erro de validação dos dados",
  "errors": {
    "campo1": "mensagem de erro do campo1",
    "campo2": "mensagem de erro do campo2"
  },
  "path": "/endpoint-que-gerou-erro"
}
```

---

## Exemplo Prático: Cadastro de Cliente

### Cenário 1: Sucesso
**Request**:
```http
POST /clientes
Content-Type: application/json

{
  "nome": "João Silva",
  "numeroConta": "12345",
  "saldo": 1000.00
}
```

**Response**: `201 Created`
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "nome": "João Silva",
  "numeroConta": "12345",
  "saldo": 1000.00
}
```

### Cenário 2: Conta já existe
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

---

## Boas Práticas Implementadas

1. ✅ **Códigos semânticos**: Cada código HTTP reflete precisamente o resultado da operação
2. ✅ **Mensagens descritivas**: Mensagens claras e em português para facilitar debugging
3. ✅ **Estrutura consistente**: Todas as respostas de erro seguem o mesmo formato
4. ✅ **Timestamp**: Todas as respostas incluem timestamp para rastreabilidade
5. ✅ **Path**: Indica qual endpoint gerou o erro
6. ✅ **Validação detalhada**: Erros de validação mostram exatamente quais campos falharam
7. ✅ **Separação de responsabilidades**: Exceptions de domínio mapeadas para códigos HTTP na camada de infraestrutura
