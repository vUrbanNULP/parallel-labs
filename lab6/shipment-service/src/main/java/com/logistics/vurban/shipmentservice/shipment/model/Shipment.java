package com.logistics.vurban.shipmentservice.shipment.model;

import com.logistics.vurban.shipmentservice.client.model.Client;
import com.logistics.vurban.shipmentservice.common.model.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double weight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Client sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Client receiver;

    @Column(name = "origin_warehouse_id", nullable = false)
    private Long originWarehouseId;

    @Column(name = "destination_warehouse_id", nullable = false)
    private Long destinationWarehouseId;

    @Column(name = "current_warehouse_id")
    private Long currentWarehouseId;

    @Column(name = "trip_id")
    private Long assignedTripId;

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime dispatchedAt;

    @Column
    private LocalDateTime deliveredAt;
}