# 🏨 SISHOTEL API - Checkpoint 2

API REST para **gestão de reservas de hotel**, cobrindo o ciclo de vida completo:  
**Reserva → Check-in → Check-out.**

Desenvolvido em **Java 21** com **Spring Boot 3**, seguindo arquitetura em **3 camadas (Controller, Service, Repository)**, com **migrações versionadas (Flyway)** e **documentação de API (Swagger)**.

---

## Integrantes (Turma 3ESPY - 2025)

| Nome | RM |
| :--- | :--- |
| Fabrício Saavedra | 97631 |
| Guilherme Akio | 98582 |
| Guilherme Morais | 551981 |
| Açussena Macedo Mautone | 552568 |

## 🚀 Funcionalidades Principais

### 👤 Gestão de Hóspedes
- CRUD completo de hóspedes.  
- Validação de documento e e-mail únicos.

### 🏠 Gestão de Quartos
- CRUD completo de quartos.  
- Validação de número único.  
- "Soft delete" (inativação).

### 📅 Gestão de Reservas
- CRUD completo de quartos.  
- Validação de datas, capacidade e valor.  

### 🔁 Fluxo de Estadia (Máquina de Estados - Regra 4)
- **Check-in:** Valida status `CREATED` e data (Regra 5).  
- **Check-out:** Valida status `CHECKED_IN` e calcula valor final (Regra 6).  
- **Cancelamento:** Valida status `CREATED`.

### ⚙️ Tratamento de Exceções
- Respostas de erro padronizadas (`400`, `404`, `409`, `422`) com `@ControllerAdvice`.

### 📘 Documentação
- API documentada com **Swagger (OpenAPI)**.

### 🗄️ Banco de Dados
- Schema e dados iniciais versionados via **Flyway**.

---

## 🧰 Tecnologias Utilizadas

- Java 21  
- Spring Boot 3  
- Spring Data JPA (Hibernate)  
- H2 Database (em memória)  
- Flyway Migration  
- SpringDoc OpenAPI (Swagger)  
- Jakarta Bean Validation  
- Lombok  
- Maven

---

## ⚙️ Pré-requisitos

- **JDK 21** ou superior  
- **Apache Maven 3.8** ou superior

---

## ▶️ Como Executar Localmente

### Via Linha de Comando (Maven)

Na pasta raiz do projeto (onde está o `pom.xml`), execute:

```bash
mvn spring-boot:run
```

### Via IDE (IntelliJ, Eclipse, etc.)

1. Abra o projeto na sua IDE.  
2. Aguarde o Maven baixar as dependências.  
3. Localize a classe principal:  
   `src/main/java/com/sishotel/hotel_api/HotelApiApplication.java`  
4. Execute o método `main()` dessa classe.

A aplicação estará disponível em:  
👉 **http://localhost:8080**

---

## 🗃️ Acesso ao Banco (H2 Console)

A aplicação utiliza um banco **H2 em memória**, acessível enquanto estiver rodando:

- **URL:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

**Configurações de Login:**
```
Driver Class: org.h2.Driver
JDBC URL: jdbc:h2:mem:sishotel
User Name: sa
Password: password
```

📌 *O schema e os dados iniciais são criados automaticamente pelo Flyway (V1__Create_Initial_Schema.sql).*

---

## 📖 Documentação da API (Swagger)

A documentação completa da API (endpoints e DTOs) está disponível em:  
👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🔗 Principais Endpoints

### 👤 Hóspedes (`/api/guests`)
| Método | Endpoint | Descrição |
|--------|-----------|-----------|
| GET | `/` | Lista todos os hóspedes |
| GET | `/{id}` | Busca hóspede por ID |
| POST | `/` | Cria um novo hóspede |
| PUT | `/{id}` | Atualiza um hóspede |
| DELETE | `/{id}` | Exclui (valida reservas ativas) |

### 🏠 Quartos (`/api/rooms`)
| Método | Endpoint | Descrição |
|--------|-----------|-----------|
| GET | `/` | Lista todos os quartos |
| GET | `/{id}` | Busca quarto por ID |
| POST | `/` | Cria novo quarto |
| PUT | `/{id}` | Atualiza quarto |
| DELETE | `/{id}` | Inativa quarto (soft delete) |

### 📅 Reservas (`/api/reservations`)
| Método | Endpoint | Descrição |
|--------|-----------|-----------|
| POST | `/` | Cria nova reserva |
| GET | `/` | Lista todas as reservas |
| GET | `/{id}` | Busca reserva por ID |
| POST | `/{id}/checkin` | Realiza check-in |
| POST | `/{id}/checkout` | Realiza check-out |
| POST | `/{id}/cancel` | Cancela reserva |

---

## 🧩 Regras de Negócio

| Regra | Descrição | Status HTTP | Implementação |
|--------|------------|--------------|----------------|
| **1** | `checkoutExpected > checkinExpected` | 400 | `InvalidDateRangeException` |
| **2** | Impede sobreposição de datas no mesmo quarto | 409 | `ResourceConflictException` |
| **3** | Nº hóspedes ≤ capacidade do quarto | 400 | `CapacityExceededException` |
| **4** | Máquina de estados (CREATED → CHECKED_IN → CHECKED_OUT / CANCELED) | 409 | `InvalidReservationStateException` |
| **5** | Check-in apenas no dia esperado | 422 | `InvalidCheckinDateException` |
| **6** | Cálculo de valor estimado e final | - | `ReservationService.calculateFinalAmount()` |
| **7** | Quartos e hóspedes com reservas ativas não podem ser removidos | 409 | `RoomService`, `GuestService` |
