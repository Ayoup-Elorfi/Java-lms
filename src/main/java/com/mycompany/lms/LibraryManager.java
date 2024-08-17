package com.mycompany.lms;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class LibraryManager {
    private Map<Integer, Book> books;
    
    private Map<Integer, Member> members;
    private List<Loan> loans;
    private String url = "jdbc:sqlite:C:\\Users\\PHP\\Documents\\NetBeansProjects\\LMS\\library.db"; 
    DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author"}, 0);
    JTable table = new JTable(tableModel);


    public LibraryManager() {
        this.books = new HashMap<>();
        loadBooksFromDatabase();
        this.members = new HashMap<>();
        this.loans = new ArrayList<>();
    }

    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isBorrowed) VALUES (?, ?,? )";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setBoolean(3, book.isBorrowed());
            pstmt.executeUpdate();
            System.out.println("Book added: " + book);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
   
    private void loadBooksFromDatabase() {
        String sql = "SELECT id, title, author, isBorrowed FROM books";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet resultSet = pstmt.executeQuery()) {
            
            // Iterate through the result set and populate the books map
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                boolean isBorrowed = resultSet.getBoolean("isBorrowed");
                
                // Assuming Book class has a constructor that accepts isBorrowed
                books.put(id, new Book(id, title, author));
            }
            System.out.println("Books loaded from database.");
        } catch (SQLException e) {
            System.out.println("Error loading books from database: " + e.getMessage());
        }
    }
 

    // Member CRUD Operations
    public void addMember(Member member) {
        String sql = "INSERT INTO members(name, email) VALUES(?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());
            pstmt.executeUpdate();
            System.out.println("Member added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void getAllMembers() {
        String sql = "SELECT id, name, email FROM members";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                                   rs.getString("name") + "\t" +
                                   rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Loan CRUD Operations
    public void addLoan(int bookId, int memberId, String issueDate, String dueDate) {
        String sql = "INSERT INTO loans(book_id, member_id, issue_date, due_date) VALUES(?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            pstmt.setString(3, issueDate);
            pstmt.setString(4, dueDate);
            pstmt.executeUpdate();
            System.out.println("Loan added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void listAllLoans() {
        String sql = "SELECT * FROM loans";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                System.out.println("Loan ID: " + rs.getInt("id") + ", Book ID: " + rs.getInt("book_id") +
                        ", Member ID: " + rs.getInt("member_id") + ", Issue Date: " + rs.getString("issue_date") +
                        ", Due Date: " + rs.getString("due_date"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public class BookUnavailableException extends Exception {
        public BookUnavailableException(String message) {
            super(message);
        }
    }


    public void returnBook(int bookId) throws BookNotFoundException, SQLException {
        // SQL query to find an active loan for the book
        String selectSql = "SELECT * FROM loans WHERE book_id = ? AND returned IS FALSE"; // Assuming 'returned' column indicates if the book has been returned

        // SQL query to update the loan record as returned
        String updateSql = "UPDATE loans SET returned = TRUE WHERE book_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            
            selectStmt.setInt(1, bookId);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                throw new BookNotFoundException("No active loan found for book with ID " + bookId);
            }

            // If an active loan exists, mark it as returned
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();
                System.out.println("Book returned: " + bookId);
            }
        }
    }


 // Method to fetch all books from the database
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author FROM books";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return books;
    }
    

   
 // Method to sort books by title
    public void sortBooksByTitle() {
        List<Book> sortedBooks = new ArrayList<>(books.values());
        sortedBooks.sort(Comparator.comparing(Book::getTitle));
        System.out.println("Books sorted by title:");
        sortedBooks.forEach(System.out::println);
    }

    // Method to sort members by name
    public void sortMembersByName() {
        List<Member> sortedMembers = new ArrayList<>(members.values());
        sortedMembers.sort(Comparator.comparing(Member::getName));
        System.out.println("Members sorted by name:");
        sortedMembers.forEach(System.out::println);
    }
 // Linear search for a book by title
    public Book searchBookByTitle(String title) {
        for (Book book : books.values()) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null; // Book not found
    }
    

    
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookId);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
            return false;
        }
      
    }


    public boolean validateLogin(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ? AND isActive = TRUE";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return password.equals(storedPassword); // In real applications, use hashed password comparison
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    public void addUser(String username, String password, String email, String role, boolean isActive) {
        String sql = "INSERT INTO users (username, password, email, role, isActive) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Consider using hashed passwords for security
            pstmt.setString(3, email);
            pstmt.setString(4, role);
            pstmt.setBoolean(5, isActive);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addMember(String name, String email, String phone, boolean isActive) {
        String sql = "INSERT INTO members(name, email, phone, isActive) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setBoolean(4, isActive);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding member: " + e.getMessage());
        }
    }
    public void borrowBook(int bookId, String memberId, String borrowDate, String dueDate) {
        // Include 'returned' column, setting it to NULL for a new loan
        String sql = "INSERT INTO loans (book_id, member_id, borrow_date, due_date, returned) VALUES (?, ?, ?, ?, NULL)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, Integer.parseInt(memberId));
            pstmt.setString(3, borrowDate);
            pstmt.setString(4, dueDate);
            // No need to set a value for 'returned' as it's set to NULL by default in the query
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book borrowed successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error borrowing book: " + e.getMessage());
        }
    }

   
    public List<BorrowedBook> getBorrowedBookDetails() {
        List<BorrowedBook> borrowedBookDetails = new ArrayList<>();
        String sql = "SELECT  books.id ,books.title, books.author, members.name AS borrower " +
                     "FROM books " +
                     "JOIN loans ON books.id = loans.book_id " +
                     "JOIN members ON loans.member_id = members.id " +
                     "WHERE loans.returned IS false";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
            	 int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String borrower = rs.getString("borrower");
                borrowedBookDetails.add(new BorrowedBook(id,title, author, borrower));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching borrowed book details: " + e.getMessage());
        }
        return borrowedBookDetails;
    }
    public boolean updateBook(int id, String newTitle, String newAuthor) {
        String sql = "UPDATE books SET title = ?, author = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameters for the prepared statement
            pstmt.setString(1, newTitle);
            pstmt.setString(2, newAuthor);
            pstmt.setInt(3, id);

            // Execute the update
            int affectedRows = pstmt.executeUpdate();

            // Check if the update was successful
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
            return false;
        }
    }
    public Book getBookById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set the ID parameter in the query
            pstmt.setInt(1, id);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            // If book found, create and return a Book object
            if (rs.next()) {
                return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching book by ID: " + e.getMessage());
        }
        
        // Return null if no book is found or if there's an exception
        return null;
    }
   

    public List<Book> getBorrowedBooks() {
        List<Book> borrowedBooks = new ArrayList<>();
        String sql = "SELECT books.id, books.title, books.author FROM books " +
                     "JOIN loans ON books.id = loans.book_id " +
                     "WHERE loans.return_date IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Assuming a Book constructor like Book(int id, String title, String author)
                borrowedBooks.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching borrowed books: " + e.getMessage());
        }
        return borrowedBooks;
    }
    

    // Additional methods such as listAllMembers, listAllLoans can be added here
}
