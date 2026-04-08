package com.logistics.vurban.lab3.shipment.model;

import com.logistics.vurban.lab3.client.model.Client;
import com.logistics.vurban.lab3.common.model.Status;
import com.logistics.vurban.lab3.trip.model.Trip;
import com.logistics.vurban.lab3.warehouse.model.Warehouse;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_warehouse_id", nullable = false)
    private Warehouse originWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id", nullable = false)
    private Warehouse destinationWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_warehouse_id")
    private Warehouse currentWarehouse;

    @Column(nullable = false)
    private Double cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip assignedTrip;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime dispatchedAt;

    @Column
    private LocalDateTime deliveredAt;
}