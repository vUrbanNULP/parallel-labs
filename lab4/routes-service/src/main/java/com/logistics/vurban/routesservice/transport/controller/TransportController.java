package com.logistics.vurban.routesservice.transport.controller;

import com.logistics.vurban.routesservice.transport.dto.TransportRequestDTO;
import com.logistics.vurban.routesservice.transport.dto.TransportResponseDTO;
import com.logistics.vurban.routesservice.transport.service.TransportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public Page<TransportResponseDTO> getAll(
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        return transportService.getAllTransports(pageable);
    }

    @GetMapping("/{id}")
    public TransportResponseDTO getById(@PathVariable Long id) {
        return transportService.getTransportById(id);
    }

    @PutMapping("/{id}")
    public TransportResponseDTO update(@PathVariable Long id, @Valid @RequestBody TransportRequestDTO request) {
        return transportService.updateTransport(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        transportService.deleteTransport(id);
    }
}