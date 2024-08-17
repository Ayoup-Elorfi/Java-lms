package com.mycompany.lms;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import java.util.Comparator;
import java.util.List;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibraryGUI extends JFrame {
	
	
	
    /////////////////////////////////////////////////////

    private LibraryManager libraryManager;
    private DefaultTableModel tableModel = new DefaultTableModel(new Object[]{ "ID","Title", "Author"}, 0);
    JTable table = new JTable(tableModel);
    private DefaultTableModel tableModel2 = new DefaultTableModel(new Object[]{ "ID","Title", "Author", "Borrower"}, 0);
    JTable table2 = new JTable(tableModel2);
    private DefaultTableModel tableModel3 = new DefaultTableModel(new Object[]{ "ID","Title", "Author"}, 0);
    JTable table3 = new JTable(tableModel3);
    
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";
    private JTable borrowedBooksTable = new JTable(tableModel2);
    
    /////////////////////////////////////////////////////

    public LibraryGUI() {
        libraryManager = new LibraryManager();
        setTitle("Library Management System");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());//set border
        JTabbedPane mainTabbedPane = new JTabbedPane();
        JPanel addPanel = createAddPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Add Book/Member", null, addPanel, "Add books and members");
        tabbedPane.addTab("Borrow Book", createBorrowPanel());
        tabbedPane.addTab("List Books", createListPanel());
        tabbedPane.addTab("Register User", createRegistrationPanel()); 
        add(mainTabbedPane, BorderLayout.CENTER);
        JPanel borrowedBooksPanel = createBorrowedBooksPanel();
        tabbedPane.addTab("Borrowed Books", borrowedBooksPanel);
        add(tabbedPane, BorderLayout.CENTER);
       //tabbedPane.addTab("Search Book", createSearchPanel()); 
    }
    /////////////////////////
  


    private JPanel createAddPanel() {
        // Create the sub-tabbed pane
        JTabbedPane subTabbedPane = new JTabbedPane();

        // Add the "Add Book" tab
        JPanel addBookPanel = createAddBookPanel(); // Ensure this method exists and returns a JPanel
        subTabbedPane.addTab("Add Book", null, addBookPanel, "Add a new book");

        // Add the "Add Member" tab
        JPanel addMemberPanel = createAddMemberPanel(); // Ensure this method exists and returns a JPanel
        subTabbedPane.addTab("Add Member", null, addMemberPanel, "Add a new member");

        // Wrap the sub-tabbed pane in a parent panel if additional configuration is needed
        JPanel parentPanel = new JPanel(new BorderLayout());
        parentPanel.add(subTabbedPane, BorderLayout.CENTER);

        return parentPanel;
    }

    ////////////////////////////////////////////////


    private JPanel createBorrowPanel() {
    	JPanel borrowPanel = new JPanel(new BorderLayout());
        borrowPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Margin between components

        // Customizing components
        JLabel label = new JLabel("Borrow a Book");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        borrowPanel.add(label, BorderLayout.NORTH); // Title at the top

        // Add components to the formPanel with GridBagLayout for precise control
        addFormItem(formPanel, "Select Book:", gbc);
        JComboBox<String> bookDropdown = new JComboBox<>();
        // Assume this method populates the dropdown
        loadBooks(bookDropdown);
        formPanel.add(bookDropdown, gbc);

        addFormItem(formPanel, "Select Member:", gbc);
        JComboBox<String> memberDropdown = new JComboBox<>();
        // Assume this method populates the dropdown
        loadMembers(memberDropdown);
        formPanel.add(memberDropdown, gbc);

        addFormItem(formPanel, "Borrow Date (YYYY-MM-DD):", gbc);
        JTextField borrowDateField = new JTextField(10);
        formPanel.add(borrowDateField, gbc);

        addFormItem(formPanel, "Due Date (YYYY-MM-DD):", gbc);
        JTextField dueDateField = new JTextField(10);
        formPanel.add(dueDateField, gbc);

        JButton borrowButton = new JButton("Borrow Book");
        borrowButton.setFont(new Font("Arial", Font.BOLD, 16));
        borrowButton.setBackground(new Color(13, 110, 253));
        borrowButton.setForeground(Color.WHITE);
        borrowButton.setFocusPainted(false);
        gbc.fill = GridBagConstraints.NONE; // Button should not stretch
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        formPanel.add(borrowButton, gbc);

        borrowPanel.add(formPanel, BorderLayout.CENTER); // Add the form in the center
        borrowButton.addActionListener(e -> {
            // Assume borrowBook() handles the logic of adding the loan to the database
            borrowBook(bookDropdown.getSelectedItem().toString(),
                       memberDropdown.getSelectedItem().toString(),
                       borrowDateField.getText(),
                       dueDateField.getText());
        });
      //  borrowPanel.add(borrowButton, gbc);

        return borrowPanel;
    }
    
    /////////////////////////
    private void addFormItem(JPanel panel, String labelText, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(label, gbc);
    }

