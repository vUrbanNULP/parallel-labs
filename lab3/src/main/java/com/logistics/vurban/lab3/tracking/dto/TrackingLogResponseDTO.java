package com.logistics.vurban.lab3.tracking.dto;

import com.logistics.vurban.lab3.common.model.Status;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrackingLogResponseDTO {
    private Long id;
    private Status status;
    private LocalDateTime timestamp;
    private String details;
}