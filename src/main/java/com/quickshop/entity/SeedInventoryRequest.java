package com.quickshop.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SeedInventoryRequest(
     @NotBlank String productId,
     @Min(0) Integer availableQty
) {}