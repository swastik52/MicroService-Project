package com.microservice.inventoryservice.controller;


import com.microservice.inventoryservice.model.Inventory;
import com.microservice.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/inventory")
@RequiredArgsConstructor
public class InventoryRestController
{
    @Autowired
    private InventoryRepository repository;

    @GetMapping("/{skuCode}")
    Boolean isInStock(@PathVariable String skuCode){
        Inventory inventory= repository.findBySkuCode(skuCode).orElseThrow(()->new RuntimeException("Couldnot find Product by Sku Code "+skuCode));
        return inventory.getStock() > 0;
    }
}
