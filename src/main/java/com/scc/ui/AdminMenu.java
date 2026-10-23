package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private static final UserDao userDao = new UserDao();
    private static final Scanner scanner = new Scanner(System.in);

    public static void displayAdminMenu() {
        while (true) {
            System.out.println("\n==================================================");
            System.out.println("            ADMINISTRATOR MANAGEMENT MENU         ");
            System.out.println("==================================================");
            System.out.println("[1] View All System Users");
            System.out.println("[2] View Pending Registrations");
            System.out.println("[3] Approve User Account");
            System.out.println("[4] Logout / Return to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingUsers();
                case "3" -> approveUserAccount();
                case "4" -> {
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
        System.out.print("Enter User ID to APPROVE (or enter 0 to cancel): ");

        String input = scanner.nextLine();
        try {
            int userId = Integer.parseInt(input);
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
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numeric User ID.");
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