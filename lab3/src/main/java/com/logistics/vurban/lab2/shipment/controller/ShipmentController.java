package com.logistics.vurban.lab2.shipment.controller;

import com.logistics.vurban.lab2.shipment.dto.ShipmentRequestDTO;
import com.logistics.vurban.lab2.shipment.dto.ShipmentResponseDTO;
import com.logistics.vurban.lab2.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService shipmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShipmentResponseDTO create(@Valid @RequestBody ShipmentRequestDTO request) {
        return shipmentService.createShipment(request);
    }

    @GetMapping
    public Page<ShipmentResponseDTO> getAll(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        return shipmentService.getAllShipments(pageable);
    }

    @GetMapping("/{id}")
    public ShipmentResponseDTO getById(@PathVariable Long id) {
        return shipmentService.getShipmentById(id);
    }

    @PutMapping("/{id}")
    public ShipmentResponseDTO update(@PathVariable Long id, @Valid @RequestBody ShipmentRequestDTO request) {
        return shipmentService.updateShipment(id, request);
    }

    @PutMapping("/{id}/transfer")
    public ShipmentResponseDTO transferWarehouse(
            @PathVariable Long id,
            @RequestParam Long warehouseId) {
        return shipmentService.transferShipment(id, warehouseId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
    }
}