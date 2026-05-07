package com.logistics.vurban.shipmentservice.shipment.service;

import com.logistics.vurban.shipmentservice.client.feign.TrackingClient;
import com.logistics.vurban.shipmentservice.common.model.Status;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingSafeService {

    private final TrackingClient trackingClient;

    @CircuitBreaker(name = "trackingService", fallbackMethod = "fallbackTracking")
    public void recordLog(Long id, String number, Status status, String message) {
        trackingClient.createLog(id, number, status, message);
    }

    @CircuitBreaker(name = "trackingService", fallbackMethod = "fallbackTrackingVoid")
    public void deleteLogs(Long shipmentId) {
        trackingClient.deleteLogsByShipmentId(shipmentId);
    }

    private void fallbackTracking(Long id, String number, Status status, String message, Throwable t) {
        log.error("Tracking Service unavailable. Log skipped for {}. Reason: {}", number, t.getMessage());
    }

    private void fallbackTrackingVoid(Long shipmentId, Throwable t) {
        log.error("Tracking Service unavailable. Could not delete logs for shipment {}.", shipmentId);
    }
}