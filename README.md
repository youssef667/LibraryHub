# 📚 Library Management System

A desktop application for managing library operations — books, members, borrowing, and returns — built with **Java** and a **relational SQL database**. Designed to replace manual record-keeping with a structured, searchable, rule-enforced system.

---

## Overview

The Library Management System (LMS) allows librarians and staff to manage a book catalog, register and track members, and handle the full borrow/return lifecycle, including due dates and overdue fines. The system enforces business rules at the application layer (e.g., borrowing limits, availability checks) while persisting all data reliably in a normalized SQL database.

---

## Features

### 📖 Book Management
- Add, update, delete, and search books by title, author, ISBN, genre, or category
- Track total copies vs. available copies in real time
- View complete book catalog with filtering and sorting

### 👤 Member Management
- Register new members with contact details and membership type
- Update and deactivate member records
- View a member's borrowing history and current loans

### 🔄 Borrowing & Returns
- Issue books to members with automatic availability checks
- Enforce a maximum number of concurrent loans per member
- Calculate due dates automatically upon issue
- Process returns and update book availability instantly
- Prevent issuing a book with zero available copies

### 💰 Fines & Overdue Tracking
- Automatically calculate overdue fines based on days late
- Track outstanding fines per member
- Block further borrowing until fines are settled (optional rule)

### 🔍 Search & Reports
- Search books/members with SQL-backed queries (title, author, member ID, etc.)
- Generate reports: currently borrowed books, overdue items, most borrowed titles, active members

### 🔐 User Roles & Authentication
- Admin/Librarian login with role-based access control
- Admin: full CRUD access to books, members, and transactions
- Staff: limited access to issue/return operations only

### 🗄️ Data Persistence
- All data stored in a relational SQL database (MySQL/PostgreSQL/SQLite — specify yours)
- JDBC used for database connectivity and transaction management
- Normalized schema (Books, Members, Loans, Fines, Users tables) to avoid data redundancy

---

## Tech Stack

| Layer            | Technology                          |
|-------------------|--------------------------------------|
| Language          | Java (JDK 11+)                      |
| Database          | MySQL / SQL (adjust to your DB)     |
| DB Connectivity   | JDBC                                |
| UI                | Java Swing / JavaFX (adjust)        |
| Build Tool        | Maven / Gradle (adjust)             |
| IDE               | IntelliJ IDEA / Eclipse             |

---

## Database Schema (Summary)

- **Books** — book_id, title, author, isbn, genre, total_copies, available_copies
- **Members** — member_id, name, email, phone, membership_date, status
- **Loans** — loan_id, book_id (FK), member_id (FK), issue_date, due_date, return_date
- **Fines** — fine_id, loan_id (FK), amount, paid_status
- **Users** — user_id, username, password_hash, role

*(Include an ER diagram image here — e.g. `docs/er-diagram.png`)*

---

## Getting Started

### Prerequisites
- JDK 11 or higher
- MySQL Server (or your chosen SQL database)
- Maven (if used for dependency management)

### Setup
```bash
# Clone the repository
git clone https://github.com/yourusername/library-management-system.git
cd library-management-system

# Import the database schema
mysql -u root -p library_db < database/schema.sql

# Configure database credentials
# Edit src/config/DBConnection.java with your DB URL, username, and password

# Build and run
mvn clean install
mvn exec:java -Dexec.mainClass="com.library.Main"
```

---

## Project Structure

```
library-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/library/
│   │   │   ├── model/          # Book, Member, Loan, Fine, User classes
│   │   │   ├── dao/            # Data Access Objects (JDBC queries)
│   │   │   ├── service/        # Business logic layer
│   │   │   ├── ui/             # GUI classes
│   │   │   └── Main.java
│   │   └── resources/
├── database/
│   └── schema.sql              # Full DB schema + sample data
├── docs/
│   └── er-diagram.png
└── README.md
```

---

## Future Improvements
- REST API layer for remote/web access
- Email notifications for due/overdue books
- Barcode/QR scanning for check-in/check-out
- Unit tests with JUnit + Mockito
- Dockerized deployment

