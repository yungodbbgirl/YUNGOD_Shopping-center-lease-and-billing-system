package com.scc.model;

public record Bill(int billId, int tenantId, String storeName, double baseLease, double electricityAmount, double waterAmount, double vat, double penalty, double totalAmount, String status, String billingDate) {}