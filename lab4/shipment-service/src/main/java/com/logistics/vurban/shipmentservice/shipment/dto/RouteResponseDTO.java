package com.logistics.vurban.shipmentservice.shipment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteResponseDTO {
    private Long id;
    private WarehouseResponseDTO origin;
    private WarehouseResponseDTO destination;
    private Double distanceKm;
    private Integer estimatedDurationHours;
}