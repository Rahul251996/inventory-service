package com.quickshop.controller;

import com.quickshop.common.entity.AuthenticatedUser;
import com.quickshop.common.entity.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.quickshop.entity.AdjustInventoryRequest;
import com.quickshop.entity.InventoryItemResponse;
import com.quickshop.entity.SeedInventoryRequest;
import com.quickshop.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

 private final InventoryService inventoryService;

 /**
  * Get inventory details for a productId
  */
 @GetMapping("/{productId}")
 public InventoryItemResponse getInventory(@AuthenticatedUser UserPrincipal userPrincipal,
         @PathVariable String productId) {
     return inventoryService.getByProductId(productId);
 }

 /**
  * List inventory (paginated)
  * Example: GET /api/inventory?page=0&size=10&sort=productId,asc
  */
 @GetMapping
 public Page<InventoryItemResponse> listInventory(@AuthenticatedUser UserPrincipal userPrincipal,
         Pageable pageable) {
     return inventoryService.listAll(pageable);
 }

 /**
  * Seed or overwrite inventory quantity for a product.
  * Use this for initial stock loading or reset.
  */
 @PostMapping("/seed")
 public InventoryItemResponse seedInventory(@AuthenticatedUser UserPrincipal userPrincipal,
                                            @Valid @RequestBody SeedInventoryRequest request) {
     return inventoryService.seedInventory(request);
 }

 /**
  * Adjust inventory quantity up/down.
  * e.g. quantityChange = +10 (restock), -5 (manual reduction / sale)
  */
 @PostMapping("/adjust")
 public InventoryItemResponse adjustInventory(@AuthenticatedUser UserPrincipal userPrincipal,
         @Valid @RequestBody AdjustInventoryRequest request) {
     return inventoryService.adjustInventory(request);
 }
}