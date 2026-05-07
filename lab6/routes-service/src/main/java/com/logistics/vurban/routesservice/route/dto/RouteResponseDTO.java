package com.logistics.vurban.routesservice.route.dto;

import com.logistics.vurban.routesservice.warehouse.dto.WarehouseResponseDTO;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class RouteResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private WarehouseResponseDTO origin;
    private WarehouseResponseDTO destination;
    private Double distanceKm;
    private Integer estimatedDurationHours;
}