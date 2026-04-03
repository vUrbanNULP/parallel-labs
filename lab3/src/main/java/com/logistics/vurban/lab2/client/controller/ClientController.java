package com.logistics.vurban.lab2.client.controller;

import com.logistics.vurban.lab2.client.dto.ClientRequestDTO;
import com.logistics.vurban.lab2.client.dto.ClientResponseDTO;
import com.logistics.vurban.lab2.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponseDTO create(@Valid @RequestBody ClientRequestDTO request) {
        return clientService.createClient(request);
    }

    @GetMapping
    public List<ClientResponseDTO> getAll() {
        return clientService.getAllClients();
    }

    @GetMapping("/{id}")
    public ClientResponseDTO getById(@PathVariable Long id) {
        return clientService.getClientById(id);
    }
}