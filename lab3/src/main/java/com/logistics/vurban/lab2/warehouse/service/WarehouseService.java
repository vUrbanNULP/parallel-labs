package com.logistics.vurban.lab2.warehouse.service;

import com.logistics.vurban.lab2.common.exception.ResourceNotFoundException;
import com.logistics.vurban.lab2.warehouse.dto.WarehouseRequestDTO;
import com.logistics.vurban.lab2.warehouse.dto.WarehouseResponseDTO;
import com.logistics.vurban.lab2.warehouse.model.Warehouse;
import com.logistics.vurban.lab2.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
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

    public Page<WarehouseResponseDTO> getAllWarehouses(Pageable pageable) {
        return warehouseRepository.findAll(pageable).map(this::mapToResponse);
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

    @Transactional
    public WarehouseResponseDTO updateWarehouse(Long id, WarehouseRequestDTO request) {
        Warehouse existing = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        // --- ДОДАНО: Захист від зменшення місткості нижче поточного завантаження ---
        if (request.getMaxCapacity() < existing.getCurrentLoad()) {
            throw new IllegalArgumentException(
                    String.format("Cannot reduce max capacity to %.2f. Current load is %.2f",
                            request.getMaxCapacity(), existing.getCurrentLoad())
            );
        }

        existing.setName(request.getName());
        existing.setAddress(request.getAddress());
        existing.setMaxCapacity(request.getMaxCapacity());

        return mapToResponse(warehouseRepository.save(existing));
    }

    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Warehouse not found with id: " + id);
        }
        warehouseRepository.deleteById(id);
    }

    public void addLoad(Long warehouseId, Double weightToAdd) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + warehouseId));

        if (warehouse.getCurrentLoad() + weightToAdd > warehouse.getMaxCapacity()) {
            throw new IllegalArgumentException(
                    String.format("Warehouse '%s' capacity exceeded. Available: %.2f, Tried to add: %.2f",
                            warehouse.getName(),
                            (warehouse.getMaxCapacity() - warehouse.getCurrentLoad()),
                            weightToAdd)
            );
        }
        warehouse.setCurrentLoad(warehouse.getCurrentLoad() + weightToAdd);
        warehouseRepository.save(warehouse);
    }

    public void removeLoad(Long warehouseId, Double weightToRemove) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + warehouseId));

        double newLoad = warehouse.getCurrentLoad() - weightToRemove;
        if (newLoad < 0) {
            newLoad = 0;
        }
        warehouse.setCurrentLoad(newLoad);
        warehouseRepository.save(warehouse);
    }
}