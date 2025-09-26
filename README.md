#  💰 Expense Tracker (Java + MySQL + Swing)
A simple **Expense Tracker Application** built with **Java (Swing)** and **MySQL**, where users can manage categories and expenses.  
The project demonstrates **JDBC database connectivity**, **DAO design pattern**, and a clean separation of model and database logic.  

# 🎯 Features
- Manage expense **categories** (Food, Travel, Shopping, etc.)
- Add **expenses** with description, amount, and category
- Auto-generated `created_at` and `updated_at` timestamps
- MySQL database integration
- Swing GUI (extensible)

## 🗄️ Database Setup

### 1. Create Database
sql
CREATE DATABASE expenseTracker;
USE expenseTracker;

CREATE TABLE category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(255) UNIQUE NOT NULL
);


CREATE TABLE expense (
    id INT PRIMARY KEY AUTO_INCREMENT,
    amount INT NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    category_id INT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);

# 🛠️ Technologies Used
- Java 17+ (or compatible version)

= MySQL 8+

- Maven (if using dependencies)

- JDBC driver (mysql-connector-j)

# 📁 Project Structure

expense-tracker/
│── src/
│   ├── main/java/com/tracker
│   │   ├── model/
│   │   │   └── Expense.java, Category.java
│   │   ├── dao/
│   │   │   └── ExpenseDAO.java, CategoryDAO.java
│   │   ├── gui/
│   │   │   └── MainGUI.java
│   │   └── DatabaseConnection.java
│   └──-|
│       └── Main.java
│── pom.xml
│── README.md

# 🧑‍💻 Author
Mohamed Asharaf
