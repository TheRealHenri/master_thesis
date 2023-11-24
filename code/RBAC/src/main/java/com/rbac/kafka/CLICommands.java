package com.rbac.kafka;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Parameters(commandDescription = "CLI Commands for Kafka's RBAC")
public class CLICommands {

    private final DatabaseManager databaseManager;

    @Parameter(description = "Command to execute")
    private String command;

    @Parameter(names = {"--userName"}, description = "Name of the user to add")
    private String userName;

    @Parameter(names = {"--roleName"}, description = "Name of the role to add")
    private String roleName;

    @Parameter(names = {"--topicName"}, description = "Name of the authorized topic")
    private String topicName;

    @Parameter(names = {"--permissions"}, description = "List of authorized topics")
    private List<String> permissions;

    public CLICommands(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void runCommand() {
        switch (command) {
            case "help":
                System.out.println("Available commands:");
                System.out.println("addRole --roleName <roleName> --permissions <topic1,topic2>");
                System.out.println("deleteRole --roleName <roleName>");
                System.out.println("addUser --userName <userName>");
                System.out.println("deleteUser --userName <userName>");
                System.out.println("addPermission --roleName <roleName> --topicName <topicName>");
                System.out.println("removePermission --roleName <roleName> --topicName <topicName>");
                System.out.println("assignRole --userName <userName> --roleName <roleName>");
                System.out.println("removeRole --userName <userName> --roleName <roleName>");
                System.out.println("userStatus --userName <userName>");
                System.out.println("roleStatus --roleName <roleName>");
                System.out.println("roles");
                System.out.println("users");
                System.out.println("userRoleStatus");
                System.out.println("exit");
                break;
            case "addRole":
                System.out.println("Adding role " + roleName + " with permissions " + permissions);
                if (permissions == null || permissions.isEmpty()) {
                    databaseManager.addRole(roleName, Collections.emptyList());
                    break;
                } else {
                    databaseManager.addRole(roleName, permissions);
                }
                break;
            case "deleteRole":
                System.out.println("Deleting role " + roleName);
                databaseManager.deleteRole(roleName);
                break;
            case "addUser":
                System.out.println("Adding user " + userName);
                databaseManager.addUser(userName);
                break;
            case "deleteUser":
                System.out.println("Deleting user " + userName);
                databaseManager.deleteUser(userName);
                break;
            case "addPermission":
                System.out.println("Adding permission " + topicName + " to role " + roleName);
                databaseManager.addPermission(roleName, topicName);
                break;
            case "removePermission":
                System.out.println("Removing permission " + topicName + " from role " + roleName);
                databaseManager.removePermission(roleName, topicName);
                break;
            case "assignRole":
                System.out.println("Assigning role " + roleName + " to user " + userName);
                databaseManager.assignRole(userName, roleName);
                break;
            case "removeRole":
                System.out.println("Removing role " + roleName + " from user " + userName);
                databaseManager.removeRole(userName, roleName);
                break;
            case "userStatus":
                System.out.println("Printing status for user " + userName);
                Map<String, List<String>> userStatus = databaseManager.userStatus(userName);
                for(Map.Entry<String, List<String>> entry : userStatus.entrySet()) {
                    System.out.println("Role " + entry.getKey() + " with permissions " + entry.getValue());
                }
                break;
            case "roleStatus":
                System.out.println("Permissions for role " + roleName + " : " + databaseManager.roleStatus(roleName));
                break;
            case "roles":
                System.out.println(databaseManager.getAllRoles());
                break;
            case "users":
                System.out.println(databaseManager.getAllUsers());
                break;
            case "userRoleStatus":
                Map<String, List<String>> userRoleStatus = databaseManager.userRoleStatus();
                for(Map.Entry<String, List<String>> entry : userRoleStatus.entrySet()) {
                    System.out.println("Role " + entry.getKey() + " with users " + entry.getValue());
                }
                break;
            default:
                System.out.println("Invalid command. Type 'help' to see available commands");
                break;
        }
    }


}
