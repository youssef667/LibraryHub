package model;

import java.sql.Date;

/**
 * Plain data object representing one row of the Loans table.
 * Holds no SQL and no business logic — no due-date or fine logic here.
 * That all lives in LoanService, which computes due dates and fines
 * on top of the raw loan_date / return_date stored in the DB.
 *
 * Matches schema.sql exactly:
 *   loan_id     INT AUTO_INCREMENT PRIMARY KEY
 *   member_id   INT NOT NULL   -> FOREIGN KEY REFERENCES Members(member_id)
 *   book_id     INT NOT NULL   -> FOREIGN KEY REFERENCES Books(book_id)
 *   loan_date   DATE NOT NULL
 *   return_date DATE           (nullable — NULL means the book is still out)
 */
public class Loan {

    private int loanId;
    private int memberId;
    private int bookId;
    private Date loanDate;
    private Date returnDate;

    /**
     * Full constructor — used when a loan is loaded FROM the database,
     * where the ID is already known. returnDate may be null
     * (book not yet returned).
     */
    public Loan(int loanId, int memberId, int bookId, Date loanDate, Date returnDate) {
        this.loanId = loanId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    /**
     * No-ID constructor — used when creating a NEW loan (issuing a book).
     * loan_id is assigned by MySQL (AUTO_INCREMENT); return_date starts
     * as null since the book has not been returned yet.
     */
    public Loan(int memberId, int bookId, Date loanDate) {
        this.memberId = memberId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.returnDate = null;
    }

    // --- Getters and setters ---

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Convenience check: a loan is still open (book not yet returned)
     * when return_date is null. Simple enough to keep on the model
     * without counting as "business logic" — LoanService still owns
     * due-date and fine calculations.
     */
    public boolean isReturned() {
        return returnDate != null;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "loanId=" + loanId +
                ", memberId=" + memberId +
                ", bookId=" + bookId +
                ", loanDate=" + loanDate +
                ", returnDate=" + returnDate +
                '}';
    }
}