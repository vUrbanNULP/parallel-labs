package com.logistics.vurban.lab2.warehouse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse {
    private Long id;
    private String name;
    private String address;
    private Double maxCapacity;
    private Double currentLoad;
}