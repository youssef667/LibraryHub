package service;

import dao.LoanDAO;
import model.Book;
import model.Loan;
import model.Member;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Business logic layer for borrowing and returning books.
 *
 * This is the one class that's allowed to know about all three entities
 * (Books, Members, Loans) and coordinate across them — that coordination
 * is exactly what a "service" layer is for. BookService and MemberService
 * deliberately stay ignorant of each other and of Loans; LoanService is
 * where those boundaries meet.
 *
 * Due dates and fines are calculated here in Java, not stored in the DB —
 * the schema only stores loan_date and return_date.
 */
public class LoanService {

    // Policy constants — change these two lines to adjust library rules.
    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 0.50;

    private final LoanDAO loanDAO;
    private final BookService bookService;
    private final MemberService memberService;

    public LoanService() {
        this.loanDAO = new LoanDAO();
        this.bookService = new BookService();
        this.memberService = new MemberService();
    }

    /**
     * Issues a book to a member, if — and only if — all of these hold:
     *   1. The member exists.
     *   2. The book exists.
     *   3. The book is currently available.
     *
     * On success: inserts the Loan row AND flips the book to unavailable.
     * These two writes are treated as one logical action even though
     * they're two separate DAO calls — that coordination is LoanService's job.
     *
     * Returns the generated loan_id, or -1 if any rule failed.
     */
    public int issueBook(int memberId, int bookId) {
        Member member = memberService.getMemberById(memberId);
        if (member == null) {
            System.out.println("No member found with ID " + memberId + ".");
            return -1;
        }

        Book book = bookService.getBookById(bookId);
        if (book == null) {
            System.out.println("No book found with ID " + bookId + ".");
            return -1;
        }

        if (!book.isAvailable()) {
            System.out.println("\"" + book.getTitle() + "\" is not currently available.");
            return -1;
        }

        Loan loan = new Loan(memberId, bookId, Date.valueOf(LocalDate.now()));
        int loanId = loanDAO.issueLoan(loan);

        if (loanId == -1) {
            System.out.println("Failed to issue book due to a database error.");
            return -1;
        }

        // Two-table update: the loan row exists now, so the book must
        // be marked unavailable to match. If this second write failed,
        // the data would be inconsistent — flagged below rather than
        // silently ignored.
        boolean flipped = bookService.markAsBorrowed(bookId);
        if (!flipped) {
            System.out.println("Warning: loan was recorded but book availability was not updated.");
        }

        return loanId;
    }

    /**
     * Returns a book that's currently on loan.
     *
     * Looks up the book's active (open) loan, closes it by setting
     * return_date to today, then flips the book back to available.
     * Also reports any overdue fine owed for this loan.
     *
     * Returns true if the return was processed successfully.
     */
    public boolean returnBook(int bookId) {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            System.out.println("No book found with ID " + bookId + ".");
            return false;
        }

        Loan activeLoan = loanDAO.getActiveLoanByBook(bookId);
        if (activeLoan == null) {
            System.out.println("\"" + book.getTitle() + "\" is not currently on loan.");
            return false;
        }

        Date today = Date.valueOf(LocalDate.now());
        boolean updated = loanDAO.returnLoan(activeLoan.getLoanId(), today);

        if (!updated) {
            System.out.println("Failed to process return due to a database error.");
            return false;
        }

        boolean flipped = bookService.markAsReturned(bookId);
        if (!flipped) {
            System.out.println("Warning: loan was closed but book availability was not updated.");
        }

        double fine = calculateFine(activeLoan.getLoanDate(), today);
        if (fine > 0) {
            System.out.printf("This book was returned late. Fine due: $%.2f%n", fine);
        } else {
            System.out.println("Returned on time — no fine.");
        }

        return true;
    }

    /**
     * Returns every currently open loan, formatted as a simple report
     * of which books are out, to whom, and whether they're overdue.
     * Main will print this; LoanService just gathers/labels the data.
     */
    public List<Loan> getAllActiveLoans() {
        return loanDAO.getAllActiveLoans();
    }

    /**
     * Returns a member's full borrowing history (past and present loans).
     */
    public List<Loan> getLoanHistoryForMember(int memberId) {
        return loanDAO.getLoansByMember(memberId);
    }

    /**
     * Computes the due date for a loan: loan_date + LOAN_PERIOD_DAYS.
     * Public so Main/reports can display it without duplicating the policy.
     */
    public LocalDate getDueDate(Date loanDate) {
        return loanDate.toLocalDate().plusDays(LOAN_PERIOD_DAYS);
    }

    /**
     * Checks whether an open loan is currently overdue, as of today.
     */
    public boolean isOverdue(Loan loan) {
        if (loan.isReturned()) {
            return false;
        }
        return LocalDate.now().isAfter(getDueDate(loan.getLoanDate()));
    }

    /**
     * Calculates the fine owed for a loan, given its loan date and the
     * date it was (or would be) returned.
     *
     * Fine = days late * FINE_PER_DAY, or 0 if returned on/before the due date.
     * Kept package-private-ish (public, but only meaningful internally
     * and to Main for previewing a fine before a return is confirmed).
     */
    public double calculateFine(Date loanDate, Date returnDate) {
        LocalDate dueDate = getDueDate(loanDate);
        LocalDate actualReturn = returnDate.toLocalDate();

        long daysLate = ChronoUnit.DAYS.between(dueDate, actualReturn);
        if (daysLate <= 0) {
            return 0.0;
        }
        return daysLate * FINE_PER_DAY;
    }
}