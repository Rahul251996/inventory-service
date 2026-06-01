package com.quickshop.entity;

public record InventoryItemResponse(
     Long id,
     String productId,
     Integer availableQty,
     Integer reservedQty
) {}