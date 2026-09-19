USE librasys;

-- =========================
-- Members
-- =========================

CREATE TABLE Members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20)
);


-- =========================
-- Books
-- =========================

CREATE TABLE Books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    available BOOLEAN DEFAULT TRUE
);


-- =========================
-- Loans
-- =========================

CREATE TABLE Loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,

    member_id INT NOT NULL,
    book_id INT NOT NULL,

    loan_date DATE NOT NULL,
    return_date DATE,

    FOREIGN KEY (member_id)
        REFERENCES Members(member_id),

    FOREIGN KEY (book_id)
        REFERENCES Books(book_id)
);
USE librasys;

SHOW TABLES;

DESCRIBE Members;
DESCRIBE Books;
DESCRIBE Loans;
SHOW CREATE TABLE Loans;
SHOW TABLES;