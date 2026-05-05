package com.logistics.vurban.shipmentservice.shipment.dto;

import com.logistics.vurban.shipmentservice.client.dto.ClientResponseDTO;
import com.logistics.vurban.shipmentservice.common.model.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipmentResponseDTO {
    private Long id;
    private String trackingNumber;
    private String description;
    private Double weight;
    private Status status;

    private ClientResponseDTO sender;
    private ClientResponseDTO receiver;
    private WarehouseResponseDTO originWarehouse;
    private WarehouseResponseDTO destinationWarehouse;
    private WarehouseResponseDTO currentWarehouse;
    private Double cost;
    private LocalDateTime createdAt;
    private LocalDateTime dispatchedAt;
    private LocalDateTime deliveredAt;
}