package com.quickshop.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.quickshop.entity.ProcessedEvent;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}