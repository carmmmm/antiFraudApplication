# Anti-Fraud System

## Overview
This Anti-Fraud System is a comprehensive solution designed to detect and manage fraudulent activities in financial transactions. Developed with an emphasis on security and adaptability, the system leverages advanced fraud detection rules, feedback mechanisms, and transaction history management to provide robust protection against fraudulent behavior. This project demonstrates a deep understanding of modern software development practices, utilizing key technologies and frameworks to deliver a scalable and effective anti-fraud solution.

## Key Features
1. Transaction Management
Real-time Transaction Validation: Validates incoming transactions based on predefined rules and heuristics.
Enhanced Fraud Detection: Incorporates correlation-based rules to detect suspicious activities across multiple transactions and regions.
2. Rule-Based Fraud Detection
Rule Correlation: Implements sophisticated rules to categorize transactions as ALLOWED, MANUAL_PROCESSING, or PROHIBITED based on historical data.
Regional and IP Address Correlation: Detects anomalies by analyzing transaction patterns from different regions and IP addresses.
3. Feedback Mechanism
Dynamic Rule Adjustment: Adapts fraud detection rules based on manual feedback from SUPPORT specialists.
Limit Adjustment: Automatically adjusts detection limits using feedback to refine the fraud detection algorithms.
4. Transaction History Management
Comprehensive History Tracking: Maintains a complete history of all transactions, including their validation results and feedback.
Detailed History Endpoints: Provides endpoints to retrieve transaction history by card number or view the entire transaction history.

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
* Transaction Validation: A transaction with an amount above the threshold might be categorized as MANUAL_PROCESSING and later reviewed based on feedback.
* Feedback Handling: Adjusts detection limits dynamically based on feedback from SUPPORT specialists, refining the system’s accuracy.
* History Retrieval: Easily access transaction records to review past activities and audit transactions for anomalies.
## Installation and Setup
Clone the Repository

bash
Copy code
git clone <repository-url>
Navigate to the Project Directory

bash
Copy code
cd <project-directory>
Build the Project

bash
Copy code
./mvnw clean install
Run the Application

bash
Copy code
./mvnw spring-boot:run
Access the Application

Open your browser and navigate to http://localhost:8080.
