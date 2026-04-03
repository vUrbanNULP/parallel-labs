package com.logistics.vurban.lab2.shipment.service;

import com.logistics.vurban.lab2.client.dto.ClientResponseDTO;
import com.logistics.vurban.lab2.client.model.Client;
import com.logistics.vurban.lab2.client.repository.ClientRepository;
import com.logistics.vurban.lab2.common.exception.ResourceNotFoundException;
import com.logistics.vurban.lab2.common.model.Status;
import com.logistics.vurban.lab2.shipment.dto.ShipmentRequestDTO;
import com.logistics.vurban.lab2.shipment.dto.ShipmentResponseDTO;
import com.logistics.vurban.lab2.shipment.model.Shipment;
import com.logistics.vurban.lab2.shipment.repository.ShipmentRepository;
import com.logistics.vurban.lab2.warehouse.dto.WarehouseResponseDTO;
import com.logistics.vurban.lab2.warehouse.model.Warehouse;
import com.logistics.vurban.lab2.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final ClientRepository clientRepository;
    private final WarehouseRepository warehouseRepository;

    public ShipmentResponseDTO createShipment(ShipmentRequestDTO request) {
        Client sender = clientRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found: " + request.getSenderId()));

        Client receiver = clientRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found: " + request.getReceiverId()));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Sender and Receiver cannot be the same person");
        }

        Warehouse origin = warehouseRepository.findById(request.getOriginWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin Warehouse not found: " + request.getOriginWarehouseId()));

        Warehouse destination = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination Warehouse not found: " + request.getDestinationWarehouseId()));

        if (origin.getId().equals(destination.getId())) {
            throw new IllegalArgumentException("Origin and Destination warehouse cannot be the same");
        }

        Shipment shipment = new Shipment();
        shipment.setDescription(request.getDescription());
        shipment.setWeight(request.getWeight());
        shipment.setSender(sender);
        shipment.setReceiver(receiver);
        shipment.setOriginWarehouse(origin);
        shipment.setDestinationWarehouse(destination);

        shipment.setCurrentWarehouse(origin);
        shipment.setStatus(Status.CREATED);
        shipment.setTrackingNumber(generateTrackingNumber());

        Shipment savedShipment = shipmentRepository.save(shipment);

        return mapToResponse(savedShipment);
    }

    public List<ShipmentResponseDTO> getAllShipments() {
        return shipmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ShipmentResponseDTO getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        return mapToResponse(shipment);
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ShipmentResponseDTO mapToResponse(Shipment shipment) {
        ShipmentResponseDTO response = new ShipmentResponseDTO();
        response.setId(shipment.getId());
        response.setTrackingNumber(shipment.getTrackingNumber());
        response.setDescription(shipment.getDescription());
        response.setWeight(shipment.getWeight());
        response.setStatus(shipment.getStatus());

        ClientResponseDTO senderDto = new ClientResponseDTO();
        senderDto.setId(shipment.getSender().getId());
        senderDto.setFirstName(shipment.getSender().getFirstName());
        senderDto.setLastName(shipment.getSender().getLastName());
        senderDto.setEmail(shipment.getSender().getEmail());
        senderDto.setPhone(shipment.getSender().getPhone());
        response.setSender(senderDto);

        ClientResponseDTO receiverDto = new ClientResponseDTO();
        receiverDto.setId(shipment.getReceiver().getId());
        receiverDto.setFirstName(shipment.getReceiver().getFirstName());
        receiverDto.setLastName(shipment.getReceiver().getLastName());
        receiverDto.setEmail(shipment.getReceiver().getEmail());
        receiverDto.setPhone(shipment.getReceiver().getPhone());
        response.setReceiver(receiverDto);

        response.setOriginWarehouse(mapWarehouseToDto(shipment.getOriginWarehouse()));

        response.setDestinationWarehouse(mapWarehouseToDto(shipment.getDestinationWarehouse()));

        if (shipment.getCurrentWarehouse() != null) {
            response.setCurrentWarehouse(mapWarehouseToDto(shipment.getCurrentWarehouse()));
        }

        return response;
    }

    private WarehouseResponseDTO mapWarehouseToDto(Warehouse warehouse) {
        WarehouseResponseDTO dto = new WarehouseResponseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setAddress(warehouse.getAddress());
        dto.setMaxCapacity(warehouse.getMaxCapacity());
        dto.setCurrentLoad(warehouse.getCurrentLoad());
        return dto;
    }
}