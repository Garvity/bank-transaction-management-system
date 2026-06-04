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
- **Spring JDBC (`JdbcTemplate`)**: Replicates the exact raw SQL insert, select, and update queries from the original desktop version.
- **MySQL Driver**: Connects to the original `bankSystem` database.
- **H2 Support**: Configured an in-memory dev profile database for easy sandbox runs without local MySQL setup.
- **CORS Config**: Enabled secure origin mappings for local development servers.
- **JUnit 5 Testing**: Unit test suite using JUnit Jupiter explicitly verified.

### Frontend (React + Vite)
- **State Router**: Implements layout toggles mimicking JFrame visibility transitions (`.setVisible(false)`).
- **Vanilla CSS**: Styled with variable tokens, modern dark elements, and subtle hover animations.
- **CRT ATM Console Screen**: Deposit, Withdrawal, Fast Cash, Balance, and PIN Change views render inside a glowing retro green CRT console frame.
- **Credentials Receipt**: Registration step 3 shows credentials in a credit-card modal.
- **Mini Statement**: Renders transaction logs on a pink printed receipt (matching legacy Swing's receipt color).

---

## Database Schemas & Data Integrity

The backend automatically creates the following tables on startup via `schema.sql`:

1. **`signup`**: Form number, personal details (name, DOB, email, marital status, address, city, state).
2. **`Signuptwo`**: Form number, additional details (religion, category, income, occupation, PAN, Aadhar, Senior/Existing status).
3. **`signupthree`**: Form number, account type, card number, PIN, requested services list.
4. **`login`**: Credential entries mapping form number, generated card number, and active PIN.
5. **`bank`**: Transaction logs recording PIN, Date (representation of `java.util.Date`), Type (`'Deposit'`, `'Withdrawl'`, or `'withdrawl'`), and Amount.

> [!NOTE]
> All legacy database quirks are preserved exactly—such as the lowercase `'withdrawl'` tag inserted during Fast Cash versus the Capital `'Withdrawl'` tag inserted in Custom Withdrawals, ensuring running balances sum correctly.

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
