package com.logistics.vurban.shipmentservice.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EfficiencyReportDTO {
    private Long totalShipmentsProcessed;
    private Long deliveredCount;
    private Long inTransitCount;
    private Long cancelledCount;
    private Double deliverySuccessRate;
    private Double averageDeliveryTimeHours;
}