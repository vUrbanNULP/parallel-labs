package com.logistics.vurban.routesservice.trip.dto;

import com.logistics.vurban.routesservice.common.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentLogisticsUpdateRequest {
    private Long shipmentId;
    private Long assignedTripId;
    private Long currentWarehouseId;
    private Status status;
    private LocalDateTime dispatchedAt;
}