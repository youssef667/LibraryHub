# 📚 LibraSys — Console Library Management System

A console-based Library Management System built with **Java** and **SQL** to practice core backend and database concepts: JDBC connectivity, CRUD operations, relational schema design, and layered application architecture (Model–DAO–Service–Main).

No GUI — pure command-line interaction with a persistent SQL database.

---

## Features

- **Book Management** — add, update, delete, and search books by title, author, or ISBN
- **Member Management** — register members and view their borrowing history
- **Borrow / Return System** — issue and return books with automatic availability updates
- **Due Dates & Fines** — calculate overdue fines based on return delay
- **Input Validation** — prevents invalid operations (e.g., borrowing an unavailable book)
- **Persistent Storage** — all data stored in a relational SQL database via JDBC

---

## Tech Stack

- **Language:** Java (JDK 11+)
- **Database:** MySQL (or any JDBC-compatible SQL database)
- **Connectivity:** JDBC
- **Interface:** Command-line (System.in / Scanner)

---

## Architecture

```
src/
├── model/     → Book, Member, Loan classes (POJOs)
├── dao/       → Database access layer (JDBC queries)
├── service/   → Business logic (borrow rules, fine calculation)
└── Main.java  → Console menu and program entry point
```

This separation keeps SQL queries, business rules, and user interaction isolated — a small-scale example of layered architecture.

---

## Database Schema

```sql
CREATE TABLE Books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    author VARCHAR(100),
    isbn VARCHAR(20) UNIQUE,
    total_copies INT,
    available_copies INT
);

CREATE TABLE Members (
    member_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE
);

CREATE TABLE Loans (
    loan_id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT,
    member_id INT,
    issue_date DATE,
    due_date DATE,
    return_date DATE,
    FOREIGN KEY (book_id) REFERENCES Books(book_id),
    FOREIGN KEY (member_id) REFERENCES Members(member_id)
);
```

---

## Getting Started

```bash
# 1. Clone the repo
git clone https://github.com/yourusername/librasys.git
cd librasys

# 2. Create the database
mysql -u root -p < database/schema.sql

# 3. Set your DB credentials in src/dao/DBConnection.java

# 4. Compile and run
javac -d bin src/**/*.java
java -cp bin Main
```

---

## Example Console Menu

```
=== LibraSys ===
1. Add Book
2. Search Book
3. Register Member
4. Issue Book
5. Return Book
6. View Overdue Fines
0. Exit
Choose an option:
```

---

## What This Project Demonstrates

- Writing raw SQL and executing it from Java via JDBC (PreparedStatements, ResultSets)
- Designing a normalized relational schema with foreign keys
- Applying separation of concerns (DAO / Service / Model layers)
- Handling exceptions and edge cases (unavailable books, invalid input)
- Basic date logic for due dates and fine calculation

---

## License
MIT License