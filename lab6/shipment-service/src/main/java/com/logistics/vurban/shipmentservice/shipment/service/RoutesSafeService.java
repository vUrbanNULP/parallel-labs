package com.logistics.vurban.shipmentservice.shipment.service;

import com.logistics.vurban.shipmentservice.client.feign.RoutesClient;
import com.logistics.vurban.shipmentservice.shipment.dto.RouteResponseDTO;
import com.logistics.vurban.shipmentservice.shipment.dto.WarehouseResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoutesSafeService {
    private final RoutesClient routesClient;

    @CircuitBreaker(name = "coreService", fallbackMethod = "fallbackWarehouse")
    public WarehouseResponseDTO getWarehouseById(Long id) {
        return routesClient.getWarehouseById(id);
    }

    @CircuitBreaker(name = "coreService", fallbackMethod = "fallbackRoute")
    public RouteResponseDTO getRouteForPricing(Long originId, Long destId) {
        return routesClient.getRouteForPricing(originId, destId);
    }

    @CircuitBreaker(name = "coreService", fallbackMethod = "fallbackVoid")
    public void addLoad(Long id, Double weight) {
        routesClient.addLoad(id, weight);
    }

    @CircuitBreaker(name = "coreService", fallbackMethod = "fallbackVoid")
    public void removeLoad(Long id, Double weight) {
        routesClient.removeLoad(id, weight);
    }

    private WarehouseResponseDTO fallbackWarehouse(Long id, Throwable t) {
        throw new RuntimeException("Logistics service down. Cannot retrieve warehouse data.");
    }
    private RouteResponseDTO fallbackRoute(Long originId, Long destId, Throwable t) {
        throw new RuntimeException("Pricing service down. Cannot calculate cost.");
    }
    private void fallbackVoid(Long id, Double weight, Throwable t) {
        throw new RuntimeException("Warehouse management service down. Capacity update failed.");
    }
}