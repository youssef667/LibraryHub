package dao;

import model.Loan;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Loans table.
 * Contains only SQL/JDBC logic — no due-date or fine math.
 * That belongs in LoanService, which uses these methods as building blocks.
 * Every method opens its own connection (factory-style) and
 * closes it automatically via try-with-resources.
 */
public class LoanDAO {

    /**
     * Inserts a new loan row — i.e. issues a book to a member.
     * return_date is left NULL (book is currently out).
     * Returns the generated loan_id, or -1 if the insert failed.
     * Note: this does NOT check whether the book is available or flip
     * Books.available — that coordination belongs to LoanService, which
     * calls BookDAO.setAvailability() alongside this method.
     */
    public int issueLoan(Loan loan) {
        String sql = "INSERT INTO Loans (member_id, book_id, loan_date, return_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, loan.getMemberId());
            stmt.setInt(2, loan.getBookId());
            stmt.setDate(3, loan.getLoanDate());
            stmt.setNull(4, java.sql.Types.DATE);

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error issuing loan: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Marks a loan as returned by setting its return_date.
     * Returns true if a row was actually updated.
     * Note: this does NOT flip Books.available back to true —
     * that coordination belongs to LoanService.
     */
    public boolean returnLoan(int loanId, Date returnDate) {
        String sql = "UPDATE Loans SET return_date = ? WHERE loan_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, returnDate);
            stmt.setInt(2, loanId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error returning loan: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves a single loan by its ID.
     * Returns null if no loan with that ID exists.
     */
    public Loan getLoanById(int loanId) {
        String sql = "SELECT * FROM Loans WHERE loan_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loanId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLoan(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching loan: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves every loan (past and present) belonging to a member —
     * their full borrowing history.
     */
    public List<Loan> getLoansByMember(int memberId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Loans WHERE member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapRowToLoan(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching loans for member: " + e.getMessage());
        }
        return loans;
    }

    /**
     * Retrieves the current OPEN loan for a given book, if any
     * (return_date IS NULL). A book can only have one active loan
     * at a time, so this returns a single Loan, or null if the
     * book is not currently checked out.
     * LoanService uses this to find which loan to close when a
     * book is returned.
     */
    public Loan getActiveLoanByBook(int bookId) {
        String sql = "SELECT * FROM Loans WHERE book_id = ? AND return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLoan(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching active loan for book: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves every currently open loan (return_date IS NULL)
     * across all members/books. Used for overdue reports.
     */
    public List<Loan> getAllActiveLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Loans WHERE return_date IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                loans.add(mapRowToLoan(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching active loans: " + e.getMessage());
        }
        return loans;
    }

    /**
     * Maps the current row of a ResultSet to a Loan object.
     * Kept as one private helper so every read method builds
     * Loan objects the same way.
     * rs.getDate() naturally returns null if the column value is SQL NULL,
     * which is exactly what we want for an unreturned loan.
     */
    private Loan mapRowToLoan(ResultSet rs) throws SQLException {
        return new Loan(
                rs.getInt("loan_id"),
                rs.getInt("member_id"),
                rs.getInt("book_id"),
                rs.getDate("loan_date"),
                rs.getDate("return_date")
        );
    }
}