package com.logistics.vurban.lab2.transport.service;

import com.logistics.vurban.lab2.common.exception.ResourceNotFoundException;
import com.logistics.vurban.lab2.transport.dto.TransportRequestDTO;
import com.logistics.vurban.lab2.transport.dto.TransportResponseDTO;
import com.logistics.vurban.lab2.transport.model.Transport;
import com.logistics.vurban.lab2.transport.repository.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransportService {
    private final TransportRepository transportRepository;

    public TransportResponseDTO createTransport(TransportRequestDTO request) {
        Transport transport = new Transport();
        transport.setVehicleType(request.getVehicleType());
        transport.setLicensePlate(request.getLicensePlate());
        transport.setLoadCapacity(request.getLoadCapacity());

        Transport savedTransport = transportRepository.save(transport);
        return mapToResponse(savedTransport);
    }

    public List<TransportResponseDTO> getAllTransports() {
        return transportRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TransportResponseDTO getTransportById(Long id) {
        Transport transport = transportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transport not found with id: " + id));
        return mapToResponse(transport);
    }

    private TransportResponseDTO mapToResponse(Transport transport) {
        TransportResponseDTO response = new TransportResponseDTO();
        response.setId(transport.getId());
        response.setVehicleType(transport.getVehicleType());
        response.setLicensePlate(transport.getLicensePlate());
        response.setLoadCapacity(transport.getLoadCapacity());
        response.setCurrentLoad(transport.getCurrentLoad());
        response.setIsAvailable(transport.getIsAvailable());
        return response;
    }
}