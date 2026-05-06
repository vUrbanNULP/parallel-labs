package com.logistics.vurban.shipmentservice.analytics.service;

import com.logistics.vurban.shipmentservice.analytics.dto.EfficiencyReportDTO;
import com.logistics.vurban.shipmentservice.analytics.dto.FinancialReportDTO;
import com.logistics.vurban.shipmentservice.common.model.Status;
import com.logistics.vurban.shipmentservice.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final ShipmentRepository shipmentRepository;

    public FinancialReportDTO getFinancialReport(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(23, 59, 59);

        Long total = shipmentRepository.countShipmentsInPeriod(startTime, endTime);
        Double revenue = shipmentRepository.sumRevenueInPeriod(startTime, endTime);
        revenue = (revenue != null) ? revenue : 0.0;
        double average = (total > 0) ? revenue / total : 0.0;

        return new FinancialReportDTO(start, end, total, revenue, average);
    }

    public EfficiencyReportDTO getEfficiencyReport(LocalDate start, LocalDate end) {
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(23, 59, 59);

        Long total = shipmentRepository.countShipmentsInPeriod(startTime, endTime);
        Long delivered = shipmentRepository.countByStatusInPeriod(Status.DELIVERED, startTime, endTime);
        Long transit = shipmentRepository.countByStatusInPeriod(Status.IN_TRANSIT, startTime, endTime) +
                shipmentRepository.countByStatusInPeriod(Status.TRANSIT_HUB, startTime, endTime) +
                shipmentRepository.countByStatusInPeriod(Status.CREATED, startTime, endTime);
        Long cancelled = shipmentRepository.countByStatusInPeriod(Status.CANCELLED, startTime, endTime);

        double successRate = (total > 0) ? (delivered.doubleValue() / total) * 100 : 0.0;
        Double avgTime = shipmentRepository.getAverageDeliveryTimeInHours(startTime, endTime);
        avgTime = (avgTime != null) ? Math.round(avgTime * 100.0) / 100.0 : 0.0;

        return new EfficiencyReportDTO(total, delivered, transit, cancelled, successRate, avgTime);
    }
}