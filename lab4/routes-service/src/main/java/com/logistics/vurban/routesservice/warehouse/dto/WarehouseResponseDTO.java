package com.logistics.vurban.routesservice.warehouse.dto;

import lombok.Data;

@Data
public class WarehouseResponseDTO {
    private Long id;
    private String name;
    private String address;
    private Double maxCapacity;
    private Double currentLoad;
}