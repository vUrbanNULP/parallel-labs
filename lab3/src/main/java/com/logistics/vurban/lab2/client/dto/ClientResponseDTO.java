package com.logistics.vurban.lab2.client.dto;

import lombok.Data;

@Data
public class ClientResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}