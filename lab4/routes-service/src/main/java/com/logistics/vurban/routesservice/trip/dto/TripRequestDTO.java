package com.logistics.vurban.routesservice.trip.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TripRequestDTO {
    @NotNull(message = "Route ID is mandatory")
    private Long routeId;

    @NotNull(message = "Transport ID is mandatory")
    private Long transportId;
}