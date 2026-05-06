package com.logistics.vurban.routesservice.analytics.service;

import com.logistics.vurban.routesservice.warehouse.repository.WarehouseRepository;
import com.logistics.vurban.routesservice.analytics.dto.WarehouseLoadReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final WarehouseRepository warehouseRepository;

    public List<WarehouseLoadReportDTO> getWarehouseLoadReport() {
        return warehouseRepository.findAll().stream().map(w -> {
            double loadPercent = (w.getCurrentLoad() / w.getMaxCapacity()) * 100;
            double available = w.getMaxCapacity() - w.getCurrentLoad();
            return new WarehouseLoadReportDTO(w.getId(), w.getName(), w.getMaxCapacity(), w.getCurrentLoad(), loadPercent, available);
        }).collect(Collectors.toList());
    }
}