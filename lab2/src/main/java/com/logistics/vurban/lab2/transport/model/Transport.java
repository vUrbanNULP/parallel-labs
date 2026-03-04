package com.logistics.vurban.lab2.transport.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transport {
    private Long id;
    private String vehicleType;
    private String licensePlate;
    private Double loadCapacity;
    private Double currentLoad;
    private Boolean isAvailable;
}