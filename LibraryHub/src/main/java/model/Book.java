package model;

/**
 * Plain data object representing one row of the Books table.
 * Holds no SQL and no business logic — that lives in BookDAO / BookService.
 *
 * Matches schema.sql exactly:
 *   book_id INT AUTO_INCREMENT PRIMARY KEY
 *   title   VARCHAR(200) NOT NULL
 *   author  VARCHAR(100) NOT NULL
 *   isbn    VARCHAR(20) UNIQUE
 *   available BOOLEAN DEFAULT TRUE
 */
public class Book {

    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private boolean available;

    /**
     * Full constructor — used when a book is loaded FROM the database,
     * where the ID is already known.
     */
    public Book(int bookId, String title, String author, String isbn, boolean available) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = available;
    }

    /**
     * No-ID constructor — used when creating a NEW book to insert,
     * since MySQL assigns book_id automatically (AUTO_INCREMENT).
     * New books default to available = true.
     */
    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = true;
    }

    // --- Getters and setters ---

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", available=" + available +
                '}';
    }
}