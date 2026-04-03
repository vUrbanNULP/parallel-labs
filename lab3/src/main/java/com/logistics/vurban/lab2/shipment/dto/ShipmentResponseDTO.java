package com.logistics.vurban.lab2.shipment.dto;

import com.logistics.vurban.lab2.client.dto.ClientResponseDTO;
import com.logistics.vurban.lab2.common.model.Status;
import com.logistics.vurban.lab2.warehouse.dto.WarehouseResponseDTO;
import lombok.Data;

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
}