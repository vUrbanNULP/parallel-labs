package com.logistics.vurban.trackingservice.tracking.controller;

import com.logistics.vurban.trackingservice.common.model.Status;
import com.logistics.vurban.trackingservice.tracking.dto.TrackingLogResponseDTO;
import com.logistics.vurban.trackingservice.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {
    private final TrackingService trackingService;

    @GetMapping("/{trackingNumber}")
    public List<TrackingLogResponseDTO> getHistory(@PathVariable String trackingNumber) {
        return trackingService.getHistoryByTrackingNumber(trackingNumber);
    }

    @PostMapping("/logs")
    @ResponseStatus(HttpStatus.CREATED)
    public void createLog(
            @RequestParam Long shipmentId,
            @RequestParam String trackingNumber,
            @RequestParam Status status,
            @RequestParam String details) {
        trackingService.recordLog(shipmentId, trackingNumber, status, details);
    }

    @DeleteMapping("/logs/shipment/{shipmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLogsByShipmentId(@PathVariable Long shipmentId) {
        trackingService.deleteLogsByShipmentId(shipmentId);
    }
}