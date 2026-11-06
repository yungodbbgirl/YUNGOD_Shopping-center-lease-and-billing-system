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

        String createActivityLogsTableSQL = """
            CREATE TABLE IF NOT EXISTS activity_logs (
                log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                action_type TEXT NOT NULL,
                description TEXT NOT NULL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(user_id)
            );
        """;

        String createTenantsTableSQL = """
            CREATE TABLE IF NOT EXISTS tenants (
                tenant_id INTEGER PRIMARY KEY AUTOINCREMENT,
                store_name TEXT UNIQUE NOT NULL,
                unit_code TEXT UNIQUE NOT NULL,
                leased_sq_meters REAL NOT NULL,
                status TEXT DEFAULT 'ACTIVE'
            );
        """;

        String createBillsTableSQL = """
            CREATE TABLE IF NOT EXISTS bills (
                bill_id INTEGER PRIMARY KEY AUTOINCREMENT,
                tenant_id INTEGER,
                base_lease REAL NOT NULL,
                electricity_amount REAL NOT NULL,
                water_amount REAL NOT NULL,
                vat REAL NOT NULL,
                penalty REAL NOT NULL,
                total_amount REAL NOT NULL,
                status TEXT DEFAULT 'UNPAID',
                billing_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id)
            );
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute(createUsersTableSQL);
            stmt.execute(createActivityLogsTableSQL);
            stmt.execute(createTenantsTableSQL);
            stmt.execute(createBillsTableSQL);
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }
}