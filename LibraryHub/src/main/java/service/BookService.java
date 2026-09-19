package service;

import dao.BookDAO;
import model.Book;

import java.util.List;

/**
 * Business logic layer for books.
 * BookDAO answers "can this be written to the Books table."
 * BookService answers "should this happen at all" — input validation
 * and any rules that aren't themselves SQL concerns live here.
 */
public class BookService {

    private final BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAO();
    }

    /**
     * Validates input, then adds a new book.
     * Returns the generated book_id, or -1 if validation or the
     * insert itself failed.
     */
    public int addBook(String title, String author, String isbn) {
        if (isBlank(title) || isBlank(author)) {
            System.out.println("Title and author cannot be empty.");
            return -1;
        }

        Book book = new Book(title.trim(), author.trim(), isBlank(isbn) ? null : isbn.trim());
        return bookDAO.addBook(book);
    }

    /**
     * Returns every book in the catalog. No rules needed to just list them.
     */
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    /**
     * Returns a single book by ID, or null if not found.
     */
    public Book getBookById(int bookId) {
        return bookDAO.getBookById(bookId);
    }

    /**
     * Validates the search keyword, then searches by title or author.
     */
    public List<Book> searchBooks(String keyword) {
        if (isBlank(keyword)) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }
        return bookDAO.searchBooksByTitleOrAuthor(keyword.trim());
    }

    /**
     * Validates input, then updates an existing book's title/author/isbn.
     */
    public boolean updateBook(Book book) {
        if (book == null || isBlank(book.getTitle()) || isBlank(book.getAuthor())) {
            System.out.println("Title and author cannot be empty.");
            return false;
        }
        return bookDAO.updateBook(book);
    }

    /**
     * Deletes a book by ID.
     * Note: this does NOT check whether the book has an active loan.
     * BookService only knows about Books — it has no reason to know
     * Loans exists. That cross-entity check belongs one layer up
     * (LoanService or Main), which can ask LoanService whether the
     * book is currently on loan before calling this.
     */
    public boolean deleteBook(int bookId) {
        return bookDAO.deleteBook(bookId);
    }

    /**
     * Checks whether a book is currently available to borrow.
     * Returns false if the book doesn't exist at all.
     */
    public boolean isBookAvailable(int bookId) {
        Book book = bookDAO.getBookById(bookId);
        return book != null && book.isAvailable();
    }

    /**
     * Marks a book as borrowed (available = false).
     * Intention-revealing wrapper for LoanService to call instead
     * of a raw boolean flag.
     */
    public boolean markAsBorrowed(int bookId) {
        return bookDAO.setAvailability(bookId, false);
    }

    /**
     * Marks a book as returned (available = true).
     * Intention-revealing wrapper for LoanService to call instead
     * of a raw boolean flag.
     */
    public boolean markAsReturned(int bookId) {
        return bookDAO.setAvailability(bookId, true);
    }

    /**
     * Small shared helper: treats null and whitespace-only strings as blank.
     */
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}