package com.logistics.vurban.shipmentservice.client.feign;

import com.logistics.vurban.shipmentservice.shipment.dto.RouteResponseDTO;
import com.logistics.vurban.shipmentservice.shipment.dto.WarehouseResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "routes-service", url = "http://localhost:8081/api")
public interface RoutesClient {
    @GetMapping("/warehouses/{id}")
    WarehouseResponseDTO getWarehouseById(@PathVariable("id") Long id);

    @GetMapping("/routes/internal/pricing")
    RouteResponseDTO getRouteForPricing(@RequestParam("originId") Long originId, @RequestParam("destId") Long destId);

    @PutMapping("/warehouses/{id}/load/add")
    void addLoad(@PathVariable("id") Long id, @RequestParam("weight") Double weight);

    @PutMapping("/warehouses/{id}/load/remove")
    void removeLoad(@PathVariable("id") Long id, @RequestParam("weight") Double weight);
}