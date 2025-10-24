# Online-Banking-Application
A console-based banking application built with Java and MySQL, providing a command-line interface for managing bank accounts and transactions.

# Features

* Account Management

Create new bank accounts with initial balance
Automatic account ID generation
View account balance and details


* Transaction Operations

Deposit money into accounts
Withdraw money with balance validation
Real-time balance updates
Transaction confirmations


* Database Integration

MySQL database connectivity using JDBC
Automatic table creation on first run
Secure PreparedStatement queries to prevent SQL injection
Persistent data storage

# Technologies Used

* Java - Core programming language
* MySQL - Database management system
* JDBC - Java Database Connectivity
* Scanner - Console input handling

# Prerequisites
Before running this application, ensure you have:

* Java Development Kit (JDK) 8 or higher
* MySQL Server installed and running
* MySQL Connector/J (JDBC Driver)

# Configure Database Connection

* Open BankingApp.java and update the following lines with your MySQL credentials:
javastatic final String DB_URL = "jdbc:mysql://localhost:3306/banking_db";
static final String DB_USER = "root";
static final String DB_PASSWORD = "your_password";

* Add MySQL JDBC Driver
Download and add to classpath:

* Download MySQL Connector/J from MySQL official website
Add the .jar file to your project classpath

## When you run the application, you'll see a menu:
=== Banking Application ===
1. Create Account
2. Deposit Money
3. Withdraw Money
4. Check Balance
5. Exit
Choose an option:
