# Anti-Fraud System

## Overview
This Anti-Fraud System is a comprehensive solution designed to detect and manage fraudulent activities in financial transactions. Developed with an emphasis on security and adaptability, the system leverages advanced fraud detection rules, feedback mechanisms, and transaction history management to provide robust protection against fraudulent behavior. The project showcases a robust implementation using modern technologies such as Spring Boot, Jakarta Persistence (JPA), and an H2 database.

## Key Features
### 1. Fraud Detection Rules
The system employs heuristic rules to assess transaction validity, considering multiple factors:

* Chrono Clock Logic: Transactions are correlated based on their timestamps. This allows for detecting suspicious activities over time.
* IP Address Correlation: Flags transactions that originate from multiple IP addresses within a short timeframe.
* Regional Analysis: Identifies transactions from various regions to spot anomalies.
### 2. Role-Based Access Control
The system incorporates a role-based access control model to manage different user permissions:

* Anonymous: No access.
* MERCHANT: Limited to transaction creation.
* ADMINISTRATOR: Full control over user roles and transactions.
* SUPPORT: Manages feedback and transaction history.
### 3. Feedback Mechanism
A manual feedback system allows SUPPORT specialists to adjust fraud detection parameters:

* ALLOWED: Indicates a transaction is legitimate.
* MANUAL_PROCESSING: Requires further review.
* PROHIBITED: Marks a transaction as fraudulent.
* Feedback updates the detection limits according to the following formulas:
  
```
Increase Limit: new_limit = ceil(0.8 * current_limit + 0.2 * value_from_transaction)
Decrease Limit: new_limit = ceil(0.8 * current_limit - 0.2 * value_from_transaction)
```

### 4. Transaction History
Tracks all transactions, including those marked as PROHIBITED, and supports querying by card number or retrieving the entire history.

## Technologies Used
* Spring Boot: Framework for building the RESTful APIs and managing application configurations.
* Spring Security: Handles authentication and authorization to ensure secure access control.
* Jakarta Persistence: Manages data persistence with robust ORM capabilities.
* H2 Database: Lightweight in-memory database used for transaction data storage.
* Java: The core programming language used for developing the backend logic.
* RESTful API: Design pattern used for building scalable and maintainable web services.

## Endpoints
#### Transaction Validation
* POST /api/antifraud/transaction: Submit a new transaction for validation.
* PUT /api/antifraud/transaction: Submit feedback for a transaction and adjust detection limits.
#### Transaction History
* GET /api/antifraud/history: Retrieve the complete transaction history.
* GET /api/antifraud/history/{number}: Retrieve transaction history for a specific card number.
#### User Management
* POST /api/auth/user: Create a new user with specific roles.
* DELETE /api/auth/user: Remove a user from the system.
* GET /api/auth/list: List all users in the system.
## Example Scenarios
### Transaction Validation: 
A transaction with an amount above the threshold might be categorized as MANUAL_PROCESSING and later reviewed based on feedback.
POST /api/antifraud/transaction
Request Body:

  ```
{
  "amount": 210,
  "ip": "192.168.1.1",
  "number": "4000008449433403",
  "region": "EAP",
  "date": "2022-01-22T16:04:00"
}
  ```
Response:
```
{
  "result": "MANUAL_PROCESSING",
  "info": "amount"
}
```
### Feedback Handling: 
Adjusts detection limits dynamically based on feedback from SUPPORT specialists, refining the system’s accuracy.
PUT /api/antifraud/transaction

Request Body:
```
{
  "transactionId": 1,
  "feedback": "ALLOWED"
}
```

Response:

```
{
  "transactionId": 1,
  "amount": 210,
  "ip": "192.168.1.1",
  "number": "4000008449433403",
  "region": "EAP",
  "date": "2022-01-22T16:04:00",
  "result": "MANUAL_PROCESSING",
  "feedback": "ALLOWED"
}

```

### History Retrieval: 
Easily access transaction records to review past activities and audit transactions for anomalies.

GET /api/antifraud/history/4000008449433403
 
Response:

```
[
  {
    "transactionId": 1,
    "amount": 210,
    "ip": "192.168.1.1",
    "number": "4000008449433403",
    "region": "EAP",
    "date": "2022-01-22T16:04:00",
    "result": "MANUAL_PROCESSING",
    "feedback": ""
  }
]
```
  
## Installation and Setup
### Clone the Repository

```
git clone https://github.com/carmmmm/antiFraudApplication.git
```

### Navigate to the Project Directory

```
cd antiFraudApplication
```

### Build the Project

```
./mvnw clean install
```
### Run the Application

```
./mvnw spring-boot:run
```
### Access the Application

Open your browser and navigate to http://localhost:28852 or use postman to send and recieve requests. 
