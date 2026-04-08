package com.logistics.vurban.lab3.trip.dto;

import com.logistics.vurban.lab3.route.dto.RouteResponseDTO;
import com.logistics.vurban.lab3.transport.dto.TransportResponseDTO;
import lombok.Data;
import java.util.List;

@Data
public class TripResponseDTO {
    private Long id;
    private RouteResponseDTO route;
    private TransportResponseDTO assignedTransport;
    private Boolean isCompleted;
    private List<Long> shipmentIds;
}