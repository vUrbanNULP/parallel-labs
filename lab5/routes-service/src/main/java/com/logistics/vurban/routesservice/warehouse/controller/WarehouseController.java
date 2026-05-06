package com.logistics.vurban.routesservice.warehouse.controller;

import com.logistics.vurban.routesservice.warehouse.dto.WarehouseRequestDTO;
import com.logistics.vurban.routesservice.warehouse.dto.WarehouseResponseDTO;
import com.logistics.vurban.routesservice.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public Page<WarehouseResponseDTO> getAll(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        return warehouseService.getAllWarehouses(pageable);
    }

    @GetMapping("/{id}")
    public WarehouseResponseDTO getById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id);
    }

    @PutMapping("/{id}")
    public WarehouseResponseDTO update(@PathVariable Long id, @Valid @RequestBody WarehouseRequestDTO request) {
        return warehouseService.updateWarehouse(id, request);
    }

    @PutMapping("/{id}/load/add")
    @ResponseStatus(HttpStatus.OK)
    public void addLoad(@PathVariable Long id, @RequestParam Double weight) {
        warehouseService.addLoad(id, weight);
    }

    @PutMapping("/{id}/load/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeLoad(@PathVariable Long id, @RequestParam Double weight) {
        warehouseService.removeLoad(id, weight);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
    }


}