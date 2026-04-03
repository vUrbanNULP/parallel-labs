package com.logistics.vurban.lab2.warehouse.controller;

import com.logistics.vurban.lab2.warehouse.dto.WarehouseRequestDTO;
import com.logistics.vurban.lab2.warehouse.dto.WarehouseResponseDTO;
import com.logistics.vurban.lab2.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseResponseDTO create(@Valid @RequestBody WarehouseRequestDTO request) {
        return warehouseService.createWarehouse(request);
    }

    @GetMapping
    public List<WarehouseResponseDTO> getAll() {
        return warehouseService.getAllWarehouses();
    }

    @GetMapping("/{id}")
    public WarehouseResponseDTO getById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id);
    }
}