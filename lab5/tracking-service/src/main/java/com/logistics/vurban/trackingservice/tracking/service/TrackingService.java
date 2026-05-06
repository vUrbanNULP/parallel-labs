package com.logistics.vurban.trackingservice.tracking.service;

import com.logistics.vurban.trackingservice.common.model.Status;
import com.logistics.vurban.trackingservice.tracking.dto.TrackingLogResponseDTO;
import com.logistics.vurban.trackingservice.tracking.model.TrackingLog;
import com.logistics.vurban.trackingservice.tracking.repository.TrackingLogRepository;
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

    public void recordLog(Long shipmentId, String trackingNumber, Status status, String details) {
        TrackingLog log = new TrackingLog();
        log.setShipmentId(shipmentId);
        log.setTrackingNumber(trackingNumber);
        log.setStatus(status);
        log.setTimestamp(LocalDateTime.now());
        log.setDetails(details);
        trackingLogRepository.save(log);
    }

    public void deleteLogsByShipmentId(Long shipmentId) {
        trackingLogRepository.deleteByShipmentId(shipmentId);
    }

    @Transactional(readOnly = true)
    public List<TrackingLogResponseDTO> getHistoryByTrackingNumber(String trackingNumber) {
        return trackingLogRepository.findByTrackingNumberOrderByTimestampAsc(trackingNumber)
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