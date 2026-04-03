package com.logistics.vurban.lab2.warehouse.service;

import com.logistics.vurban.lab2.common.exception.ResourceNotFoundException;
import com.logistics.vurban.lab2.warehouse.dto.WarehouseRequestDTO;
import com.logistics.vurban.lab2.warehouse.dto.WarehouseResponseDTO;
import com.logistics.vurban.lab2.warehouse.model.Warehouse;
import com.logistics.vurban.lab2.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseResponseDTO createWarehouse(WarehouseRequestDTO request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setMaxCapacity(request.getMaxCapacity());

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return mapToResponse(savedWarehouse);
    }

    public List<WarehouseResponseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public WarehouseResponseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        return mapToResponse(warehouse);
    }

    private WarehouseResponseDTO mapToResponse(Warehouse warehouse) {
        WarehouseResponseDTO response = new WarehouseResponseDTO();
        response.setId(warehouse.getId());
        response.setName(warehouse.getName());
        response.setAddress(warehouse.getAddress());
        response.setMaxCapacity(warehouse.getMaxCapacity());
        response.setCurrentLoad(warehouse.getCurrentLoad());
        return response;
    }
}