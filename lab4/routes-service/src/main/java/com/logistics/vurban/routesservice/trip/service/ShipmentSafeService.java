package com.logistics.vurban.routesservice.trip.service;

import com.logistics.vurban.routesservice.client.feign.ShipmentClient;
import com.logistics.vurban.routesservice.trip.dto.ShipmentLogisticsUpdateRequest;
import com.logistics.vurban.routesservice.trip.dto.ShipmentResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentSafeService {
    private final ShipmentClient shipmentClient;

    @CircuitBreaker(name = "coreService", fallbackMethod = "fallbackShipment")
    public ShipmentResponseDTO getShipmentById(Long id) {
        return shipmentClient.getShipmentById(id);
    }

    @CircuitBreaker(name = "coreService", fallbackMethod = "fallbackShipments")
    public List<ShipmentResponseDTO> getShipmentsByTripId(Long tripId) {
        return shipmentClient.getShipmentsByTripId(tripId);
    }

    @CircuitBreaker(name = "coreService", fallbackMethod = "fallbackVoid")
    public void updateShipmentLogisticsData(ShipmentLogisticsUpdateRequest request) {
        shipmentClient.updateShipmentLogisticsData(request);
    }

    private ShipmentResponseDTO fallbackShipment(Long id, Throwable t) {
        throw new RuntimeException("Shipment Service down. Verification failed.");
    }
    private List<ShipmentResponseDTO> fallbackShipments(Long tripId, Throwable t) {
        return java.util.Collections.emptyList();
    }
    private void fallbackVoid(ShipmentLogisticsUpdateRequest request, Throwable t) {
        throw new RuntimeException("Shipment Service down. Logistics update aborted.");
    }
}