package com.logistics.vurban.lab3.transport.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransportRequestDTO {
    @NotBlank(message = "Vehicle type is mandatory")
    private String vehicleType;

    @NotBlank(message = "License plate is mandatory")
    private String licensePlate;

    @NotNull(message = "Load capacity is mandatory")
    @Min(value = 1, message = "Load capacity must be at least 1")
    private Double loadCapacity;

    @NotNull(message = "Is available is mandatory")
    private Boolean isAvailable = true;
}