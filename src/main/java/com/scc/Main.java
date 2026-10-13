package com.scc;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Main {
    private static final UserDao userDao = new UserDao();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
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
                case "1" -> handleRegister();
                case "2" -> handleLogin();
                case "3" -> {
                    System.out.println("Exiting application. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleRegister() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.println("Select System User Role:");
        System.out.println("[1] Shopping Center Property Manager");
        System.out.println("[2] Lease & Billing Staff / Cashier");
        System.out.println("[3] Shopping Center Financial Auditor");
        System.out.print("Choose role option: ");

        String roleChoice = scanner.nextLine();
        String role = switch (roleChoice) {
            case "1" -> "Property Manager";
            case "2" -> "Billing Staff / Cashier";
            case "3" -> "Financial Auditor";
            default -> "Billing Staff / Cashier";
        };

        boolean success = userDao.registerUser(username, password, role);
        if (success) {
            System.out.println("Registration successful! Account status is PENDING.");
        }
    }

    private static void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDao.loginUser(username, password);
        if (user != null) {
            System.out.println("\nLogin successful! Welcome, " + user.username() + " (" + user.role() + ")");
            runUserSession(user);
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void runUserSession(User user) {
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