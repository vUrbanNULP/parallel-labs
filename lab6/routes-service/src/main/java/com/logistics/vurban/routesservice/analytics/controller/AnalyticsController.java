package com.logistics.vurban.routesservice.analytics.controller;

import com.logistics.vurban.routesservice.analytics.dto.WarehouseLoadReportDTO;
import com.logistics.vurban.routesservice.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/warehouses")
    public List<WarehouseLoadReportDTO> getWarehouseLoad() {
        return analyticsService.getWarehouseLoadReport();
    }
}