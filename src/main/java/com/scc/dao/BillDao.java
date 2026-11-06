package com.scc.dao;

import com.scc.model.Bill;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDao {

    public boolean createBill(int tenantId, double baseLease, double electricity, double water) {
        double vat = (baseLease + electricity + water) * 0.12; // 12% VAT
        double penalty = 0.0;
        double total = baseLease + electricity + water + vat + penalty;

        String sql = "INSERT INTO bills (tenant_id, base_lease, electricity_amount, water_amount, vat, penalty, total_amount, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'UNPAID')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tenantId);
            pstmt.setDouble(2, baseLease);
            pstmt.setDouble(3, electricity);
            pstmt.setDouble(4, water);
            pstmt.setDouble(5, vat);
            pstmt.setDouble(6, penalty);
            pstmt.setDouble(7, total);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error creating bill: " + e.getMessage());
            return false;
        }
    }

    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = """
            SELECT b.bill_id, b.tenant_id, t.store_name, b.base_lease, b.electricity_amount, 
                   b.water_amount, b.vat, b.penalty, b.total_amount, b.status, b.billing_date 
            FROM bills b JOIN tenants t ON b.tenant_id = t.tenant_id
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bills.add(new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("tenant_id"),
                        rs.getString("store_name"),
                        rs.getDouble("base_lease"),
                        rs.getDouble("electricity_amount"),
                        rs.getDouble("water_amount"),
                        rs.getDouble("vat"),
                        rs.getDouble("penalty"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("billing_date")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching bills: " + e.getMessage());
        }
        return bills;
    }

    public boolean updateBillStatusToPaid(int billId) {
        String sql = "UPDATE bills SET status = 'PAID' WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating bill status: " + e.getMessage());
            return false;
        }
    }
}