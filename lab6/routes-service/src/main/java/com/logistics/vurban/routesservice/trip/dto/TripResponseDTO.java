package com.logistics.vurban.routesservice.trip.dto;

import com.logistics.vurban.routesservice.route.dto.RouteResponseDTO;
import com.logistics.vurban.routesservice.transport.dto.TransportResponseDTO;
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