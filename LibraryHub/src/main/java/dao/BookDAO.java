package dao;

import model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Books table.
 * Contains only SQL/JDBC logic — no business rules.
 * Every method opens its own connection (factory-style) and
 * closes it automatically via try-with-resources.
 */
public class BookDAO {

    /**
     * Inserts a new book into the database.
     * Returns the generated book_id, or -1 if the insert failed.
     */
    public int addBook(Book book) {
        String sql = "INSERT INTO Books (title, author, isbn, available) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setBoolean(4, book.isAvailable());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves a single book by its ID.
     * Returns null if no book with that ID exists.
     */
    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM Books WHERE book_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching book: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves every book in the database.
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(mapRowToBook(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching books: " + e.getMessage());
        }
        return books;
    }

    /**
     * Searches books whose title or author contains the given keyword
     * (case-insensitive partial match).
     */
    public List<Book> searchBooksByTitleOrAuthor(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books WHERE title LIKE ? OR author LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRowToBook(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching books: " + e.getMessage());
        }
        return books;
    }

    /**
     * Updates a book's title, author, and isbn (not availability —
     * use setAvailability() for that, since it's changed by loan logic).
     * Returns true if a row was actually updated.
     */
    public boolean updateBook(Book book) {
        String sql = "UPDATE Books SET title = ?, author = ?, isbn = ? WHERE book_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setInt(4, book.getBookId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a book by its ID.
     * Returns true if a row was actually deleted.
     */
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM Books WHERE book_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
        return false;
    }

    /**
     * Flips a book's availability flag.
     * Called by LoanService when a book is issued (false) or returned (true).
     * Returns true if a row was actually updated.
     */
    public boolean setAvailability(int bookId, boolean available) {
        String sql = "UPDATE Books SET available = ? WHERE book_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, available);
            stmt.setInt(2, bookId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating availability: " + e.getMessage());
        }
        return false;
    }

    /**
     * Maps the current row of a ResultSet to a Book object.
     * Kept as one private helper so every read method builds
     * Book objects the same way.
     */
    private Book mapRowToBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getBoolean("available")
        );
    }
}