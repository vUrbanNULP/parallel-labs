package com.logistics.vurban.lab2.transport.controller;

import com.logistics.vurban.lab2.transport.dto.TransportRequestDTO;
import com.logistics.vurban.lab2.transport.dto.TransportResponseDTO;
import com.logistics.vurban.lab2.transport.service.TransportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transports")
@RequiredArgsConstructor
public class TransportController {
    private final TransportService transportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransportResponseDTO create(@Valid @RequestBody TransportRequestDTO request) {
        return transportService.createTransport(request);
    }

    @GetMapping
    public List<TransportResponseDTO> getAll() {
        return transportService.getAllTransports();
    }

    @GetMapping("/{id}")
    public TransportResponseDTO getById(@PathVariable Long id) {
        return transportService.getTransportById(id);
    }
}