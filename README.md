# 🏦 Transferência Bancária API 🚀

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-24.0-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-3.0-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

> **"Code is like humor. When you have to explain it, it’s bad."** – Cory House

Bem-vindo ao **Core Banking Transfer System**. Este projeto é uma implementação robusta e escalável de um sistema de transferências financeiras, arquitetado meticulosamente sobre os princípios da **Clean Architecture** (Arquitetura Hexagonal).

Aqui, performance, segurança e consistência não são opcionais.

---

## ⚡ Mainframe Capabilities (Funcionalidades)

### 👤 Customer Management Module
*   **Onboarding**: Registro de novos correntistas com validação estrita.
*   **Query Interface**: Listagem rápida de base de usuários.
*   **Deep Search**: Busca indexada por ID de conta.

### 💸 Transaction Engine
O coração do sistema. Um motor de processamento transacional ACID-compliant.
*   **P2P Transfers**: Movimentação entre accounts com locking otimista.
*   **Compliance Check**: Engine de regras que rejeita automaticamente transações acima de **R$ 10k** (Anti-Fraud/Policy).
*   **Balance Validation**: Checagem de fundos em tempo real.
*   **Account Verification**: Validação de integridade de origem e destino.
*   **Audit Log**: Persistência de falhas com reason codes detalhados para debug e auditoria.
*   **Atomic Operations**: Garantia total de consistência; ou tudo acontece, ou nada acontece.

### 📜 Ledger (Histórico)
*   Consulta cronológica de movimentações, fornecendo rastreabilidade total.

---

## 🛠 Tech Stack & Arsenal

| Tecnologia | Função | Power Level |
| :--- | :--- | :--- |
| **Java 21** | Core Language | LTS |
| **Spring Boot 3** | Framework | High Velocity |
| **Arquitetura Hexagonal** | Design Pattern | Decoupled & Testable |
| **MySQL** | Database | Relational Integrity |
| **Docker Compose** | Orchestration | Containerized |
| **OpenAPI / Swagger** | Documentation | Standardized |
| **JUnit 5 / MockMvc** | Testing | Quality Assurance |

---

## 🖥️ System Initialization (Como Rodar)

### 0. Pré-requisitos (The Gear)
*   **JDK 21+** (No old school Java 8 here)
*   **Docker** (Para subir a infraestrutura com um comando)
*   **Maven** (Build system)

### 1. Cloning the Source
```bash
git clone <url-do-master-repo>
cd transferencia
```

### 2. Infrastructure Spin-up (Docker)
Inicialize o banco de dados e dependências sem sujar sua máquina local.
```bash
docker-compose up -d
```
*Aguarde o MySQL estar pronto para conexões na porta `3306`.*

### 3. Deploy Application
**Linux/Mac (Unix Power):**
```bash
./mvnw spring-boot:run
```
**Windows (PowerShell):**
```powershell
./mvnw.cmd spring-boot:run
```

---

## 📡 Control Center (Endpoints)

O sistema expõe uma API RESTful completa. A documentação interativa está disponível no **Swagger UI**.

👉 **Access Point:** `http://localhost:8081/transferencia-api/swagger-ui.html`

> *Explore, teste payloads e valide respostas em tempo real.*

---

## 🧪 Testing Lab

Nossa suíte de testes garante que nenhum bug sobreviva. Utilizamos H2 Database em memória para testes de integração ultra-rápidos.

```bash
# Executar a suíte de testes
./mvnw test
```

---

## 🧩 Architectural Blueprints

O projeto segue estritamente a **Arquitetura Hexagonal (Ports and Adapters)** para garantir que o núcleo da aplicação (Domain) permaneça isolado de tecnologias externas.

```
src/main/java/com/itau/app/transferencia
├── 🧠 domain          # O santuário. Regras de negócio puras (Entities, Exceptions, Ports).
├── 📦 application     # O orquestrador. Services que implementam UseCases.
└── 🔌 infrastructure  # O mundo real. Controllers, Repository Implementations, Configs.
```

---

## 👨‍💻 Contribuição

Pull Requests são bem-vindos. Para mudanças maiores, abra uma issue primeiro para discutir o que você gostaria de mudar.

> **"Talk is cheap. Show me the code."** – Linus Torvalds

---
*Built with ❤️ and ☕ by a Software Engineer.*
