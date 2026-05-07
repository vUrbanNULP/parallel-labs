package com.logistics.vurban.routesservice.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WarehouseRequestDTO {
    @NotBlank(message = "Warehouse name is mandatory")
    private String name;

    @NotBlank(message = "Address is mandatory")
    private String address;

    @NotNull(message = "Capacity is mandatory")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Double maxCapacity;
}