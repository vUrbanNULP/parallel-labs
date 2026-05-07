package com.logistics.vurban.shipmentservice.shipment.service;

import com.logistics.vurban.shipmentservice.client.dto.ClientResponseDTO;
import com.logistics.vurban.shipmentservice.client.model.Client;
import com.logistics.vurban.shipmentservice.client.repository.ClientRepository;
import com.logistics.vurban.shipmentservice.common.exception.ResourceNotFoundException;
import com.logistics.vurban.shipmentservice.common.model.Status;
import com.logistics.vurban.shipmentservice.shipment.dto.ShipmentRequestDTO;
import com.logistics.vurban.shipmentservice.shipment.dto.ShipmentResponseDTO;
import com.logistics.vurban.shipmentservice.shipment.dto.RouteResponseDTO;
import com.logistics.vurban.shipmentservice.shipment.dto.WarehouseResponseDTO;
import com.logistics.vurban.shipmentservice.shipment.model.Shipment;
import com.logistics.vurban.shipmentservice.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final ClientRepository clientRepository;
    private final RoutesSafeService routesSafeService;
    private final TrackingSafeService trackingSafeService;

    public ShipmentResponseDTO createShipment(ShipmentRequestDTO request) {
        if (request.getWeight() <= 0) {
            throw new IllegalArgumentException("Weight must be greater than zero");
        }

        Client sender = clientRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found: " + request.getSenderId()));

        Client receiver = clientRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found: " + request.getReceiverId()));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Sender and Receiver cannot be the same person");
        }

        if (request.getOriginWarehouseId().equals(request.getDestinationWarehouseId())) {
            throw new IllegalArgumentException("Origin and Destination warehouse cannot be the same");
        }

        WarehouseResponseDTO origin = routesSafeService.getWarehouseById(request.getOriginWarehouseId());
        WarehouseResponseDTO destination = routesSafeService.getWarehouseById(request.getDestinationWarehouseId());
        RouteResponseDTO route = routesSafeService.getRouteForPricing(origin.getId(), destination.getId());

        Double calculatedCost = calculateCost(request.getWeight(), route.getDistanceKm());

        routesSafeService.addLoad(origin.getId(), request.getWeight());

        Shipment shipment = new Shipment();
        shipment.setDescription(request.getDescription());
        shipment.setWeight(request.getWeight());
        shipment.setCost(calculatedCost);
        shipment.setSender(sender);
        shipment.setReceiver(receiver);
        shipment.setOriginWarehouseId(origin.getId());
        shipment.setDestinationWarehouseId(destination.getId());
        shipment.setCurrentWarehouseId(origin.getId());
        shipment.setStatus(Status.CREATED);
        shipment.setTrackingNumber(generateTrackingNumber());
        shipment.setCreatedAt(LocalDateTime.now());

        Shipment savedShipment = shipmentRepository.save(shipment);

        trackingSafeService.recordLog(savedShipment.getId(), savedShipment.getTrackingNumber(), savedShipment.getStatus(),
                "Shipment created at " + origin.getName() + ". Destination: " + destination.getName());

        return mapToResponse(savedShipment);
    }

    public Page<ShipmentResponseDTO> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAll(pageable).map(this::mapToResponse);
    }

    public ShipmentResponseDTO getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));
        return mapToResponse(shipment);
    }

    public Page<ShipmentResponseDTO> getClientShipments(Long clientId, Pageable pageable) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client not found: " + clientId);
        }
        return shipmentRepository.findBySenderIdOrReceiverId(clientId, clientId, pageable)
                .map(this::mapToResponse);
    }

    public ShipmentResponseDTO updateShipment(Long id, ShipmentRequestDTO request) {
        Shipment existing = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        if (existing.getStatus() != Status.CREATED) {
            if (!existing.getWeight().equals(request.getWeight())) {
                throw new IllegalArgumentException("Cannot change weight of a shipment that has already been dispatched.");
            }
        }

        if (existing.getStatus() == Status.CREATED && !existing.getWeight().equals(request.getWeight())) {
            double weightDifference = request.getWeight() - existing.getWeight();
            if (weightDifference > 0) {
                routesSafeService.addLoad(existing.getOriginWarehouseId(), weightDifference);
            } else {
                routesSafeService.removeLoad(existing.getOriginWarehouseId(), Math.abs(weightDifference));
            }
            RouteResponseDTO route = routesSafeService.getRouteForPricing(existing.getOriginWarehouseId(), existing.getDestinationWarehouseId());
            existing.setCost(calculateCost(request.getWeight(), route.getDistanceKm()));
        }

        existing.setDescription(request.getDescription());
        existing.setWeight(request.getWeight());

        Shipment savedShipment = shipmentRepository.save(existing);
        trackingSafeService.recordLog(savedShipment.getId(), savedShipment.getTrackingNumber(), savedShipment.getStatus(), "Shipment details updated by admin.");
        return mapToResponse(savedShipment);
    }

    public void deleteShipment(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        if (shipment.getStatus() == Status.IN_TRANSIT || shipment.getStatus() == Status.DELIVERED) {
            throw new IllegalArgumentException("Cannot cancel shipment that is " + shipment.getStatus());
        }

        if (shipment.getCurrentWarehouseId() != null) {
            routesSafeService.removeLoad(shipment.getCurrentWarehouseId(), shipment.getWeight());
        }

        trackingSafeService.deleteLogs(id);
        shipmentRepository.delete(shipment);
    }

    public ShipmentResponseDTO transferShipment(Long shipmentId, Long newWarehouseId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + shipmentId));

        if (shipment.getStatus() == Status.IN_TRANSIT) {
            throw new IllegalArgumentException("Cannot manually transfer shipment while it is IN_TRANSIT. Use route completion instead.");
        }
        if (shipment.getStatus() == Status.DELIVERED || shipment.getStatus() == Status.CANCELLED) {
            throw new IllegalArgumentException("Cannot transfer shipment with status: " + shipment.getStatus());
        }

        if (shipment.getCurrentWarehouseId() != null && shipment.getCurrentWarehouseId().equals(newWarehouseId)) {
            throw new IllegalArgumentException("Shipment is already at requested warehouse.");
        }

        if (shipment.getCurrentWarehouseId() != null) {
            routesSafeService.removeLoad(shipment.getCurrentWarehouseId(), shipment.getWeight());
        }
        routesSafeService.addLoad(newWarehouseId, shipment.getWeight());

        shipment.setCurrentWarehouseId(newWarehouseId);

        if (newWarehouseId.equals(shipment.getDestinationWarehouseId())) {
            shipment.setStatus(Status.READY_FOR_PICKUP);
        } else {
            shipment.setStatus(Status.TRANSIT_HUB);
        }

        Shipment savedShipment = shipmentRepository.save(shipment);

        WarehouseResponseDTO newWarehouseDto = routesSafeService.getWarehouseById(newWarehouseId);
        trackingSafeService.recordLog(savedShipment.getId(), savedShipment.getTrackingNumber(), savedShipment.getStatus(), "Manual transfer to warehouse: " + newWarehouseDto.getName());

        return mapToResponse(savedShipment);
    }

    public ShipmentResponseDTO deliverShipment(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + shipmentId));

        if (shipment.getStatus() != Status.READY_FOR_PICKUP) {
            throw new IllegalArgumentException("Shipment cannot be delivered. Current status: " + shipment.getStatus());
        }

        routesSafeService.removeLoad(shipment.getCurrentWarehouseId(), shipment.getWeight());

        shipment.setCurrentWarehouseId(null);
        shipment.setStatus(Status.DELIVERED);
        shipment.setDeliveredAt(LocalDateTime.now());

        Shipment savedShipment = shipmentRepository.save(shipment);
        trackingSafeService.recordLog(savedShipment.getId(), savedShipment.getTrackingNumber(), savedShipment.getStatus(), "Shipment delivered to recipient.");
        return mapToResponse(savedShipment);
    }

    public void internalUpdateLogisticsData(Long shipmentId, Long tripId, Long currentWarehouseId, Status status, LocalDateTime dispatchedAt) {
        Shipment shipment = shipmentRepository.findById(shipmentId).orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));
        shipment.setAssignedTripId(tripId);
        shipment.setCurrentWarehouseId(currentWarehouseId);
        shipment.setStatus(status);
        if (dispatchedAt != null && shipment.getDispatchedAt() == null) {
            shipment.setDispatchedAt(dispatchedAt);
        }
        shipmentRepository.save(shipment);
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Double calculateCost(Double weight, Double distanceKm) {
        double baseFee = 50.0;
        double weightRate = 2.5;
        double distanceRate = 1.5;
        return baseFee + (weight * weightRate) + (distanceKm * distanceRate);
    }

    private ShipmentResponseDTO mapToResponse(Shipment shipment) {
        ShipmentResponseDTO response = new ShipmentResponseDTO();
        response.setId(shipment.getId());
        response.setTrackingNumber(shipment.getTrackingNumber());
        response.setDescription(shipment.getDescription());
        response.setWeight(shipment.getWeight());
        response.setCost(shipment.getCost());
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

        try {
            response.setOriginWarehouse(routesSafeService.getWarehouseById(shipment.getOriginWarehouseId()));
            response.setDestinationWarehouse(routesSafeService.getWarehouseById(shipment.getDestinationWarehouseId()));
            if (shipment.getCurrentWarehouseId() != null) {
                response.setCurrentWarehouse(routesSafeService.getWarehouseById(shipment.getCurrentWarehouseId()));
            }
        } catch (Exception e) {
        }

        response.setCreatedAt(shipment.getCreatedAt());
        response.setDispatchedAt(shipment.getDispatchedAt());
        response.setDeliveredAt(shipment.getDeliveredAt());

        return response;
    }

    public java.util.List<ShipmentResponseDTO> getShipmentsByTripId(Long tripId) {
        return shipmentRepository.findByAssignedTripId(tripId)
                .stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }
}