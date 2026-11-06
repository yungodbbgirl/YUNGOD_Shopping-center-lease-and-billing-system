package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.BillDao;
import com.scc.dao.TenantDao;
import com.scc.dao.UserDao;
import com.scc.model.Bill;
import com.scc.model.Tenant;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final ActivityLogDao activityLogDao;
    private final TenantDao tenantDao;
    private final BillDao billDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.activityLogDao = new ActivityLogDao();
        this.tenantDao = new TenantDao();
        this.billDao = new BillDao();
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
        if ("Property Manager".equalsIgnoreCase(user.role())) {
            AdminMenu.displayAdminMenu(user);
            return;
        }

        if ("Billing Staff".equalsIgnoreCase(user.role())) {
            runBillingStaffSession(user);
            return;
        }

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

    private void runBillingStaffSession(User user) {
        while (true) {
            System.out.println("\n==================================================");
            System.out.println("             BILLING STAFF DASHBOARD              ");
            System.out.println("==================================================");
            System.out.println("[1] Register New Tenant & Lease Contract");
            System.out.println("[2] Enter Monthly Sub-Metered Utility Readings");
            System.out.println("[3] View/Process Statements of Account");
            System.out.println("[4] Update Payment Status (Unpaid/Overdue to Paid)");
            System.out.println("[5] View Profile Info");
            System.out.println("[6] Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.println("\n--- REGISTER NEW TENANT & LEASE ---");
                    System.out.print("Enter Store Name: ");
                    String storeName = scanner.nextLine().trim();

                    System.out.print("Enter Unit Code (e.g., U-101): ");
                    String unitCode = scanner.nextLine().trim();

                    System.out.print("Enter Leased Sq. Meters: ");
                    String sqMetersInput = scanner.nextLine().trim();

                    try {
                        double sqMeters = Double.parseDouble(sqMetersInput);
                        boolean success = tenantDao.registerTenant(storeName, unitCode, sqMeters);
                        if (success) {
                            activityLogDao.logAction(user.userId(), "TICKET_CREATED", "Billing Staff ID " + user.userId() + " registered new tenant: " + storeName + " (" + unitCode + ")");
                            System.out.println("Success! Tenant and leasing contract recorded successfully.");
                        } else {
                            System.out.println("Registration failed. Store Name or Unit Code might already be taken.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format for square meters. Please try again.");
                    }
                }
                case "2" -> {
                    System.out.println("\n--- UTILITY READINGS ENTRY & BILL GENERATION ---");
                    List<Tenant> activeTenants = tenantDao.getAllActiveTenants();

                    if (activeTenants.isEmpty()) {
                        System.out.println("No registered active tenants found. Please register a tenant first.");
                        break;
                    }

                    String format = "| %-5s | %-20s | %-10s | %-15s |%n";
                    String border = "+-------+----------------------+------------+-----------------+";
                    System.out.println(border);
                    System.out.printf(format, "ID", "Store Name", "Unit Code", "Leased Sqm");
                    System.out.println(border);
                    for (Tenant t : activeTenants) {
                        System.out.printf(format, t.tenantId(), t.storeName(), t.unitCode(), t.leasedSqMeters());
                    }
                    System.out.println(border);

                    System.out.print("Enter Tenant ID to generate bill: ");
                    String idInput = scanner.nextLine().trim();

                    try {
                        int tenantId = Integer.parseInt(idInput);
                        Tenant selectedTenant = activeTenants.stream().filter(t -> t.tenantId() == tenantId).findFirst().orElse(null);

                        if (selectedTenant == null) {
                            System.out.println("Invalid Tenant ID selected.");
                            break;
                        }

                        System.out.print("Enter Electricity Consumption (kWh): ");
                        double kwh = Double.parseDouble(scanner.nextLine().trim());
                        double electricityAmount = kwh * 12.50; // Rate per kWh example

                        System.out.print("Enter Water Consumption (cu. m): ");
                        double water = Double.parseDouble(scanner.nextLine().trim());
                        double waterAmount = water * 50.00; // Rate per cu.m example

                        // Base lease computation (e.g., 500 per sq meter)
                        double baseLease = selectedTenant.leasedSqMeters() * 500.00;

                        boolean success = billDao.createBill(tenantId, baseLease, electricityAmount, waterAmount);
                        if (success) {
                            activityLogDao.logAction(user.userId(), "BILL_PROCESSED", "Billing Staff ID " + user.userId() + " generated itemized bill for Tenant ID " + tenantId);
                            System.out.println("Success! Utility readings recorded and itemized billing invoice generated.");
                        } else {
                            System.out.println("Failed to generate bill.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Invalid numeric input. Please try again.");
                    }
                }
                case "3" -> {
                    System.out.println("\n--- STATEMENTS OF ACCOUNT (ITEMIZED) ---");
                    List<Bill> bills = billDao.getAllBills();
                    if (bills.isEmpty()) {
                        System.out.println("No billing statements found.");
                        break;
                    }

                    String format = "| %-6s | %-15s | %-10s | %-10s | %-10s | %-8s | %-8s | %-10s | %-8s |%n";
                    String border = "+--------+-----------------+------------+------------+------------+----------+----------+------------+----------+";
                    System.out.println(border);
                    System.out.printf(format, "Bill ID", "Store Name", "Base Lease", "Electricity", "Water", "VAT", "Penalty", "Total", "Status");
                    System.out.println(border);

                    for (Bill b : bills) {
                        System.out.printf(format, b.billId(), b.storeName(), String.format("%.2f", b.baseLease()), String.format("%.2f", b.electricityAmount()), String.format("%.2f", b.waterAmount()), String.format("%.2f", b.vat()), String.format("%.2f", b.penalty()), String.format("%.2f", b.totalAmount()), b.status());
                    }
                    System.out.println(border);
                }
                case "4" -> {
                    System.out.println("\n--- Update Payment Status ---");
                    System.out.print("Enter Bill ID to update status to PAID: ");
                    String billIdInput = scanner.nextLine().trim();

                    try {
                        int billId = Integer.parseInt(billIdInput);
                        boolean success = billDao.updateBillStatusToPaid(billId);
                        if (success) {
                            activityLogDao.logAction(user.userId(), "BILL_PROCESSED", "Billing Staff ID " + user.userId() + " updated Bill ID " + billId + " status to PAID");
                            System.out.println("Success! Payment status updated to Paid.");
                        } else {
                            System.out.println("Failed to update status. Invalid Bill ID.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Bill ID format.");
                    }
                }
                case "5" -> {
                    System.out.println("\n--- User Profile ---");
                    System.out.println("User ID: " + user.userId());
                    System.out.println("Username: " + user.username());
                    System.out.println("Role: " + user.role());
                    System.out.println("Status: " + user.status());
                }
                case "6" -> {
                    System.out.println("Logging out from Billing Staff dashboard...");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}