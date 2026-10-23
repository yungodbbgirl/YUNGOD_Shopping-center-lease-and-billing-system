package com.scc.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        String createUsersTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                role TEXT NOT NULL,
                status TEXT NOT NULL
            );
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute(createUsersTableSQL);
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }
}