package com.logistics.vurban.lab2.warehouse.repository;

import com.logistics.vurban.lab2.warehouse.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}