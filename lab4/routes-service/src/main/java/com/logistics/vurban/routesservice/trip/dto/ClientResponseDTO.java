package com.logistics.vurban.routesservice.trip.dto;

import lombok.Data;

@Data
public class ClientResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}