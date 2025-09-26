package com.tracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public  static final String driver = "com.mysql.cj.jdbc.Driver";
    public  static final String url = "jdbc:mysql://localhost:3306/expenseTracker";
    public  static final String username = "root";
    public  static final String password = "Asharafs@2005";
    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.out.println("DataBase not found");
        }
    }
    public static Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
