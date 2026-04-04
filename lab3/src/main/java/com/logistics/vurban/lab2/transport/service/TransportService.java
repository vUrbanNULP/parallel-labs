package com.logistics.vurban.lab2.transport.service;

import com.logistics.vurban.lab2.common.exception.ResourceNotFoundException;
import com.logistics.vurban.lab2.transport.dto.TransportRequestDTO;
import com.logistics.vurban.lab2.transport.dto.TransportResponseDTO;
import com.logistics.vurban.lab2.transport.model.Transport;
import com.logistics.vurban.lab2.transport.repository.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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

    public Page<TransportResponseDTO> getAllTransports(Pageable pageable) {
        return transportRepository.findAll(pageable).map(this::mapToResponse);
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

    public TransportResponseDTO updateTransport(Long id, TransportRequestDTO request) {
        Transport existing = transportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transport not found with id: " + id));

        existing.setVehicleType(request.getVehicleType());
        existing.setLicensePlate(request.getLicensePlate());
        existing.setLoadCapacity(request.getLoadCapacity());

        return mapToResponse(transportRepository.save(existing));
    }

    public void deleteTransport(Long id) {
        if (!transportRepository.existsById(id)) throw new ResourceNotFoundException("Transport not found: " + id);
        transportRepository.deleteById(id);
    }
}