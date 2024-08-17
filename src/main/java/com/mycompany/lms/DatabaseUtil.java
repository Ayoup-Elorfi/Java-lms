package com.mycompany.lms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:sqlite:C:\\Users\\PHP\\Documents\\NetBeansProjects\\LMS\\library.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}

