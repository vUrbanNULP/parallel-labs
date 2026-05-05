package com.logistics.vurban.shipmentservice.analytics.controller;

import com.logistics.vurban.shipmentservice.analytics.dto.EfficiencyReportDTO;
import com.logistics.vurban.shipmentservice.analytics.dto.FinancialReportDTO;
import com.logistics.vurban.shipmentservice.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

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