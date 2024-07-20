package com.example.deliveryservice.repository;

import com.example.deliveryservice.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<InventoryItem, Long> {
}
