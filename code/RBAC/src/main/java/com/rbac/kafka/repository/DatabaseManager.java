package com.rbac.kafka.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:/var/lib/myapp/db/rbacdatabase.db";
    private Connection connection;

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
        connection = DriverManager.getConnection(DB_URL);
        createTables();
        } catch (SQLException e) {
            System.out.println("Error connecting to SQLite database: " + e.getMessage());
            // Handle error properly
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // SQL statement for creating the Roles table
            String sqlCreateRoles = "CREATE TABLE IF NOT EXISTS Roles (" +
                    "RoleId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "RoleName TEXT NOT NULL UNIQUE," +
                    "Permissions TEXT);";

            // SQL statement for creating the Users table
            String sqlCreateUsers = "CREATE TABLE IF NOT EXISTS Users (" +
                    "UserId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "UserName TEXT NOT NULL UNIQUE);";

            // SQL statement for creating the UserRoles junction table
            String sqlCreateUserRoles = "CREATE TABLE IF NOT EXISTS UserRoles (" +
                    "UserId INTEGER NOT NULL," +
                    "RoleId INTEGER NOT NULL," +
                    "PRIMARY KEY (UserId, RoleId)," +
                    "FOREIGN KEY (UserId) REFERENCES Users(UserId)," +
                    "FOREIGN KEY (RoleId) REFERENCES Roles(RoleId));";

            // Execute the SQL statements to create new tables
            stmt.execute(sqlCreateRoles);
            stmt.execute(sqlCreateUsers);
            stmt.execute(sqlCreateUserRoles);
        }
    }


    public void performDatabaseOperation() {

    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing the SQLite database connection: " + e.getMessage());
            // Handle error properly
        }
    }

}
