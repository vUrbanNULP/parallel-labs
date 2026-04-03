package com.logistics.vurban.lab2.warehouse.repository;

import com.logistics.vurban.lab2.warehouse.model.Warehouse;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class WarehouseRepository {
    private final Map<Long, Warehouse> warehouses = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Warehouse save(Warehouse warehouse) {
        if (warehouse.getId() == null) {
            warehouse.setId(idGenerator.getAndIncrement());
            warehouse.setCurrentLoad(0.0); // Початкове завантаження порожнього складу
        }
        warehouses.put(warehouse.getId(), warehouse);
        return warehouse;
    }

    public List<Warehouse> findAll() {
        return new ArrayList<>(warehouses.values());
    }

    public Optional<Warehouse> findById(Long id) {
        return Optional.ofNullable(warehouses.get(id));
    }
}