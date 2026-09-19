import model.Book;
import model.Loan;
import model.Member;
import service.BookService;
import service.LoanService;
import service.MemberService;

import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for LibraSys.
 *
 * Main's job is ONLY user interaction: printing menus, reading input,
 * calling the appropriate Service method, and printing the result.
 * It contains no SQL and no business rules — those live in the
 * Service layer, which is what makes this class thin.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BookService bookService = new BookService();
    private static final MemberService memberService = new MemberService();
    private static final LoanService loanService = new LoanService();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addBook();
                case "2" -> searchBooks();
                case "3" -> registerMember();
                case "4" -> issueBook();
                case "5" -> returnBook();
                case "6" -> viewActiveLoans();
                case "7" -> viewAllBooks();
                case "8" -> viewMemberHistory();
                case "0" -> running = false;
                default -> System.out.println("Invalid option, try again.");
            }
            System.out.println();
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=== LibraSys ===");
        System.out.println("1. Add Book");
        System.out.println("2. Search Book");
        System.out.println("3. Register Member");
        System.out.println("4. Issue Book");
        System.out.println("5. Return Book");
        System.out.println("6. View Active Loans / Overdue");
        System.out.println("7. View All Books");
        System.out.println("8. View Member Borrowing History");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    // --- Menu actions ---
    // Each method's job is: read input -> call one Service method -> print result.
    // No validation logic here beyond "is this parseable as a number" —
    // real validation (blank fields, availability, etc.) already happens
    // inside the Service layer, which is exactly why these stay short.

    private static void addBook() {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("ISBN (optional, press Enter to skip): ");
        String isbn = scanner.nextLine();

        int bookId = bookService.addBook(title, author, isbn);
        if (bookId != -1) {
            System.out.println("Book added with ID " + bookId + ".");
        }
    }

    private static void searchBooks() {
        System.out.print("Search by title or author: ");
        String keyword = scanner.nextLine();

        List<Book> results = bookService.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            results.forEach(Main::printBook);
        }
    }

    private static void registerMember() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone (optional, press Enter to skip): ");
        String phone = scanner.nextLine();

        int memberId = memberService.addMember(name, email, phone);
        if (memberId != -1) {
            System.out.println("Member registered with ID " + memberId + ".");
        }
    }

    private static void issueBook() {
        Integer memberId = readInt("Member ID: ");
        Integer bookId = readInt("Book ID: ");
        if (memberId == null || bookId == null) return;

        int loanId = loanService.issueBook(memberId, bookId);
        if (loanId != -1) {
            System.out.println("Book issued. Loan ID: " + loanId);
        }
    }

    private static void returnBook() {
        Integer bookId = readInt("Book ID: ");
        if (bookId == null) return;

        loanService.returnBook(bookId);
    }

    private static void viewActiveLoans() {
        List<Loan> activeLoans = loanService.getAllActiveLoans();
        if (activeLoans.isEmpty()) {
            System.out.println("No books are currently on loan.");
            return;
        }

        for (Loan loan : activeLoans) {
            boolean overdue = loanService.isOverdue(loan);
            System.out.printf(
                    "Loan #%d | Member %d | Book %d | Loan date: %s | Due: %s%s%n",
                    loan.getLoanId(),
                    loan.getMemberId(),
                    loan.getBookId(),
                    loan.getLoanDate(),
                    loanService.getDueDate(loan.getLoanDate()),
                    overdue ? "  ⚠ OVERDUE" : ""
            );
        }
    }

    private static void viewAllBooks() {
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the catalog yet.");
        } else {
            books.forEach(Main::printBook);
        }
    }

    private static void viewMemberHistory() {
        Integer memberId = readInt("Member ID: ");
        if (memberId == null) return;

        Member member = memberService.getMemberById(memberId);
        if (member == null) {
            System.out.println("No member found with that ID.");
            return;
        }

        List<Loan> history = loanService.getLoanHistoryForMember(memberId);
        if (history.isEmpty()) {
            System.out.println(member.getName() + " has no borrowing history.");
            return;
        }

        System.out.println("Borrowing history for " + member.getName() + ":");
        for (Loan loan : history) {
            String status = loan.isReturned()
                    ? "Returned on " + loan.getReturnDate()
                    : "Still out — due " + loanService.getDueDate(loan.getLoanDate());
            System.out.println("  Book " + loan.getBookId() + " | Borrowed " + loan.getLoanDate() + " | " + status);
        }
    }

    // --- Small shared helpers ---

    private static void printBook(Book book) {
        System.out.printf(
                "  [%d] \"%s\" by %s | ISBN: %s | %s%n",
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn() == null ? "N/A" : book.getIsbn(),
                book.isAvailable() ? "Available" : "Borrowed"
        );
    }

    /**
     * Reads a line and parses it as an int. Prints a message and
     * returns null (instead of throwing) if the input isn't a valid number,
     * so callers can bail out of the current menu action cleanly.
     */
    private static Integer readInt(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("That's not a valid number.");
            return null;
        }
    }
}