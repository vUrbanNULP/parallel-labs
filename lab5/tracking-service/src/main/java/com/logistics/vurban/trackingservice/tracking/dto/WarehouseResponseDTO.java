package com.logistics.vurban.trackingservice.tracking.dto;

import lombok.Data;

@Data
public class WarehouseResponseDTO {
    private Long id;
    private String name;
    private String address;
    private Double maxCapacity;
    private Double currentLoad;
}