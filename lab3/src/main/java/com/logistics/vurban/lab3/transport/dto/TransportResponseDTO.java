package com.logistics.vurban.lab3.transport.dto;

import lombok.Data;

@Data
public class TransportResponseDTO {
    private Long id;
    private String vehicleType;
    private String licensePlate;
    private Double loadCapacity;
    private Double currentLoad;
    private Boolean isAvailable;
}