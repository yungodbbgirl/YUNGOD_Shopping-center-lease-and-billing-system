package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private static final UserDao userDao = new UserDao();
    private static final Scanner scanner = new Scanner(System.in);

    public static void displayAdminMenu(User currentUser) {
        while (true) {
            System.out.println("\n==================================================");
            System.out.println("            ADMINISTRATOR MANAGEMENT MENU         ");
            System.out.println("==================================================");
            System.out.println("[1] View All System Users");
            System.out.println("[2] View Pending Registrations");
            System.out.println("[3] Approve User Account");
            System.out.println("[4] Update User Role and Status");
            System.out.println("[5] Logout / Return to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingUsers();
                case "3" -> approveUserAccount();
                case "4" -> updateUserRoleAndStatus(currentUser);
                case "5" -> {
                    System.out.println("Logging out from Admin menu...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void viewAllUsers() {
        List<User> users = userDao.getAllUsers();
        printUserTable(users, "ALL SYSTEM USERS");
    }

    private static void viewPendingUsers() {
        List<User> users = userDao.getPendingUsers();
        printUserTable(users, "PENDING REGISTRATIONS");
    }

    private static void approveUserAccount() {
        List<User> pendingUsers = userDao.getPendingUsers();
        if (pendingUsers.isEmpty()) {
            System.out.println("\nNo pending user accounts to approve.");
            return;
        }

        printUserTable(pendingUsers, "PENDING ACCOUNTS");
        int userId = readIntInput("Enter User ID to APPROVE (or enter 0 to cancel): ");
        if (userId == 0) {
            System.out.println("Approval action cancelled.");
            return;
        }

        boolean exists = pendingUsers.stream().anyMatch(u -> u.userId() == userId);
        if (!exists) {
            System.out.println("Invalid User ID or user is not in the pending list.");
            return;
        }

        boolean success = userDao.approveUser(userId);
        if (success) {
            System.out.println("Success! User ID " + userId + " status has been updated to 'APPROVED'.");
        } else {
            System.out.println("Failed to approve user account.");
        }
    }

    private static void updateUserRoleAndStatus(User currentUser) {
        List<User> users = userDao.getAllUsers();
        printUserTable(users, "ALL SYSTEM USERS");

        int userId = readIntInput("Enter User ID to UPDATE (or enter 0 to cancel): ");
        if (userId == 0) {
            System.out.println("Update action cancelled.");
            return;
        }

        // Self-Edit Restriction Rule
        if (userId == currentUser.userId()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        boolean exists = users.stream().anyMatch(u -> u.userId() == userId);
        if (!exists) {
            System.out.println("Invalid User ID. User does not exist.");
            return;
        }

        String newRole = readValidRole();
        String newStatus = readValidStatus();

        boolean success = userDao.updateUserRoleAndStatus(userId, newRole, newStatus);
        if (success) {
            System.out.println("Success! User ID " + userId + " updated to Role: " + newRole + ", Status: " + newStatus);
        } else {
            System.out.println("Failed to update user account.");
        }
    }

    // --- Input Validation Wrappers ---

    private static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric User ID.");
            }
        }
    }

    private static String readValidRole() {
        while (true) {
            System.out.print("Enter valid role (e.g., Property Manager, Billing Staff, Financial Auditor): ");
            String role = scanner.nextLine().trim();
            if (!role.isEmpty()) {
                return role.toUpperCase();
            }
            System.out.println("Role cannot be empty. Please try again.");
        }
    }

    private static String readValidStatus() {
        while (true) {
            System.out.print("Enter valid status (APPROVED, PENDING): ");
            String status = scanner.nextLine().trim().toUpperCase();
            if (status.equals("APPROVED") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("Invalid status. Only APPROVED or PENDING are accepted.");
        }
    }

    private static void printUserTable(List<User> users, String title) {
        System.out.println("\n--- " + title + " ---");
        if (users.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        String format = "| %-6s | %-15s | %-28s | %-10s |%n";
        String border = "+--------+-----------------+------------------------------+------------+";

        System.out.println(border);
        System.out.printf(format, "ID", "Username", "Role", "Status");
        System.out.println(border);

        for (User u : users) {
            System.out.printf(format, u.userId(), u.username(), u.role(), u.status());
        }
        System.out.println(border);
    }
}