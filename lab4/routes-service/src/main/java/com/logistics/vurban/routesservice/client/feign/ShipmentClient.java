package com.logistics.vurban.routesservice.client.feign;

import com.logistics.vurban.routesservice.trip.dto.ShipmentLogisticsUpdateRequest;
import com.logistics.vurban.routesservice.trip.dto.ShipmentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "shipment-service", url = "http://localhost:8082/api")
public interface ShipmentClient {
    @GetMapping("/shipments/{id}")
    ShipmentResponseDTO getShipmentById(@PathVariable("id") Long id);

    @GetMapping("/shipments/internal/trip/{tripId}")
    List<ShipmentResponseDTO> getShipmentsByTripId(@PathVariable("tripId") Long tripId);

    @PutMapping("/shipments/internal/update-logistics")
    void updateShipmentLogisticsData(@RequestBody ShipmentLogisticsUpdateRequest request);
}