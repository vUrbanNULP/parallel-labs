package com.logistics.vurban.lab2.shipment.controller;

import com.logistics.vurban.lab2.shipment.dto.ShipmentRequestDTO;
import com.logistics.vurban.lab2.shipment.dto.ShipmentResponseDTO;
import com.logistics.vurban.lab2.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public List<ShipmentResponseDTO> getAll() {
        return shipmentService.getAllShipments();
    }

    @GetMapping("/{id}")
    public ShipmentResponseDTO getById(@PathVariable Long id) {
        return shipmentService.getShipmentById(id);
    }
}