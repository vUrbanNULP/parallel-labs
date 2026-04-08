package com.logistics.vurban.lab3.tracking.service;

import com.logistics.vurban.lab3.common.exception.ResourceNotFoundException;
import com.logistics.vurban.lab3.shipment.model.Shipment;
import com.logistics.vurban.lab3.shipment.repository.ShipmentRepository;
import com.logistics.vurban.lab3.tracking.dto.TrackingLogResponseDTO;
import com.logistics.vurban.lab3.tracking.model.TrackingLog;
import com.logistics.vurban.lab3.tracking.repository.TrackingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TrackingService {
    private final TrackingLogRepository trackingLogRepository;
    private final ShipmentRepository shipmentRepository;

    public void recordLog(Shipment shipment, String details) {
        TrackingLog log = new TrackingLog();
        log.setShipment(shipment);
        log.setStatus(shipment.getStatus());
        log.setTimestamp(LocalDateTime.now());
        log.setDetails(details);
        trackingLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<TrackingLogResponseDTO> getHistoryByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with tracking number: " + trackingNumber));

        return trackingLogRepository.findByShipmentIdOrderByTimestampAsc(shipment.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TrackingLogResponseDTO mapToResponse(TrackingLog log) {
        TrackingLogResponseDTO dto = new TrackingLogResponseDTO();
        dto.setId(log.getId());
        dto.setStatus(log.getStatus());
        dto.setTimestamp(log.getTimestamp());
        dto.setDetails(log.getDetails());
        return dto;
    }
}