package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
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
                case "2" -> {
                    User user = handleLogin();
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

    public void handleRegister() {
        System.out.println("\n--- USER REGISTRATION ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        System.out.println("Select Role:");
        System.out.println("[1] Property Manager");
        System.out.println("[2] Billing Staff");
        System.out.println("[3] Financial Auditor");
        System.out.print("Choose role option: ");
        String roleChoice = scanner.nextLine().trim();

        String role = switch (roleChoice) {
            case "1" -> "Property Manager";
            case "2" -> "Billing Staff";
            case "3" -> "Financial Auditor";
            default -> {
                System.out.println("Invalid role choice. Defaulting to Billing Staff.");
                yield "Billing Staff";
            }
        };

        boolean success = userDao.registerUser(username, password, role);
        if (success) {
            System.out.println("Registration successful! Account status is PENDING approval.");
        } else {
            System.out.println("Registration failed. Username may already exist.");
        }
    }

    public User handleLogin() {
        System.out.println("\n--- USER LOGIN ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        User user = userDao.loginUser(username, password);

        if (user == null) {
            System.out.println("Login failed: Incorrect username or password.");
            return null;
        }

        if (!"APPROVED".equalsIgnoreCase(user.status())) {
            System.out.println("Login restricted: Your account is still PENDING approval.");
            return null;
        }

        System.out.println("Login successful! Welcome, " + user.username() + " (" + user.role() + ")");
        return user;
    }

    private void runUserSession(User user) {
        // Route Property Manager directly to Admin Menu, passing the current user session
        if ("Property Manager".equals(user.role())) {
            AdminMenu.displayAdminMenu(user);
            return;
        }

        // Default session for other roles
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