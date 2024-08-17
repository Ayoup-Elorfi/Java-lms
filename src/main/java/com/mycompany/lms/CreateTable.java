package com.mycompany.lms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CreateTable {
    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:C:\\Users\\PHP\\Documents\\NetBeansProjects\\LMS\\library.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS B (\r\n"
                + "    loan_id INTEGER PRIMARY KEY AUTOINCREMENT,\r\n"
                + "    book_id INTEGER NOT NULL,\r\n"
                + "    member_id INTEGER NOT NULL,\r\n"
                + "    borrow_date DATE NOT NULL,\r\n"
                + "    due_date DATE NOT NULL,\r\n"
                + "    return_date DATE,\r\n"
                + "    returned BOOLEAN,\r\n"
                + "    FOREIGN KEY (book_id) REFERENCES books(id),\r\n"
                + "    FOREIGN KEY (member_id) REFERENCES members(id)\r\n"
                + ");\r\n"
                + ";\r\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
                java.sql.Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            System.out.println("Table created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        createNewTable();
    }
}
