# Fluxo de Códigos HTTP - Exemplos Práticos

## 1. Cadastro de Cliente

### ✅ Cenário de Sucesso
```
Cliente → POST /clientes
{
  "nome": "João Silva",
  "numeroConta": "12345",
  "saldo": 1000.00
}

API → 201 CREATED
Location: /clientes/conta/12345
{
  "id": "uuid-gerado",
  "nome": "João Silva",
  "numeroConta": "12345",
  "saldo": 1000.00
}
```

### ❌ Cenário de Erro: Conta Já Existe
```
Cliente → POST /clientes
{
  "nome": "Maria Santos",
  "numeroConta": "12345",  ← Conta já cadastrada
  "saldo": 500.00
}

API → 409 CONFLICT
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Conta já existe",
  "path": "/clientes"
}

Log: ERROR - Tentativa de cadastro com conta já existente: 12345
```

---

## 2. Transferência Bancária

### ✅ Cenário de Sucesso
```
Cliente → POST /transferencias
{
  "contaOrigem": "12345",
  "contaDestino": "67890",
  "valor": 500.00
}

API → 201 CREATED
{
  "id": "uuid-gerado",
  "contaOrigem": "12345",
  "contaDestino": "67890",
  "valor": 500.00,
  "status": "SUCESSO",
  "dataHora": "2025-12-27T19:00:00"
}
```

### ❌ Cenário de Erro: Saldo Insuficiente
```
Cliente → POST /transferencias
{
  "contaOrigem": "12345",  ← Saldo: R$ 100,00
  "contaDestino": "67890",
  "valor": 500.00          ← Tentando transferir R$ 500,00
}

API → 400 BAD REQUEST
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Saldo insuficiente",
  "path": "/transferencias"
}

Banco de Dados:
- Transferência registrada com status: ERRO
- Saldos não alterados
```

### ❌ Cenário de Erro: Limite Excedido
```
Cliente → POST /transferencias
{
  "contaOrigem": "12345",
  "contaDestino": "67890",
  "valor": 15000.00  ← Limite máximo: R$ 10.000,00
}

API → 400 BAD REQUEST
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Valor excede o limite de R$ 10.000,00",
  "path": "/transferencias"
}
```

### ❌ Cenário de Erro: Conta Não Encontrada
```
Cliente → POST /transferencias
{
  "contaOrigem": "99999",  ← Conta não existe
  "contaDestino": "67890",
  "valor": 500.00
}

API → 404 NOT FOUND
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Conta de origem não encontrada",
  "path": "/transferencias"
}
```

---

## 3. Buscar Cliente

### ✅ Cenário de Sucesso
```
Cliente → GET /clientes/conta/12345

API → 200 OK
{
  "id": "uuid",
  "nome": "João Silva",
  "numeroConta": "12345",
  "saldo": 1000.00
}
```

### ❌ Cenário de Erro: Cliente Não Encontrado
```
Cliente → GET /clientes/conta/99999

API → 404 NOT FOUND
```

---

## 4. Validação de Dados

### ❌ Cenário de Erro: Dados Inválidos
```
Cliente → POST /transferencias
{
  "contaOrigem": "",      ← Vazio
  "contaDestino": "67890",
  "valor": -100.00        ← Negativo
}

API → 422 UNPROCESSABLE ENTITY
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Erro de validação dos dados",
  "errors": {
    "contaOrigem": "não deve estar em branco",
    "valor": "deve ser maior que zero"
  },
  "path": "/transferencias"
}
```

---

## 5. Conflito de Concorrência

### ❌ Cenário de Erro: Optimistic Locking
```
Usuário A → POST /transferencias (versão 1)
Usuário B → POST /transferencias (versão 1) ← Simultaneamente

API → 409 CONFLICT (para um dos usuários)
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "A operação falhou devido a um conflito de concorrência. Por favor, tente novamente.",
  "path": "/transferencias"
}
```

---

## 6. Erro Interno

### ❌ Cenário de Erro: Exceção Não Tratada
```
Cliente → POST /transferencias
{
  "contaOrigem": "12345",
  "contaDestino": "67890",
  "valor": 500.00
}

[Erro inesperado no servidor]

API → 500 INTERNAL SERVER ERROR
{
  "timestamp": "2025-12-27T19:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Ocorreu um erro interno no servidor.",
  "path": "/transferencias"
}
```

---

## Resumo Visual

```
┌─────────────────────────────────────────────────────────────┐
│                    CÓDIGOS HTTP POR CENÁRIO                  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ✅ SUCESSO                                                  │
│  ├─ 200 OK          → Consultas e listagens                 │
│  └─ 201 Created     → Criação de recursos                   │
│                                                              │
│  ❌ ERRO DO CLIENTE                                          │
│  ├─ 400 Bad Request → Regras de negócio violadas            │
│  ├─ 404 Not Found   → Recurso não encontrado                │
│  ├─ 409 Conflict    → Conflito de estado/concorrência       │
│  └─ 422 Unprocessable → Dados inválidos                     │
│                                                              │
│  ❌ ERRO DO SERVIDOR                                         │
│  └─ 500 Internal    → Erro inesperado                       │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Decisões de Design

### Por que 409 Conflict para conta duplicada?
- **409 Conflict** indica que a requisição não pode ser completada devido a um conflito com o estado atual do recurso
- Cadastrar uma conta que já existe é um **conflito de estado**, não um erro de validação
- Diferente de **400 Bad Request**, que indica problemas com a requisição em si

### Por que 201 Created para transferências?
- Transferências criam um novo registro no banco de dados
- **201 Created** é o código semântico correto para criação de recursos
- **200 OK** seria apropriado apenas para operações que não criam recursos

### Por que 422 Unprocessable Entity para validação?
- **422** indica que a sintaxe está correta, mas a semântica está errada
- Diferente de **400**, que pode indicar problemas de sintaxe ou parsing
- Permite distinguir entre erros de formato e erros de conteúdo

### Por que 400 Bad Request para regras de negócio?
- Saldo insuficiente e limite excedido são **regras de negócio**
- A requisição não pode ser processada devido a restrições do domínio
- **400** é apropriado para indicar que a requisição não pode ser atendida
