package com.logistics.vurban.lab2.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}