/////////////////////////////////////
    
    private JPanel createListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        refreshBookList(); // Populates the table with data

        // Hide the ID column after the first data load
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0)); // This assumes ID is the first column

        // Set row height and font
        table.setRowHeight(30);
        table.setFont(new Font("Serif", Font.PLAIN, 20));

        // Customize table headers
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(new Color(0, 120, 215));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setFont(new Font("Dialog", Font.BOLD, 18));
        ((DefaultTableCellRenderer)tableHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Alternate row colors and center alignment
        centerAlignTableData();

        // Buttons and action panel
        JButton updateButton = createUpdateButton();
        JButton deleteButton = createDeleteButton();

        JPanel actionPanel = new JPanel();
        actionPanel.add(updateButton);
        actionPanel.add(deleteButton);
        listPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        listPanel.add(actionPanel, BorderLayout.SOUTH);

        return listPanel;
    }

    private JButton createUpdateButton() {
        JButton updateButton = new JButton("Update Book");
        updateButton.addActionListener(e -> openUpdateBookDialog());
        updateButton.setBackground(new Color(173, 216, 230)); // Light blue background
        updateButton.setForeground(Color.BLACK); // Text color
        return updateButton;
    }

    private JButton createDeleteButton() {
        JButton deleteButton = new JButton("Delete Selected Book");
        deleteButton.addActionListener(e -> deleteSelectedBook());
        deleteButton.setBackground(Color.RED); // Red background
        deleteButton.setForeground(Color.WHITE); // Text color
        deleteButton.setPreferredSize(new Dimension(150, 25));
        return deleteButton;
    }

    private void centerAlignTableData() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }


    ////////////////////////
    private void refreshBorrowedBooksList(DefaultTableModel borrowedBooksModel) {
        // Clear the existing data
        borrowedBooksModel.setRowCount(0);

        // Fetch the borrowed books and populate the model
        List<BorrowedBook> borrowedBooks = libraryManager.getBorrowedBookDetails();
        for (BorrowedBook book : borrowedBooks) {
            borrowedBooksModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getBorrower()});
        }
    }

    private JPanel createBorrowedBooksPanel() {
        JPanel borrowedBooksPanel = new JPanel(new BorderLayout());
        customizeTableAppearance(borrowedBooksTable);

        borrowedBooksPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Define a new table model specifically for borrowed books
        String[] borrowedColumnNames = {"ID", "Title", "Author", "Borrowed By"};
        DefaultTableModel borrowedTableModel = new DefaultTableModel(borrowedColumnNames, 0);
        JTable borrowedBooksTable = new JTable(borrowedTableModel);
        JScrollPane borrowedScrollPane = new JScrollPane(borrowedBooksTable);
        borrowedBooksPanel.add(borrowedScrollPane, BorderLayout.CENTER);

        // Center align table data
        centerAlignTableData(borrowedBooksTable);

        // Buttons Panel for borrowed books panel
        JPanel borrowedButtonsPanel = new JPanel();
        borrowedButtonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // Return Button
        JButton returnButton = new JButton("Return Book");
        returnButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        returnButton.setForeground(Color.WHITE);
        returnButton.setFocusPainted(false);
        returnButton.addActionListener(e -> returnSelectedBook(borrowedBooksTable, borrowedTableModel));
        borrowedButtonsPanel.add(returnButton);

        borrowedBooksPanel.add(borrowedButtonsPanel, BorderLayout.SOUTH);

        // Add a ComponentListener to refresh the list every time the panel is shown
        borrowedBooksPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshBorrowedBooksList(borrowedTableModel); // Adjust this method to accept the model as a parameter
            }
        });

        return borrowedBooksPanel;
    }

    
       

    private void centerAlignTableData(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        table.getTableHeader().setDefaultRenderer(centerRenderer);
    }

    private void returnSelectedBook(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // Assuming the first column is ID
            int bookId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
            try {
                // Your logic to return the book
                libraryManager.returnBook(bookId);
                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(null, "Book returned successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error returning book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a book to return.", "No Book Selected", JOptionPane.WARNING_MESSAGE);
        }
    }



