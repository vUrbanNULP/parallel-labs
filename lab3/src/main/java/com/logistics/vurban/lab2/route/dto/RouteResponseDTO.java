package com.logistics.vurban.lab2.route.dto;

import com.logistics.vurban.lab2.transport.dto.TransportResponseDTO;
import com.logistics.vurban.lab2.warehouse.dto.WarehouseResponseDTO;
import lombok.Data;

@Data
public class RouteResponseDTO {
    private Long id;
    private WarehouseResponseDTO origin;
    private WarehouseResponseDTO destination;
    private TransportResponseDTO assignedTransport;
    private Double distanceKm;
    private Integer estimatedDurationHours;
}