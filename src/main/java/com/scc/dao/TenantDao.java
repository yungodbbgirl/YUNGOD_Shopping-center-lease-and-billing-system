package com.scc.dao;

import com.scc.model.Tenant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TenantDao {

    public boolean registerTenant(String storeName, String unitCode, double leasedSqMeters) {
        String sql = "INSERT INTO tenants (store_name, unit_code, leased_sq_meters, status) VALUES (?, ?, ?, 'ACTIVE')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, storeName);
            pstmt.setString(2, unitCode);
            pstmt.setDouble(3, leasedSqMeters);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error registering tenant: " + e.getMessage());
            return false;
        }
    }

    public List<Tenant> getAllActiveTenants() {
        List<Tenant> tenants = new ArrayList<>();
        String sql = "SELECT tenant_id, store_name, unit_code, leased_sq_meters, status FROM tenants WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tenants.add(new Tenant(
                        rs.getInt("tenant_id"),
                        rs.getString("store_name"),
                        rs.getString("unit_code"),
                        rs.getDouble("leased_sq_meters"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching tenants: " + e.getMessage());
        }
        return tenants;
    }
}