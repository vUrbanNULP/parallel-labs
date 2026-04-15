package com.logistics.vurban.lab3.analytics.controller;

import com.logistics.vurban.lab3.analytics.dto.*;
import com.logistics.vurban.lab3.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/finance")
    public FinancialReportDTO getFinance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return analyticsService.getFinancialReport(start, end);
    }

    @GetMapping("/efficiency")
    public EfficiencyReportDTO getEfficiency(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return analyticsService.getEfficiencyReport(start, end);
    }
}