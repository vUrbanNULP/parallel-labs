package com.logistics.vurban.shipmentservice.shipment.controller;

import com.logistics.vurban.shipmentservice.shipment.dto.ShipmentRequestDTO;
import com.logistics.vurban.shipmentservice.shipment.dto.ShipmentResponseDTO;
import com.logistics.vurban.shipmentservice.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/search")
    public Page<ShipmentResponseDTO> search(
            @RequestParam Long clientId,
            @PageableDefault(size = 10) Pageable pageable) {
        return shipmentService.getClientShipments(clientId, pageable);
    }

    @GetMapping("/internal/trip/{tripId}")
    public java.util.List<ShipmentResponseDTO> getShipmentsByTripId(@PathVariable Long tripId) {
        return shipmentService.getShipmentsByTripId(tripId);
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

    @PutMapping("/{id}/deliver")
    public ShipmentResponseDTO deliver(@PathVariable Long id) {
        return shipmentService.deliverShipment(id);
    }

    @PutMapping("/internal/update-logistics")
    @ResponseStatus(HttpStatus.OK)
    public void updateShipmentLogisticsData(@RequestBody com.logistics.vurban.shipmentservice.shipment.dto.ShipmentLogisticsUpdateRequest request) {
        shipmentService.internalUpdateLogisticsData(
                request.getShipmentId(),
                request.getAssignedTripId(),
                request.getCurrentWarehouseId(),
                request.getStatus(),
                request.getDispatchedAt()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
    }
}