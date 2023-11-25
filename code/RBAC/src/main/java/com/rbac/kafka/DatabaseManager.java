package com.rbac.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.rbac.kafka.RBACUtils.DB_URL;

public class DatabaseManager {

    private final KafkaController kafkaController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Connection connection;

    public DatabaseManager(KafkaController kafkaController) {
        this.kafkaController = kafkaController;
        if(!checkDatabaseExists()){
            System.out.println("Initializing new Database");
            initializeDatabase();
        }
    }

    private boolean checkDatabaseExists() {
        System.out.println("Checking for existing database");
        Set<String> requiredTables = new HashSet<>();
        requiredTables.add("Roles");
        requiredTables.add("Users");
        requiredTables.add("UserRoles");

        Set<String> foundTables = new HashSet<>();

        boolean databaseExists;
        try {
            connection = getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "%", null);
            while (resultSet.next()) {
                foundTables.add(resultSet.getString(3));
            }
            databaseExists = foundTables.containsAll(requiredTables);
        } catch (SQLException e) {
            System.out.println("Error connecting to SQLite database: " + e.getMessage());
            return false;
        }
        if (databaseExists) {
            System.out.println("Found existing database");
        } else {
            System.out.println("No existing database found");
        }
        return databaseExists;
    }

    public void initializeDatabase() {
        try {
        connection = getConnection();
        createTables();
        } catch (SQLException e) {
            System.out.println("Error connecting to SQLite database: " + e.getMessage());
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

    public void addUser(String userName) {
        if (userExists(userName)) {
            System.out.println("User already exists");
            return;
        }
        performDatabaseOperation(connection -> {
            String sql = "INSERT INTO Users (UserName) VALUES (?)";
            try (var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, userName);
                preparedStatement.executeUpdate();
            }
        });
    }

    public void deleteUser(String userName) {
        if (!userExists(userName)) {
            System.out.println("User does not exist");
            return;
        }

        Map<String, List<String>> aclChanges = new HashMap<>();
        Map<String, List<String>> userStatus = userStatus(userName);
        List<String> userPermissions = getPermissionsFromStatus(userStatus);
        aclChanges.put(userName, userPermissions);

        AtomicBoolean success = new AtomicBoolean(true);
        performDatabaseOperation(connection -> {
            String deleteUserSQL = "DELETE FROM Users WHERE UserName = ?";
            try (var preparedStatement = connection.prepareStatement(deleteUserSQL)) {
                preparedStatement.setString(1, userName);
                int rowsAffected = preparedStatement.executeUpdate();
                success.set(rowsAffected > 0);
            }
        });
        if (success.get()) {
            kafkaController.manageDatabaseChanges(aclChanges, false);
        } else {
            System.out.println("Failed to delete user from the database.");
        }
    }

    public void addRole(String roleName, List<String> permissions) {
        if (roleExists(roleName)) {
            System.out.println("Role already exists");
            return;
        }
        performDatabaseOperation(connection -> {
            String sql = "INSERT INTO Roles (RoleName, Permissions) VALUES (?, ?)";
            try (var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, roleName);
                String jsonPermissions = objectMapper.writeValueAsString(permissions);
                preparedStatement.setString(2, jsonPermissions);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                System.out.println("Error adding role: " + e.getMessage());
            }
        });
    }

    public void deleteRole(String roleName) {
        if (!roleExists(roleName)) {
            System.out.println("Role does not exist");
            return;
        }

        Map<String, List<String>> aclChanges = new HashMap<>();
        Map<String, List<String>> preUsersWithPermissions = new HashMap<>();
        List<String> users = getUsersForRole(roleName);
        for (String user : users) {
            Map<String, List<String>> userStatus = userStatus(user);
            List<String> userPermissions = getPermissionsFromStatus(userStatus);
            preUsersWithPermissions.put(user, userPermissions);
        }

        AtomicBoolean success = new AtomicBoolean(true);
        performDatabaseOperation(connection -> {
            String sql = "DELETE FROM Roles WHERE RoleName = ?";
            try (var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, roleName);
                int rowsAffected = preparedStatement.executeUpdate();
                success.set(rowsAffected > 0);
            }
        });

        if (success.get()) {
            Map<String, List<String>> postUsersWithPermissions = new HashMap<>();
            for (String user : users) {
                Map<String, List<String>> userStatus = userStatus(user);
                List<String> userPermissions = getPostPermissionsFromStatus(userStatus);
                postUsersWithPermissions.put(user, userPermissions);
            }
            for (Map.Entry<String, List<String>> entry : preUsersWithPermissions.entrySet()) {
                List<String> prePermissions = entry.getValue();
                List<String> postPermissions = postUsersWithPermissions.get(entry.getKey());
                List<String> permissionsRemoved = new ArrayList<>(prePermissions);
                if (!postPermissions.isEmpty()) permissionsRemoved.removeAll(postPermissions);
                aclChanges.put(entry.getKey(), permissionsRemoved);
            }
            kafkaController.manageDatabaseChanges(aclChanges, false);
        } else {
            System.out.println("Failed to delete user from the database.");
        }

    }

    public void addPermission(String roleName, String permission) {
        if (!roleExists(roleName)) {
            System.out.println("Role does not exist");
            return;
        }
        if (permissionExistsForRole(permission, roleName)) {
            System.out.println("Permission already exists for role");
            return;
        }

        Map<String, List<String>> aclChanges = new HashMap<>();
        Map<String, List<String>> preUsersWithPermissions = new HashMap<>();
        List<String> users = getUsersForRole(roleName);
        for (String user : users) {
            Map<String, List<String>> userStatus = userStatus(user);
            List<String> userPermissions = getPermissionsFromStatus(userStatus);
            preUsersWithPermissions.put(user, userPermissions);
        }

        boolean success = true;

        try {
            String currentPermissionsQuery = "SELECT Permissions FROM Roles WHERE RoleName = ?";
            String permissionsJson = retrieveData(currentPermissionsQuery, roleName).get(0);
            List<String> permissions = new ArrayList<>(objectMapper.readValue(permissionsJson, new TypeReference<>() {
            }));
            permissions.add(permission);
            String newPermissionsJson = objectMapper.writeValueAsString(permissions);
            String updatePermissionsQuery = "UPDATE Roles SET Permissions = ? WHERE RoleName = ?";
            success = updateData(updatePermissionsQuery, newPermissionsJson, roleName);
        } catch (Exception e) {
            System.out.println("Error adding permission: " + e.getMessage());
        }

        if (success) {
            Map<String, List<String>> postUsersWithPermissions = new HashMap<>();
            for (String user : users) {
                Map<String, List<String>> userStatus = userStatus(user);
                List<String> userPermissions = getPostPermissionsFromStatus(userStatus);
                postUsersWithPermissions.put(user, userPermissions);
            }
            for (Map.Entry<String, List<String>> entry : preUsersWithPermissions.entrySet()) {
                List<String> prePermissions = entry.getValue();
                List<String> postPermissions = postUsersWithPermissions.get(entry.getKey());
                List<String> permissionsAdded = new ArrayList<>(postPermissions);
                if (!prePermissions.isEmpty()) permissionsAdded.removeAll(prePermissions);
                aclChanges.put(entry.getKey(), permissionsAdded);
            }
            kafkaController.manageDatabaseChanges(aclChanges, true);
        } else {
            System.out.println("Failed to delete user from the database.");
        }
    }

    public void removePermission(String roleName, String permission) {
        if (!roleExists(roleName)) {
            System.out.println("Role does not exist");
            return;
        }
        if (!permissionExistsForRole(permission, roleName)) {
            System.out.println("Permission does not exist for role");
            return;
        }

        Map<String, List<String>> aclChanges = new HashMap<>();
        Map<String, List<String>> preUsersWithPermissions = new HashMap<>();
        List<String> users = getUsersForRole(roleName);
        for (String user : users) {
            Map<String, List<String>> userStatus = userStatus(user);
            List<String> userPermissions = getPermissionsFromStatus(userStatus);
            preUsersWithPermissions.put(user, userPermissions);
        }

        boolean success = true;

        try {
            String currentPermissionsQuery = "SELECT Permissions FROM Roles WHERE RoleName = ?";
            String permissionsJson = retrieveData(currentPermissionsQuery, roleName).get(0);
            List<String> permissions = objectMapper.readValue(permissionsJson, new TypeReference<>() {});
            permissions.remove(permission);
            String newPermissionsJson = objectMapper.writeValueAsString(permissions);
            String updatePermissionsQuery = "UPDATE Roles SET Permissions = ? WHERE RoleName = ?";
            success = updateData(updatePermissionsQuery, newPermissionsJson, roleName);
        } catch (Exception e) {
            System.out.println("Error adding permission: " + e.getMessage());
        }

        if (success) {
            Map<String, List<String>> postUsersWithPermissions = new HashMap<>();
            for (String user : users) {
                Map<String, List<String>> userStatus = userStatus(user);
                List<String> userPermissions = getPostPermissionsFromStatus(userStatus);
                postUsersWithPermissions.put(user, userPermissions);
            }
            for (Map.Entry<String, List<String>> entry : preUsersWithPermissions.entrySet()) {
                List<String> prePermissions = entry.getValue();
                List<String> postPermissions = postUsersWithPermissions.get(entry.getKey());
                List<String> permissionsRemoved = new ArrayList<>(prePermissions);
                if (!postPermissions.isEmpty()) permissionsRemoved.removeAll(postPermissions);
                aclChanges.put(entry.getKey(), permissionsRemoved);
            }
            kafkaController.manageDatabaseChanges(aclChanges, false);
        } else {
            System.out.println("Failed to delete user from the database.");
        }
    }

    public void assignRole(String userName, String roleName) {
        System.out.println("Assigning role to user");
        if (!userExists(userName) || !roleExists(roleName)) {
            System.out.println("User or role does not exist");
            return;
        }

        System.out.println("Assigning role to user1");
        Map<String, List<String>> aclChanges = new HashMap<>();
        Map<String, List<String>> preUserStatus = userStatus(userName);
        List<String> preUserPermissions = getPermissionsFromStatus(preUserStatus);

        AtomicBoolean success = new AtomicBoolean(true);

        performDatabaseOperation(connection -> {
            String sql = "INSERT INTO UserRoles (UserId, RoleId) " +
                    "SELECT u.UserId, r.RoleId " +
                    "FROM Users u, Roles r " +
                    "WHERE u.UserName = ? AND r.RoleName = ?";
            try (var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, roleName);
                int rowsAffected = preparedStatement.executeUpdate();
                success.set(rowsAffected > 0);
            }
        });

        if (success.get()) {
            Map<String, List<String>> postUserStatus = userStatus(userName);
            List<String> postUserPermissions = getPostPermissionsFromStatus(postUserStatus);
            List<String> permissionsAdded = new ArrayList<>(postUserPermissions);
            if (!preUserPermissions.isEmpty()) permissionsAdded.removeAll(preUserPermissions);
            aclChanges.put(userName, permissionsAdded);
            kafkaController.manageDatabaseChanges(aclChanges, true);
        } else {
            System.out.println("Failed to delete user from the database.");
        }

    }

    public void removeRole(String userName, String roleName) {
        if (!userExists(userName) || !roleExists(roleName)) {
            System.out.println("User or role does not exist");
            return;
        }
        if (!isUserAssignedToRole(userName, roleName)) {
            System.out.println("User is not assigned to role");
            return;
        }

        Map<String, List<String>> aclChanges = new HashMap<>();
        Map<String, List<String>> preUserStatus = userStatus(userName);
        List<String> preUserPermissions = getPermissionsFromStatus(preUserStatus);

        AtomicBoolean success = new AtomicBoolean(true);

        performDatabaseOperation(connection -> {
            String sql = "DELETE FROM UserRoles " +
                    "WHERE UserId = (SELECT UserId FROM Users WHERE UserName = ?) " +
                    "AND RoleId = (SELECT RoleId FROM Roles WHERE RoleName = ?)\n";
            try (var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, roleName);
                int rowsAffected = preparedStatement.executeUpdate();
                success.set(rowsAffected > 0);
            }
        });

        if (success.get()) {
            Map<String, List<String>> postUserStatus = userStatus(userName);
            List<String> postUserPermissions = getPostPermissionsFromStatus(postUserStatus);
            List<String> permissionsRemoved = new ArrayList<>(preUserPermissions);
            if (!postUserPermissions.isEmpty()) permissionsRemoved.removeAll(postUserPermissions);
            aclChanges.put(userName, permissionsRemoved);
            kafkaController.manageDatabaseChanges(aclChanges, false);
        } else {
            System.out.println("Failed to delete user from the database.");
        }
    }

    public Map<String, List<String>> userStatus(String userName) {
        if (!userExists(userName)) {
            System.out.println("User does not exist");
            return null;
        }

        Map<String, List<String>> rolesAndPermissions = new HashMap<>();
        String sql = "SELECT r.RoleName, r.Permissions FROM Users u JOIN UserRoles ur ON u.UserId = ur.UserId JOIN Roles r ON ur.RoleId = r.RoleId WHERE u.UserName = ?";

        performDatabaseOperation(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, userName);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String roleName = rs.getString("RoleName");
                        String permissionsJson = rs.getString("Permissions");
                        List<String> permissions = objectMapper.readValue(permissionsJson, new TypeReference<>() {
                        });
                        rolesAndPermissions.put(roleName, permissions);
                    }
                } catch (Exception e) {
                    System.out.println("Error retrieving user status: " + e.getMessage());
                }
            }
        });
        return rolesAndPermissions;
    }

    public List<String> roleStatus(String roleName) {
        if (!roleExists(roleName)) {
            System.out.println("Role does not exist");
            return null;
        }

        String sql = "SELECT r.Permissions FROM Roles r WHERE r.RoleName = ?";

        return new ArrayList<>(retrieveData(sql, roleName));
    }

    public Map<String, List<String>> userRoleStatus() {
        Map<String, List<String>> userRoles = new HashMap<>();

        String sql = "SELECT r.RoleName, u.UserName FROM Users u JOIN UserRoles ur ON u.UserId = ur.UserId JOIN Roles r ON ur.RoleId = r.RoleId";
        performDatabaseOperation(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String roleName = rs.getString("RoleName");
                        String userName = rs.getString("UserName");
                        if (userRoles.containsKey(roleName)) {
                            userRoles.get(roleName).add(userName);
                        } else {
                            List<String> users = new ArrayList<>();
                            users.add(userName);
                            userRoles.put(roleName, users);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error retrieving user status: " + e.getMessage());
                }
            }
        });
        return userRoles;
    }

    public List<String> getUsersForRole(String roleName) {
        if (!roleExists(roleName)) {
            System.out.println("Role does not exist");
            return Collections.emptyList();
        }
        String sql = "SELECT u.UserName FROM Users u JOIN UserRoles ur ON u.UserId = ur.UserId JOIN Roles r ON ur.RoleId = r.RoleId WHERE r.RoleName = ?";
        return new ArrayList<>(retrieveData(sql, roleName));
    }

    public List<String> getAllRoles() {
        String sql = "SELECT RoleName FROM Roles";
        List<String> results = new ArrayList<>();
        performDatabaseOperation(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getString(1));
                    }
                }
            }
        });
        return results;
    }

    public List<String> getAllUsers() {
        String sql = "SELECT UserName FROM Users";
        List<String> results = new ArrayList<>();
        performDatabaseOperation(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getString(1));
                    }
                }
            }
        });
        return results;
    }


    public boolean userExists(String userName) {
        return checkExists("SELECT COUNT(*) FROM Users WHERE UserName = ?", userName);
    }

    public boolean roleExists(String roleName) {
        return checkExists("SELECT COUNT(*) FROM Roles WHERE RoleName = ?", roleName);
    }

    public boolean permissionExistsForRole(String permission, String roleName) {
        AtomicBoolean exists = new AtomicBoolean(false);
        performDatabaseOperation(connection -> {
            String sql = "SELECT COUNT(*) FROM Roles WHERE RoleName = ? AND Permissions LIKE ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, roleName);
                preparedStatement.setString(2, "%" + permission + "%");
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        exists.set(rs.getInt(1) > 0);
                    }
                }
            }
        });
        return exists.get();
    }

    private boolean checkExists(String sql, String name) {
        AtomicBoolean exists = new AtomicBoolean(false);
        performDatabaseOperation(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        exists.set(rs.getInt(1) > 0);
                    }
                }
            }
        });
        return exists.get();
    }

    public List<String> retrieveData(String sql, String name) {
        List<String> results = new ArrayList<>();
        performDatabaseOperation(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getString(1));
                    }
                }
            }
        });
        return results;
    }

    public boolean updateData(String sql, Object... params) {
        AtomicBoolean success = new AtomicBoolean(true);
        performDatabaseOperation(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
                int rowsAffected = preparedStatement.executeUpdate();
                success.set(rowsAffected > 0);
            }
        });
        return success.get();
    }

    public boolean isUserAssignedToRole(String userName, String roleName) {
        AtomicBoolean isAssigned = new AtomicBoolean(false);
        performDatabaseOperation(connection -> {
            String sql = "SELECT COUNT(*) FROM UserRoles WHERE UserId = (SELECT UserId FROM Users WHERE UserName = ?) AND RoleId = (SELECT RoleId FROM Roles WHERE RoleName = ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, roleName);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        isAssigned.set(rs.getInt(1) > 0);
                    }
                }
            }
        });
        return isAssigned.get();
    }

    public List<String> getPermissionsFromStatus(Map<String, List<String>> userStatus) {
        List<String> uniquePermissions = new ArrayList<>();
        if (userStatus == null) return uniquePermissions;
        for (Map.Entry<String, List<String>> entry : userStatus.entrySet()) {
            for (String permission : entry.getValue()) {
                if (!uniquePermissions.contains(permission)) {
                    uniquePermissions.add(permission);
                }
            }
        }
        return uniquePermissions;
    }

    public List<String> getPostPermissionsFromStatus(Map<String, List<String>> userStatus) {
        return getPermissionsFromStatus(userStatus);
    }


    public synchronized void performDatabaseOperation(DatabaseOperation operation) {
        try {
            if (connection == null || connection.isClosed()) {
                connection = getConnection();
            }
            operation.execute(connection);
        } catch (SQLException e) {
            System.out.println("Error connecting to SQLite database: " + e.getMessage());
        }

    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing the SQLite database connection: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

}
