package com.logistics.vurban.lab3.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WarehouseLoadReportDTO {
    private Long warehouseId;
    private String warehouseName;
    private Double maxCapacity;
    private Double currentLoad;
    private Double loadPercentage;
    private Double availableSpace;
}