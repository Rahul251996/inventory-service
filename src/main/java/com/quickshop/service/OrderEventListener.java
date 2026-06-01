package com.quickshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quickshop.entity.InventoryItem;
import com.quickshop.entity.OrderCreatedEvent;
import com.quickshop.entity.ProcessedEvent;
import com.quickshop.repository.InventoryItemRepository;
import com.quickshop.repository.ProcessedEventRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Component
public class OrderEventListener {

    @Autowired
    public  InventoryItemRepository inventoryRepository;

    @Autowired
    public  ProcessedEventRepository processedEventRepository;


    public ObjectMapper objectMapper;
 
 @PostConstruct
 public void init() {
     System.out.println("OrderEventListener Loaded");
     this.objectMapper = new ObjectMapper();
     this.objectMapper.registerModule(new JavaTimeModule());
 }

 
 @KafkaListener(topics = "order-queue",groupId = "rahul-test-group-reset")
 public void handleOrderCreated(String message) {

     System.out.println("#################################");
     System.out.println("MESSAGE RECEIVED");
     System.out.println(message);
     System.out.println("#################################");
     try {
         OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);

         
         // Idempotency check
         if (processedEventRepository.existsById(event.eventId())) {
             log.info("Event {} already processed, skipping.", event.eventId());
             return;
         }

         // For each item, check and reserve stock
         boolean allAvailable = event.items().stream().allMatch(item -> {
             Optional<InventoryItem> opt = inventoryRepository.findByProductId(item.productId());
             if (opt.isEmpty()) return false;
             InventoryItem inv = opt.get();
             return inv.getAvailableQty() >= item.quantity();
         });

         if (!allAvailable) {
             log.warn("Insufficient stock for order {}", event.orderId());
             // TODO: publish order.rejected event here
         } else {
             event.items().forEach(item -> {
                 InventoryItem inv = inventoryRepository.findByProductId(item.productId())
                         .orElseThrow(() -> new IllegalStateException("Inventory missing: " + item.productId()));
                 inv.setAvailableQty(inv.getAvailableQty() - item.quantity());
                 inv.setReservedQty(inv.getReservedQty() + item.quantity());
                 inventoryRepository.save(inv);
             });

             log.info("Inventory reserved for order {}", event.orderId());
             // TODO: publish order.confirmed event
         }

         processedEventRepository.save(ProcessedEvent.builder()
                 .eventId(event.eventId())
                 .build());

     } catch (Exception e) {
         log.error("Failed to process message {}, sending to DLQ maybe", message, e);
         // TODO: send to DLQ topic via KafkaTemplate
         throw new RuntimeException(e);
     }
 }
}