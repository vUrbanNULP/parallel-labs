package com.logistics.vurban.lab3.transport.service;

import com.logistics.vurban.lab3.common.exception.ResourceNotFoundException;
import com.logistics.vurban.lab3.transport.dto.TransportRequestDTO;
import com.logistics.vurban.lab3.transport.dto.TransportResponseDTO;
import com.logistics.vurban.lab3.transport.model.Transport;
import com.logistics.vurban.lab3.transport.repository.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        existing.setIsAvailable(request.getIsAvailable());

        return mapToResponse(transportRepository.save(existing));
    }

    @Transactional
    public void deleteTransport(Long id) {
        Transport transport = transportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transport not found: " + id));

        if (!transport.getIsAvailable() || transport.getCurrentLoad() > 0) {
            throw new IllegalArgumentException(String.format("Cannot delete transport '%s'. It is currently assigned to an active trip or holds cargo.", transport.getLicensePlate()));
        }

        transportRepository.delete(transport);
    }
}