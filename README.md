# Bank Management Web System (Spring Boot & React)

This project is a modern, high-fidelity migration of a legacy Java Swing-based ATM Desktop Application into a multi-tier web application using a **Spring Boot REST API** (Java) and a **React Single-Page Application** (Vite). 

The system preserves all legacy database structures, query formats, transaction styles, and flow conditions, while wrapping them in a premium glassmorphic dark-theme visual portal.

---

## Project Structure

```
bank-management-web/
├── backend/                  # Spring Boot REST API
│   ├── pom.xml               # Maven configuration (Java 17, Web, JDBC, MySQL, H2, JUnit 5)
│   └── src/
│       ├── main/java/        # API Controllers, Database Services, and Bootstraps
│       └── main/resources/   # App properties and database schemas (schema.sql)
├── frontend/                 # React Vite Client
│   ├── package.json          # Node modules and scripts configuration
│   ├── src/                  # React source files (App Router, index.css, Components)
│   └── public/               # Public assets
└── README.md                 # Project Setup & Execution manual (This file)
```

---

## Technical Stack & Features

### Backend (Spring Boot 3.2.5)
- **Spring Web**: Exposes clean, validation-safe REST controllers.
- **Spring Data JPA & Hibernate**: Integrated alongside JDBC for advanced transactional entity mapping and query configuration.
- **Pessimistic Concurrency Locking**: Implements row-level locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) on credentials session data to block concurrent ATM withdrawal race conditions and eliminate double-spending risks.
- **PCI-DSS Compliance Cryptography**: Secures customer PAN and Aadhar details at rest using AES-256-CBC encryption, and hashes PIN logins using BCrypt to satisfy financial data protection requirements.
- **Event-Sourced Transaction Ledger**: Transaction actions (Deposits, Withdrawals, Fast Cash) are processed as immutable Event streams published through an internal application message broker and recorded in the database event store. Balances are derived dynamically by replaying the event logs.
- **Spring JDBC (`JdbcTemplate`)**: Replicates the exact raw SQL database updates and structural parameters matching the legacy desktop configuration.
- **H2 In-Memory & MySQL support**: Easily switchable via Spring profile configurations (`spring.profiles.active=dev` vs `prod`).
- **CORS Config**: Configured cross-origin resource sharing policies for React application bindings.
- **JUnit 5 Integration Tests**: Includes automated multi-threaded test suites validating lock execution latency and event delivery compliance.

### Frontend (React + Vite)
- **State Router**: Implements layout toggles mimicking JFrame visibility transitions (`.setVisible(false)`).
- **Vanilla CSS**: Styled with variable tokens, modern dark elements, and subtle hover animations.
- **CRT ATM Console Screen**: Deposit, Withdrawal, Fast Cash, Balance, and PIN Change views render inside a glowing retro green CRT console frame.
- **Credentials Receipt**: Registration step 3 shows credentials in a credit-card modal.
- **Mini Statement**: Renders transaction logs on a pink printed receipt (matching legacy Swing's receipt color).

---

## Database Schemas & Data Security (PCI-DSS Compliance)

The backend automatically manages schemas on startup via `schema.sql` and includes a self-healing **`DatabaseSchemaMigrationRunner`** (which alters pre-existing database columns for secure hash sizes without data loss).

### Table Schema and Cryptography Rules

1. **`signup`**: Stores personal registration details.
2. **`Signuptwo`**: Stores additional details. **PCI-DSS Compliance Layer**: The `pan` (PAN number) and `addhar` (Aadhar number) values are encrypted at rest using AES-256-CBC with standard PKCS5 padding.
3. **`signupthree`**: Stores account types and services. **Security Layer**: The PIN column stores the salted BCrypt hash of the user's PIN.
4. **`login`**: Maps login credentials. 
   - **`pin`**: Stores the salted BCrypt hash of the PIN.
   - **`pin_lookup_hash`**: Stores a deterministic SHA-256 hash of the PIN (used to safely link transactions without exposing plain-text keys).
5. **`bank`**: Core ledger event log.
   - **`pin`**: Stores the deterministic SHA-256 hash of the transaction PIN (protecting customer identity at rest).
   - **Ledger Invariants**: Dynamic balance computation sums deposits and subtracts withdrawals by querying this table. All legacy quirks are preserved (e.g. capital `'Withdrawl'` vs lowercase `'withdrawl'` for fast cash).


---

## Prerequisites
Ensure the following tools are installed on your machine:
1. **Java Development Kit (JDK) 17 or higher**
2. **Apache Maven 3.6+**
3. **Node.js (v18+) & npm**
4. *(Optional)* **MySQL Server** (running on port `3306`)

---

## Execution Guide

### 1. Run the Spring Boot Backend

1. Navigate to the `backend` folder:
   ```bash
   cd backend
   ```

2. **Select Database Profile**:
   Open `src/main/resources/application.properties`. You can toggle the active profile between `prod` (MySQL) and `dev` (H2 In-Memory):
   ```properties
   # To use MySQL (Requires a local MySQL server with AyushVish password):
   spring.profiles.active=prod
   
   # To use H2 (No local database required, sandbox mode):
   spring.profiles.active=dev
   ```

3. **Start the Application**:
   Run the project using Maven:
   ```bash
   mvn spring-boot:run
   ```
   The backend will start on `http://localhost:8080`.

4. **Verify / Run Tests**:
   Execute the JUnit 5 test suite to verify connectivity and utilities:
   ```bash
   mvn test
   ```

---

### 2. Run the React Frontend

1. Navigate to the `frontend` folder:
   ```bash
   cd frontend
   ```

2. **Install Dependencies**:
   ```bash
   npm install
   ```

3. **Start the Dev Server**:
   ```bash
   npm run dev
   ```
   Vite will start the client interface on `http://localhost:5173`.

4. Open `http://localhost:5173` in your browser to start your transactions!

---

### 3. Run Backend via Docker

You can containerize the backend to run it independently without needing Java or Maven pre-installed on your local environment:

1. **Build the Docker Image**:
   Navigate to the `backend` directory and run:
   ```bash
   docker build -t bank-management-backend .
   ```

2. **Run the Container**:
   Start the backend container (exposing port `8080`):
   ```bash
   docker run -d -p 8080:8080 --name bank-backend bank-management-backend
   ```

3. **To Override active profiles (e.g. targeting MySQL instead of in-memory H2)**:
   ```bash
   docker run -d -p 8080:8080 --name bank-backend -e SPRING_PROFILES_ACTIVE=prod bank-management-backend
   ```

