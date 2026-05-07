package com.logistics.vurban.routesservice.warehouse.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class WarehouseResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String address;
    private Double maxCapacity;
    private Double currentLoad;
}