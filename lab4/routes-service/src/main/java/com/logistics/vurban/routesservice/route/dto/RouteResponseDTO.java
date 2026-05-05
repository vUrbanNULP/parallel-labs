package com.logistics.vurban.routesservice.route.dto;

import com.logistics.vurban.routesservice.warehouse.dto.WarehouseResponseDTO;
import lombok.Data;

@Data
public class RouteResponseDTO {
    private Long id;
    private WarehouseResponseDTO origin;
    private WarehouseResponseDTO destination;
    private Double distanceKm;
    private Integer estimatedDurationHours;
}