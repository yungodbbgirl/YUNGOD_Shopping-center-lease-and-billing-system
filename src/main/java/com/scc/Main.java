package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.model.User;
import com.scc.ui.AdminMenu;
import com.scc.ui.Authentication;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Database
        DatabaseInitializer.initializeDatabase();

        // 2. Instantiate shared DAO and Utilities to pass into Authentication
        UserDao userDao = new UserDao();
        Scanner scanner = new Scanner(System.in);
        Authentication auth = new Authentication(userDao, scanner);

        // 3. Main execution loop
        while (true) {
            System.out.println("\n==================================================");
            System.out.println(" SHOPPING CENTER LEASE & UTILITY BILLING SYSTEM ");
            System.out.println("==================================================");
            System.out.println("[1] Register Account");
            System.out.println("[2] Login");
            System.out.println("[3] Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> auth.handleRegister();
                case "2" -> {
                    User user = auth.handleLogin();
                    if (user != null) {
                        runUserSession(user);
                    }
                }
                case "3" -> {
                    System.out.println("Exiting application. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void runUserSession(User user) {
        // Route Property Manager directly to Admin Menu
        if ("Property Manager".equals(user.role())) {
            AdminMenu.displayAdminMenu();
            return;
        }

        // Default session for other roles
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Dashboard (" + user.role() + ") ---");
            System.out.println("[1] View Profile Info");
            System.out.println("[2] Logout");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.println("User ID: " + user.userId());
                System.out.println("Username: " + user.username());
                System.out.println("Role: " + user.role());
                System.out.println("Status: " + user.status());
            } else if (choice.equals("2")) {
                System.out.println("Logging out securely...");
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }
}