package com.logistics.vurban.lab3.route.dto;

import com.logistics.vurban.lab3.transport.dto.TransportResponseDTO;
import com.logistics.vurban.lab3.warehouse.dto.WarehouseResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class RouteResponseDTO {
    private Long id;
    private WarehouseResponseDTO origin;
    private WarehouseResponseDTO destination;
    private Double distanceKm;
    private Integer estimatedDurationHours;
}