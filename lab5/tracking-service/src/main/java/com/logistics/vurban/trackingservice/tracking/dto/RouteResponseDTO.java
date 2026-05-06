package com.logistics.vurban.trackingservice.tracking.dto;

import com.logistics.vurban.trackingservice.tracking.dto.WarehouseResponseDTO;
import lombok.Data;

@Data
public class RouteResponseDTO {
    private Long id;
    private WarehouseResponseDTO origin;
    private WarehouseResponseDTO destination;
    private Double distanceKm;
    private Integer estimatedDurationHours;
}