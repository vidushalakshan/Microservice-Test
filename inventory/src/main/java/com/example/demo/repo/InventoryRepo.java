package com.example.demo.repo;

import com.example.demo.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

//service walata saha controllers walata one karana data types thamai meke define kranne

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Integer> {
    @Query(value = "SELECT * FROM inventory WHERE id = ?1", nativeQuery = true)
    Inventory getItemById(Integer itemId);
}
