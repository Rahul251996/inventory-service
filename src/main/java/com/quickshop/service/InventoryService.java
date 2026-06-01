package com.quickshop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quickshop.entity.AdjustInventoryRequest;
import com.quickshop.entity.InventoryItem;
import com.quickshop.entity.InventoryItemResponse;
import com.quickshop.entity.SeedInventoryRequest;
import com.quickshop.repository.InventoryItemRepository;

@Service
@RequiredArgsConstructor
public class InventoryService {

 private final InventoryItemRepository inventoryItemRepository;

 @Transactional(readOnly = true)
 public InventoryItemResponse getByProductId(String productId) {
     InventoryItem item = inventoryItemRepository.findByProductId(productId)
             .orElseThrow(() -> new EntityNotFoundException("Inventory not found for productId: " + productId));
     return toResponse(item);
 }

 /**
  * Seed or overwrite available quantity for a product
  */
 @Transactional
 public InventoryItemResponse seedInventory(SeedInventoryRequest request) {
     InventoryItem item = inventoryItemRepository.findByProductId(request.productId())
             .orElse(InventoryItem.builder()
                     .productId(request.productId())
                     .availableQty(0)
                     .reservedQty(0)
                     .build());

     item.setAvailableQty(request.availableQty());
     // reservedQty untouched here

     InventoryItem saved = inventoryItemRepository.save(item);
     return toResponse(saved);
 }

 /**
  * Adjust available quantity up or down.
  * For negative changes we ensure no negative availableQty.
  */
 @Transactional
 public InventoryItemResponse adjustInventory(AdjustInventoryRequest request) {
     InventoryItem item = inventoryItemRepository.findByProductId(request.productId())
             .orElseThrow(() -> new EntityNotFoundException("Inventory not found for productId: " + request.productId()));

     int newAvailable = item.getAvailableQty() + request.quantityChange();
     if (newAvailable < 0) {
         throw new IllegalArgumentException("Adjustment would make availableQty negative");
     }
     item.setAvailableQty(newAvailable);

     InventoryItem saved = inventoryItemRepository.save(item);
     return toResponse(saved);
 }

 @Transactional(readOnly = true)
 public Page<InventoryItemResponse> listAll(Pageable pageable) {
     return inventoryItemRepository.findAll(pageable)
             .map(this::toResponse);
 }

 private InventoryItemResponse toResponse(InventoryItem item) {
     return new InventoryItemResponse(
             item.getId(),
             item.getProductId(),
             item.getAvailableQty(),
             item.getReservedQty()
     );
 }
}