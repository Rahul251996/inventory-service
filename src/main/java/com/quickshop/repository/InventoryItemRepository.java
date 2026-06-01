package com.quickshop.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.quickshop.entity.InventoryItem;

import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

 Optional<InventoryItem> findByProductId(String productId);
}