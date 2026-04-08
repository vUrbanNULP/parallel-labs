package com.logistics.vurban.lab3.shipment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShipmentRequestDTO {
    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Weight is mandatory")
    @Min(value = 0, message = "Weight must be positive")
    private Double weight;

    @NotNull(message = "Sender ID is mandatory")
    private Long senderId;

    @NotNull(message = "Receiver ID is mandatory")
    private Long receiverId;

    @NotNull(message = "Origin Warehouse ID is mandatory")
    private Long originWarehouseId;

    @NotNull(message = "Destination Warehouse ID is mandatory")
    private Long destinationWarehouseId;
}