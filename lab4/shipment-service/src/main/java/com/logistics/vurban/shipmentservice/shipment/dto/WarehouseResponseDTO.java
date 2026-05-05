package com.logistics.vurban.shipmentservice.shipment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseResponseDTO {
    private Long id;
    private String name;
    private String address;
    private Double maxCapacity;
    private Double currentLoad;
}