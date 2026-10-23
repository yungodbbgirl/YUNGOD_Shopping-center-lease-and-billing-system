package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final Scanner scanner;

    // Accept shared instances of UserDao and Scanner to avoid duplicate resource creation[cite: 10]
    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void handleRegister() {
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

    public User handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDao.loginUser(username, password);
        if (user != null) {
            System.out.println("\nLogin successful! Welcome, " + user.username() + " (" + user.role() + ")");
            return user;
        } else {
            System.out.println("Invalid username or password.");
            return null;
        }
    }
}