package com.logistics.vurban.lab2.shipment.model;

import com.logistics.vurban.lab2.client.model.Client;
import com.logistics.vurban.lab2.common.model.Status;
import com.logistics.vurban.lab2.warehouse.model.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shipment {
    private Long id;
    private String trackingNumber;
    private String description;
    private Double weight;
    private Status status;

    // Зв'язки
    private Client sender;
    private Client receiver;
    private Warehouse originWarehouse;
    private Warehouse destinationWarehouse;
    private Warehouse currentWarehouse;
}