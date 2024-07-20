package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.InventoryItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    @Query("SELECT i FROM InventoryItem i WHERE i.productId = :productId")
    InventoryItem findByProductId(@Param("productId") Long productId);

    @Query("SELECT i.quantity FROM InventoryItem i WHERE i.productId = :productId")
    Integer getAvailableQuantity(@Param("productId") Long productId);

    @Modifying
    @Transactional
    @Query("UPDATE InventoryItem i SET i.quantity = i.quantity - :quantity WHERE i.productId = :productId AND i.quantity >= :quantity")
    void reduceStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}