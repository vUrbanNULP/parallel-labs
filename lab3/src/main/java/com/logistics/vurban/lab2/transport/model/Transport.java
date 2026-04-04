package com.logistics.vurban.lab2.transport.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private Double loadCapacity;

    @Column(nullable = false)
    private Double currentLoad = 0.0;

    @Column(nullable = false)
    private Boolean isAvailable = true;
}