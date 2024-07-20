package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryCheckRequest;
import com.example.inventoryservice.dto.InventoryCheckResponse;
import com.example.inventoryservice.dto.InventoryItemRequest;
import com.example.inventoryservice.model.InventoryItem;
import com.example.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/check-inventory/{orderId}")
    public ResponseEntity<String> checkInventory(@PathVariable Long orderId) {
        try {
            inventoryService.checkInventoryAndRespond(orderId);
            return ResponseEntity.ok("Inventory check initiated for order ID: " + orderId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to check inventory: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public void addInventoryItems(@RequestBody List<InventoryItemRequest> inventoryItems) {
        inventoryService.addInventoryItems(inventoryItems);
    }

    @GetMapping("/all")
    public List<InventoryItem> getAllInventoryItems() {
        return inventoryService.getAllInventoryItems();
    }
}