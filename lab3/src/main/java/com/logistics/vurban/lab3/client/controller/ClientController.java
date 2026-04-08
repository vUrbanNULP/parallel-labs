package com.logistics.vurban.lab3.client.controller;

import com.logistics.vurban.lab3.client.dto.ClientRequestDTO;
import com.logistics.vurban.lab3.client.dto.ClientResponseDTO;
import com.logistics.vurban.lab3.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public Page<ClientResponseDTO> getAll(
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        return clientService.getAllClients(pageable);
    }

    @GetMapping("/{id}")
    public ClientResponseDTO getById(@PathVariable Long id) {
        return clientService.getClientById(id);
    }

    @PutMapping("/{id}")
    public ClientResponseDTO update(@PathVariable Long id, @Valid @RequestBody ClientRequestDTO request) {
        return clientService.updateClient(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clientService.deleteClient(id);
    }
}