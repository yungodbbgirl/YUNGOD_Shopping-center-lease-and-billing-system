package com.scc.model;

public record Tenant(int tenantId, String storeName, String unitCode, double leasedSqMeters, String status) {}