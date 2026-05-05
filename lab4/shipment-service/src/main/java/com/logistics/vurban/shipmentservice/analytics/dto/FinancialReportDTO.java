package com.logistics.vurban.shipmentservice.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class FinancialReportDTO {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long totalShipments;
    private Double totalRevenue;
    private Double averageRevenuePerShipment;
}