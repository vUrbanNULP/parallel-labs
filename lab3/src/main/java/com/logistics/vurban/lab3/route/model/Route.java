package com.logistics.vurban.lab3.route.model;

import com.logistics.vurban.lab3.transport.model.Transport;
import com.logistics.vurban.lab3.warehouse.model.Warehouse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_warehouse_id", nullable = false)
    private Warehouse origin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id", nullable = false)
    private Warehouse destination;

    @Column(nullable = false)
    private Double distanceKm;

    @Column(nullable = false)
    private Integer estimatedDurationHours;
}