////////////////////////////////////////////////

    private void openUpdateBookDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
        	int bookId = (Integer) table.getModel().getValueAt(selectedRow, 0); // Assuming ID is in the first column
            Book selectedBook = libraryManager.getBookById(bookId); // Implement this method to fetch the selected book details
            if (selectedBook != null) {
                showBookUpdateForm(selectedBook);
            } else {
                JOptionPane.showMessageDialog(this, "Book not found.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to update.");
        }
    }
    private void showBookUpdateForm(Book book) {
        JDialog dialog = new JDialog();
        dialog.setLayout(new GridLayout(0, 2));

        JTextField titleField = new JTextField(book.getTitle());
        JTextField authorField = new JTextField(book.getAuthor());
        // Add more fields as necessary

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> updateBook(book.getId(), titleField.getText(), authorField.getText()));
        // Setup dialog properties
        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Author:"));
        dialog.add(authorField);
        dialog.add(saveButton);
        
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center on screen
        dialog.setVisible(true);
    }
    
    /////////////////////////////////////////////////////

    private void updateBook(int id, String newTitle, String newAuthor) {
        // Implement database update logic here
        boolean success = libraryManager.updateBook(id, newTitle, newAuthor); // This method should return true if the update was successful
        if (success) {
            JOptionPane.showMessageDialog(null, "Book updated successfully.");
            refreshBookList(); // Refresh your book list to show the updated info
        } else {
            JOptionPane.showMessageDialog(null, "Failed to update book.");
        }
    }
    
    /////////////////////////////////////////////////////

    public void refreshBookList() {
        List<Book> books = libraryManager.getAllBooks(); // Fetch the latest books
        books.sort(Comparator.comparing(book -> book.getTitle() == null ? "" : book.getTitle().toLowerCase()));

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing data
        
        for (Book book : books) {
            model.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor()});
        }
        // Do NOT hide the column here
    }





    /////////////////////////////////////////////////////

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryGUI frame = new LibraryGUI();
            frame.setVisible(true);
        });
    }
    
    /////////////////////////////////////////////////////

    public void deleteSelectedBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Object idValue = table.getModel().getValueAt(selectedRow, 0);

            // Ensuring correct type before casting
            int bookId = idValue instanceof Number ? ((Number)idValue).intValue() : Integer.parseInt(idValue.toString());

            if (libraryManager.deleteBook(bookId)) {
                JOptionPane.showMessageDialog(null, "Book deleted successfully.");
                refreshBookList(); // Refresh the book list to reflect the deletion
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete the book.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a book to delete.");
        }
    }

    
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////

    private JPanel createRegistrationPanel() {
        // Use BorderLayout for overall panel layout for better control over spacing
        JPanel registrationPanel = new JPanel(new BorderLayout(10, 10));
     // Initialize feedbackLabel here to make it effectively final in this context
        JLabel feedbackLabel = new JLabel("Password must be at least 8 characters long.");
        feedbackLabel.setForeground(Color.RED); // Initial feedback color
        // Create a sub-panel for form fields with GridBagLayout for fine-grained control over component layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10); // Top, left, bottom, right padding for each component

        // Font for labels and fields
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(labelFont);
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(fieldFont);
        formPanel.add(usernameLabel, gbc);
        formPanel.add(usernameField, gbc);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        
        passwordLabel.setFont(labelFont);
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                showFeedback();
            }
            public void removeUpdate(DocumentEvent e) {
                showFeedback();
            }
            public void insertUpdate(DocumentEvent e) {
                showFeedback();
            }
            private void showFeedback() {
                String feedback = validatePassword(new String(passwordField.getPassword()));
                System.out.println("Feedback: " + feedback); // Debug print
                if (feedback == null) {
                    feedbackLabel.setText("Password is strong.");
                    feedbackLabel.setForeground(new Color(0, 128, 0)); // Green color for valid feedback
                } else {
                    feedbackLabel.setText(feedback);
                    feedbackLabel.setForeground(Color.RED); // Red color for error feedback
                }
            }

        });

        passwordField.setFont(fieldFont);
        formPanel.add(passwordLabel, gbc);
        formPanel.add(passwordField, gbc);

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        JTextField emailField = new JTextField(15);
        emailField.setFont(fieldFont);
        formPanel.add(emailLabel, gbc);
        formPanel.add(emailField, gbc);

        // Register button with some styling
        
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();
            
            String validationResult = validatePassword(password);
            if (validationResult == null) {
                libraryManager.addUser(username, password, email, "user", true); // Adjust parameters as needed
                JOptionPane.showMessageDialog(null, "User registered successfully!");
            } else {
                JOptionPane.showMessageDialog(null, validationResult, "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(0, 123, 255));
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25)); // Add some padding around the button
        // Align the button to the right
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(registerButton, gbc);

        // Optional: Add some padding around the formPanel
        registrationPanel.add(formPanel, BorderLayout.NORTH);

        // Optional: Add a JPanel at the bottom or additional components for aesthetics
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        registrationPanel.add(bottomPanel, BorderLayout.CENTER);

        return registrationPanel;
    }
    
    /////////////////////////////////////////////////////

    private String validatePassword(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Password must contain at least one digit.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*[!@#$%^&+=].*")) {
            return "Password must contain at least one special character (@#$%^&+=).";
        }
        if (password.matches("\\s")) {
            return "Password cannot contain spaces.";
        }
        // Password is valid
        return null;
    }

    
    /////////////////////////////////////////////////////

    private JPanel createAddMemberPanel() {
        // Use BorderLayout for better control over spacing and alignment
        JPanel memberPanel = new JPanel(new BorderLayout(10, 10));
        memberPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the panel

        // Create a sub-panel with GridBagLayout for form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Each component in its own row
        gbc.fill = GridBagConstraints.HORIZONTAL; // Expand components to fill horizontal space
        gbc.insets = new Insets(5, 0, 5, 0); // Padding between rows

        // Custom font for labels and fields
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        JTextField nameField = new JTextField(20);
        nameField.setFont(fieldFont);
        formPanel.add(nameLabel, gbc);
        formPanel.add(nameField, gbc);

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        JTextField emailField = new JTextField(20);
        emailField.setFont(fieldFont);
        formPanel.add(emailLabel, gbc);
        formPanel.add(emailField, gbc);

        // Phone field
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(labelFont);
        JTextField phoneField = new JTextField(20);
        phoneField.setFont(fieldFont);
        formPanel.add(phoneLabel, gbc);
        formPanel.add(phoneField, gbc);

        // Add Member button with styling
        JButton addMemberButton = new JButton("Add Member");
        addMemberButton.setFont(new Font("Arial", Font.BOLD, 16));
        addMemberButton.setForeground(Color.WHITE);
        addMemberButton.setBackground(new Color(13, 110, 253)); // A pleasant blue
        addMemberButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        gbc.insets = new Insets(15, 0, 0, 0); // Increase padding above the button
        formPanel.add(addMemberButton, gbc);
        addMemberButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            try {
                libraryManager.addMember(name, email, phone,true);
                JOptionPane.showMessageDialog(null, "Member added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error adding member: " + ex.getMessage());
            }
        });

        // Align the formPanel at the top of the memberPanel
        memberPanel.add(formPanel, BorderLayout.NORTH);

        // Optionally, add a decorative or informative component at the bottom or center of memberPanel
        JLabel infoLabel = new JLabel("Fill out the form to add a new member.");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        memberPanel.add(infoLabel, BorderLayout.SOUTH);

        return memberPanel;
    }

    /////////////////////////////////////////////////////

    private JPanel createAddBookPanel() {
        // Use BorderLayout for better control over spacing and alignment
        JPanel bookPanel = new JPanel(new BorderLayout(10, 10));
        bookPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the panel

        // Create a sub-panel with GridBagLayout for form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Each component in its own row
        gbc.fill = GridBagConstraints.HORIZONTAL; // Expand components to fill horizontal space
        gbc.insets = new Insets(5, 0, 5, 0); // Padding between rows

        // Custom font for labels and fields
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // ID field
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(labelFont);
        JTextField idField = new JTextField(20);
        idField.setFont(fieldFont);
        formPanel.add(idLabel, gbc);
        formPanel.add(idField, gbc);

        // Title field
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(labelFont);
        JTextField titleField = new JTextField(20);
        titleField.setFont(fieldFont);
        formPanel.add(titleLabel, gbc);
        formPanel.add(titleField, gbc);

        // Author field
        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setFont(labelFont);
        JTextField authorField = new JTextField(20);
        authorField.setFont(fieldFont);
        formPanel.add(authorLabel, gbc);
        formPanel.add(authorField, gbc);

        // Add Book button with styling
        JButton addBookButton = new JButton("Add Book");
        addBookButton.setFont(new Font("Arial", Font.BOLD, 16));
        addBookButton.setForeground(Color.WHITE);
        addBookButton.setBackground(new Color(30, 150, 75)); // A pleasant green
        addBookButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        gbc.insets = new Insets(15, 0, 0, 0); // Increase padding above the button
        formPanel.add(addBookButton, gbc);
        addBookButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                libraryManager.addBook(new Book(id, title, author));
                JOptionPane.showMessageDialog(null, "Book added successfully!");
                refreshBookList(); // Refresh the book list to include the new book
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid numeric ID.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error adding book: " + ex.getMessage());
            }
        });

        // Align the formPanel at the top of the bookPanel
        bookPanel.add(formPanel, BorderLayout.NORTH);

        // Optionally, add a decorative or informative component at the bottom or center of bookPanel
        JLabel infoLabel = new JLabel("Fill out the form to add a new book.");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        bookPanel.add(infoLabel, BorderLayout.SOUTH);

        return bookPanel;
    }
    /////////////////////////////////////////////////////

    private void loadBooks(JComboBox<String> bookDropdown) {
        String sql = "SELECT id, title FROM books";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                // Assuming you want to display the title but use the ID as the value
                bookDropdown.addItem(id + " - " + title);
            }
        } catch (SQLException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }
    
    /////////////////////////////////////////////////////

    private void loadMembers(JComboBox<String> memberDropdown) {
        String sql = "SELECT id, name FROM members";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                // Assuming you want to display the name but use the ID as the value
                memberDropdown.addItem(id + " - " + name);
            }
        } catch (SQLException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
    }
    
    
    /////////////////////////////////////////////////////

    
    private void borrowBook(String bookInfo, String memberInfo, String borrowDate, String dueDate) {
        // Extracting IDs from the selected dropdown items
        int bookId = Integer.parseInt(bookInfo.split(" - ")[0]);
        int memberId = Integer.parseInt(memberInfo.split(" - ")[0]);

        // Include the 'returned' column, setting it initially to 'false' for a new loan
        String sql = "INSERT INTO loans (book_id, member_id, borrow_date, due_date, returned) VALUES (?, ?, ?, ?, FALSE)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            pstmt.setString(3, borrowDate);
            pstmt.setString(4, dueDate);
            // No need to set a value for 'returned' explicitly in the PreparedStatement as it's already in the SQL
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book borrowed successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error borrowing book: " + e.getMessage());
        }
    }

   
 
    /////////////////////////////////////////////////////


    private void customizeTableAppearance(JTable table) {
        // Set table row height and font
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Center align table data
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int columnIndex = 0; columnIndex < table.getModel().getColumnCount(); columnIndex++) {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
        }

        // Improve header appearance
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setBackground(new Color(0, 70, 130)); // Dark blue
        header.setForeground(Color.WHITE);

        // Hide the ID column visually
        table.removeColumn(table.getColumnModel().getColumn(0));
    }
    /////////////////////////////////////////////////////

    
    
    /////////////////////////////////////////////////////

  


}
