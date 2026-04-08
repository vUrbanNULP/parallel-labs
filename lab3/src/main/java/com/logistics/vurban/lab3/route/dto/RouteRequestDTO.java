package com.logistics.vurban.lab3.route.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RouteRequestDTO {
    @NotNull(message = "Origin Warehouse ID is mandatory")
    private Long originWarehouseId;

    @NotNull(message = "Destination Warehouse ID is mandatory")
    private Long destinationWarehouseId;

    @NotNull(message = "Distance is mandatory")
    @Min(value = 1, message = "Distance must be greater than 0")
    private Double distanceKm;

    @NotNull(message = "Estimated duration is mandatory")
    @Min(value = 1, message = "Duration must be greater than 0")
    private Integer estimatedDurationHours;
}