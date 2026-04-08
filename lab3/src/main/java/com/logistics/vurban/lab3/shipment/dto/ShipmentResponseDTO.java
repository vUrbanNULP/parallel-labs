package com.logistics.vurban.lab3.shipment.dto;

import com.logistics.vurban.lab3.client.dto.ClientResponseDTO;
import com.logistics.vurban.lab3.common.model.Status;
import com.logistics.vurban.lab3.warehouse.dto.WarehouseResponseDTO;
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