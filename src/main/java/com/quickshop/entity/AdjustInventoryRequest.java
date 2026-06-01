package com.quickshop.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdjustInventoryRequest(
     @NotBlank String productId,

     /**
      * quantityChange > 0  => increase stock (restock)
      * quantityChange < 0  => decrease stock (manual adjustment or sale)
      */
     @NotNull Integer quantityChange
) {}