package com.logistics.vurban.lab3.analytics.service;

import com.logistics.vurban.lab3.analytics.dto.*;
import com.logistics.vurban.lab3.common.model.Status;
import com.logistics.vurban.lab3.shipment.repository.ShipmentRepository;
import com.logistics.vurban.lab3.warehouse.repository.WarehouseRepository;
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

    private final WarehouseRepository warehouseRepository;
    private final ShipmentRepository shipmentRepository;

    public List<WarehouseLoadReportDTO> getWarehouseLoadReport() {
        return warehouseRepository.findAll().stream().map(w -> {
            double loadPercent = (w.getCurrentLoad() / w.getMaxCapacity()) * 100;
            double available = w.getMaxCapacity() - w.getCurrentLoad();
            return new WarehouseLoadReportDTO(w.getId(), w.getName(), w.getMaxCapacity(), w.getCurrentLoad(), loadPercent, available);
        }).collect(Collectors.toList());
    }

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
                shipmentRepository.countByStatusInPeriod(Status.TRANSIT_HUB, startTime, endTime);
        Long cancelled = shipmentRepository.countByStatusInPeriod(Status.CANCELLED, startTime, endTime);

        double successRate = (total > 0) ? (delivered.doubleValue() / total) * 100 : 0.0;
        Double avgTime = shipmentRepository.getAverageDeliveryTimeInHours(startTime, endTime);
        avgTime = (avgTime != null) ? Math.round(avgTime * 100.0) / 100.0 : 0.0;

        return new EfficiencyReportDTO(total, delivered, transit, cancelled, successRate, avgTime);
    }
}