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
import com.logistics.vurban.lab2.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final ClientRepository clientRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseService warehouseService;

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

        warehouseService.addLoad(origin.getId(), request.getWeight());

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

    public Page<ShipmentResponseDTO> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAll(pageable)
                .map(this::mapToResponse);
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

    // Тут не можна змінити трек-номер, статус, відправника, склади - це вже інша частина бізнес логіки.
    // Цей метод чисто для того шоб підправити адміністратором вагу і опис.
    public ShipmentResponseDTO updateShipment(Long id, ShipmentRequestDTO request) {
        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        existingShipment.setDescription(request.getDescription());
        existingShipment.setWeight(request.getWeight());

        Shipment updatedShipment = shipmentRepository.save(existingShipment);
        return mapToResponse(updatedShipment);
    }

    public void deleteShipment(Long id) {
        if (!shipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shipment not found with id: " + id);
        }
        shipmentRepository.deleteById(id);
    }

    public ShipmentResponseDTO transferShipment(Long shipmentId, Long newWarehouseId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + shipmentId));

        if (shipment.getStatus() == Status.DELIVERED || shipment.getStatus() == Status.CANCELLED) {
            throw new IllegalArgumentException("Cannot transfer shipment with status: " + shipment.getStatus());
        }

        Warehouse newWarehouse = warehouseRepository.findById(newWarehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("New warehouse not found: " + newWarehouseId));

        Warehouse currentWarehouse = shipment.getCurrentWarehouse();

        if (currentWarehouse != null && currentWarehouse.getId().equals(newWarehouseId)) {
            throw new IllegalArgumentException("Shipment is already at warehouse: " + newWarehouse.getName());
        }

        if (currentWarehouse != null) {
            warehouseService.removeLoad(currentWarehouse.getId(), shipment.getWeight());
        }

        warehouseService.addLoad(newWarehouseId, shipment.getWeight());

        shipment.setCurrentWarehouse(newWarehouse);
        shipment.setStatus(Status.AT_WAREHOUSE);

        if (newWarehouse.getId().equals(shipment.getDestinationWarehouse().getId())) {
            // TODO: коли буду робити клієнта тут дописати
        }

        Shipment savedShipment = shipmentRepository.save(shipment);
        return mapToResponse(savedShipment);
    }
}