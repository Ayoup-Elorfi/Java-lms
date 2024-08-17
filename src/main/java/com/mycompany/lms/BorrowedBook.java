package com.mycompany.lms;

public class BorrowedBook extends Book {
    private String borrower;

    public BorrowedBook(int id, String title, String author, String borrower) {
        super(id, title, author); 
        this.borrower = borrower;
    }

    
    public String getBorrower() {
        return borrower;
    }

    
}
