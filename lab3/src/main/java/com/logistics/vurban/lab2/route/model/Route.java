package com.logistics.vurban.lab2.route.model;

import com.logistics.vurban.lab2.transport.model.Transport;
import com.logistics.vurban.lab2.warehouse.model.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Route {
    private Long id;
    private Warehouse origin;
    private Warehouse destination;
    private Transport assignedTransport;
    private Double distanceKm;
    private Integer estimatedDurationHours;
}