package com.mycompany.lms;

import java.time.LocalDate;

public class Loan {
    private Book book;
    private Member member;
    private LocalDate issueDate;
    private LocalDate dueDate;

    public Loan(Book book, Member member, LocalDate issueDate, LocalDate dueDate) {
        this.book = book;
        this.member = member;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.book.setBorrowed(true);
    }


    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    // Method to mark book as returned
    public void returnBook() {
        book.setBorrowed(false);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "book=" + book +
                ", member=" + member +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                '}';
    }
}
