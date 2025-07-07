# CashCompass

## Overview
CashCompass is a modern JavaFX-based desktop application for personal finance management. It allows users to track their incomes and expenses, set monthly saving goals, and visualize their financial data with pie charts. The application uses a PostgreSQL database for persistent storage and supports multiple users with secure password handling.

## Installation & Setup

### Requirements
- Java 17 or newer
- Gradle (or use the included Gradle Wrapper)
- PostgreSQL database

### Setup Steps
1. **Clone the repository**
2. **Configure the database**
   - Create a PostgreSQL database (e.g., `cashcompass`).
   - Create a user and grant access.
   - Update the `src/main/resources/db.properties` file with your database credentials.
3. **Run database migrations**
   - Ensure the required tables (`users`, `months`, `incomes`, `expenses`) exist. (Use `db_schema.sql` as a reference for the schema.)
4. **Build and run the application**
   - Using Gradle Wrapper:
     ```
     ./gradlew run
     ```
   - Or with your IDE (run the `MainFX` class).

## How it works
- On startup, users can create a new account or log in.
- Each user can create and manage multiple months, each with its own starting balance, currency, and saving goal.
- Users can add, view, and delete incomes and expenses for the selected month.
- The application updates balances automatically and stores all data in the PostgreSQL database.
- Visualizations (pie charts) help users understand their spending and income distribution.

## Features
- Multi-user support with secure password hashing (bcrypt)
- Monthly planning with starting balance, saving goal, and currency
- Add, view, and delete incomes and expenses
- Interactive pie charts for income and expense categories
- Modern, animated JavaFX UI with a blue-themed design
- Persistent storage using PostgreSQL

## Limitations
- No support for recurring transactions
- No mobile or web version
- Database schema must be set up manually (no automatic migrations)
- No advanced analytics or budgeting features

## Future Improvements
- Add recurring transactions and reminders
- Add more detailed analytics and reports
- Provide a setup script for automatic database schema creation
- Add support for multiple currencies per user/month
- Convert to a web-based application 

### For feedback or improvements, feel free to contribute! :)