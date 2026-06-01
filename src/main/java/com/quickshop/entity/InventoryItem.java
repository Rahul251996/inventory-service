package com.quickshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_items", indexes = {
     @Index(name = "idx_inventory_product", columnList = "productId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String productId;

 private Integer availableQty;

 private Integer reservedQty;
}