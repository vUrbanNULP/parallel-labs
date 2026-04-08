package com.logistics.vurban.lab3.tracking.controller;

import com.logistics.vurban.lab3.tracking.dto.TrackingLogResponseDTO;
import com.logistics.vurban.lab3.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
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
}