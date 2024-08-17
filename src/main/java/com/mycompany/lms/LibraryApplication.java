package com.mycompany.lms;

public class LibraryApplication {
    public static void main(String[] args) {
        LibraryManager libraryManager = new LibraryManager();

        // Assuming books and members are added to the libraryManager
        libraryManager.addBook(new Book(1, "The Great Gatsby", "F. Scott Fitzgerald"));
        libraryManager.addBook(new Book(2, "1984", "George Orwell"));
        libraryManager.addBook(new Book(3, "Brave New World", "Aldous Huxley"));

        libraryManager.addMember(new Member(1, "Ahmed", "ahmed@lms.ly"));
        libraryManager.addMember(new Member(2, "Ali", "Ali@lms.ly"));

        // Sort and display books by title
        libraryManager.sortBooksByTitle();

        // Sort and display members by name
        libraryManager.sortMembersByName();

        // Search for a book by title
        Book foundBook = libraryManager.searchBookByTitle("1984");
        if (foundBook != null) {
            System.out.println("Found book: " + foundBook);
        } else {
            System.out.println("Book not found.");
        }
    }
}
