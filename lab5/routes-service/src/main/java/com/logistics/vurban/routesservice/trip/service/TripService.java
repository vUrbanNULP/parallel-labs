package com.logistics.vurban.routesservice.trip.service;

import com.logistics.vurban.routesservice.common.exception.ResourceNotFoundException;
import com.logistics.vurban.routesservice.common.model.Status;
import com.logistics.vurban.routesservice.route.dto.RouteResponseDTO;
import com.logistics.vurban.routesservice.route.model.Route;
import com.logistics.vurban.routesservice.route.repository.RouteRepository;
import com.logistics.vurban.routesservice.transport.dto.TransportResponseDTO;
import com.logistics.vurban.routesservice.transport.model.Transport;
import com.logistics.vurban.routesservice.transport.repository.TransportRepository;
import com.logistics.vurban.routesservice.trip.dto.ShipmentLogisticsUpdateRequest;
import com.logistics.vurban.routesservice.trip.dto.ShipmentResponseDTO;
import com.logistics.vurban.routesservice.trip.dto.TripRequestDTO;
import com.logistics.vurban.routesservice.trip.dto.TripResponseDTO;
import com.logistics.vurban.routesservice.trip.model.Trip;
import com.logistics.vurban.routesservice.trip.repository.TripRepository;
import com.logistics.vurban.routesservice.warehouse.dto.WarehouseResponseDTO;
import com.logistics.vurban.routesservice.warehouse.model.Warehouse;
import com.logistics.vurban.routesservice.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TripService {
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final TransportRepository transportRepository;
    private final WarehouseService warehouseService;
    private final ShipmentSafeService shipmentSafeService;
    private final TrackingSafeService trackingSafeService;

    public TripResponseDTO createTrip(TripRequestDTO request) {
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        Transport transport = transportRepository.findById(request.getTransportId())
                .orElseThrow(() -> new ResourceNotFoundException("Transport not found"));

        if (!transport.getIsAvailable()) {
            throw new IllegalArgumentException("Transport is currently unavailable");
        }
        
        Trip trip = new Trip();
        trip.setRoute(route);
        trip.setAssignedTransport(transport);
        trip.setIsCompleted(false);

        return mapToResponse(tripRepository.save(trip));
    }

    public TripResponseDTO loadShipments(Long tripId, List<Long> shipmentIds) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (trip.getIsCompleted()) throw new IllegalArgumentException("Trip is already completed");

        Transport transport = trip.getAssignedTransport();
        double currentWeight = transport.getCurrentLoad();

        for (Long sId : shipmentIds) {
            ShipmentResponseDTO shipment = shipmentSafeService.getShipmentById(sId);

            if (shipment.getCurrentWarehouse() == null ||
                    !shipment.getCurrentWarehouse().getId().equals(trip.getRoute().getOrigin().getId())) {
                throw new IllegalArgumentException("Shipment " + sId + " is not at the origin warehouse");
            }

            if (shipment.getStatus() != Status.CREATED && shipment.getStatus() != Status.TRANSIT_HUB) {
                throw new IllegalArgumentException("Shipment " + sId + " cannot be loaded. Current status: " + shipment.getStatus());
            }

            boolean isDirect = trip.getRoute().getDestination().getId().equals(shipment.getDestinationWarehouse().getId());
            boolean hasForwardConnection = routeRepository
                    .findFirstByOriginIdAndDestinationIdOrderByDistanceKmAsc(
                            trip.getRoute().getDestination().getId(),
                            shipment.getDestinationWarehouse().getId()
                    ).isPresent();

            if (!isDirect && !hasForwardConnection) {
                throw new IllegalArgumentException("Wrong direction for shipment " + sId);
            }

            if (currentWeight + shipment.getWeight() > transport.getLoadCapacity()) {
                throw new IllegalArgumentException("Transport capacity exceeded");
            }

            warehouseService.removeLoad(trip.getRoute().getOrigin().getId(), shipment.getWeight());
            currentWeight += shipment.getWeight();

            ShipmentLogisticsUpdateRequest updateReq = new ShipmentLogisticsUpdateRequest(sId, trip.getId(), null, Status.IN_TRANSIT, LocalDateTime.now());
            shipmentSafeService.updateShipmentLogisticsData(updateReq);

            trackingSafeService.recordLog(sId, shipment.getTrackingNumber(), Status.IN_TRANSIT,
                    "Loaded onto trip to " + trip.getRoute().getDestination().getName());
        }

        transport.setCurrentLoad(currentWeight);
        transport.setIsAvailable(false);
        transportRepository.save(transport);

        return mapToResponse(trip);
    }

    public void completeTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (trip.getIsCompleted()) throw new IllegalArgumentException("Trip is already completed");

        Warehouse destination = trip.getRoute().getDestination();

        List<ShipmentResponseDTO> tripShipments = shipmentSafeService.getShipmentsByTripId(tripId);

        for (ShipmentResponseDTO shipment : tripShipments) {
            if (shipment.getStatus() == Status.IN_TRANSIT) {
                warehouseService.addLoad(destination.getId(), shipment.getWeight());

                Status newStatus;
                String logDetails;
                if (destination.getId().equals(shipment.getDestinationWarehouse().getId())) {
                    newStatus = Status.READY_FOR_PICKUP;
                    logDetails = "Arrived at destination warehouse: " + destination.getName();
                } else {
                    newStatus = Status.TRANSIT_HUB;
                    logDetails = "Arrived at transit hub: " + destination.getName();
                }

                ShipmentLogisticsUpdateRequest updateReq = new ShipmentLogisticsUpdateRequest(shipment.getId(), null, destination.getId(), newStatus, null);
                shipmentSafeService.updateShipmentLogisticsData(updateReq);

                trackingSafeService.recordLog(shipment.getId(), shipment.getTrackingNumber(), newStatus, logDetails);
            }
        }

        Transport transport = trip.getAssignedTransport();
        transport.setIsAvailable(true);
        transport.setCurrentLoad(0.0);
        transportRepository.save(transport);

        trip.setIsCompleted(true);
        tripRepository.save(trip);
    }

    public TripResponseDTO updateTrip(Long tripId, TripRequestDTO request) {
        Trip existing = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (existing.getIsCompleted()) {
            throw new IllegalArgumentException("Cannot update a completed trip");
        }

        List<ShipmentResponseDTO> assignedShipments = shipmentSafeService.getShipmentsByTripId(tripId);
        if (!assignedShipments.isEmpty()) {
            throw new IllegalArgumentException("Cannot change transport for a trip that already has loaded shipments. Unload them first.");
        }

        Transport newTransport = transportRepository.findById(request.getTransportId())
                .orElseThrow(() -> new ResourceNotFoundException("Transport not found"));

        if (!newTransport.getIsAvailable() && !existing.getAssignedTransport().getId().equals(newTransport.getId())) {
            throw new IllegalArgumentException("New transport is unavailable");
        }

        existing.setAssignedTransport(newTransport);
        return mapToResponse(tripRepository.save(existing));
    }

    public void deleteTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (!trip.getIsCompleted()) {
            Transport transport = trip.getAssignedTransport();
            transport.setIsAvailable(true);
            transport.setCurrentLoad(0.0);
            transportRepository.save(transport);

            List<ShipmentResponseDTO> shipments = shipmentSafeService.getShipmentsByTripId(tripId);
            Warehouse tripOrigin = trip.getRoute().getOrigin();

            for (ShipmentResponseDTO shipment : shipments) {
                warehouseService.addLoad(tripOrigin.getId(), shipment.getWeight());

                Status revertStatus = tripOrigin.getId().equals(shipment.getOriginWarehouse().getId()) ? Status.CREATED : Status.TRANSIT_HUB;

                ShipmentLogisticsUpdateRequest updateReq = new ShipmentLogisticsUpdateRequest(shipment.getId(), null, tripOrigin.getId(), revertStatus, null);
                shipmentSafeService.updateShipmentLogisticsData(updateReq);
                trackingSafeService.recordLog(shipment.getId(), shipment.getTrackingNumber(), revertStatus, "Trip cancelled. Cargo returned to: " + tripOrigin.getName());
            }
        }
        tripRepository.deleteById(tripId);
    }

    public Page<TripResponseDTO> getAllTrips(Pageable pageable) {
        return tripRepository.findAll(pageable).map(this::mapToResponse);
    }

    private TripResponseDTO mapToResponse(Trip trip) {
        TripResponseDTO response = new TripResponseDTO();
        response.setId(trip.getId());
        response.setIsCompleted(trip.getIsCompleted());

        Transport transport = trip.getAssignedTransport();
        TransportResponseDTO transportDto = new TransportResponseDTO();
        transportDto.setId(transport.getId());
        transportDto.setVehicleType(transport.getVehicleType());
        transportDto.setLicensePlate(transport.getLicensePlate());
        transportDto.setLoadCapacity(transport.getLoadCapacity());
        transportDto.setCurrentLoad(transport.getCurrentLoad());
        transportDto.setIsAvailable(transport.getIsAvailable());
        response.setAssignedTransport(transportDto);

        Route route = trip.getRoute();
        RouteResponseDTO routeDto = new RouteResponseDTO();
        routeDto.setId(route.getId());
        routeDto.setDistanceKm(route.getDistanceKm());
        routeDto.setEstimatedDurationHours(route.getEstimatedDurationHours());

        Warehouse origin = route.getOrigin();
        WarehouseResponseDTO originDto = new WarehouseResponseDTO();
        originDto.setId(origin.getId());
        originDto.setName(origin.getName());
        originDto.setAddress(origin.getAddress());
        originDto.setMaxCapacity(origin.getMaxCapacity());
        originDto.setCurrentLoad(origin.getCurrentLoad());
        routeDto.setOrigin(originDto);

        Warehouse destination = route.getDestination();
        WarehouseResponseDTO destDto = new WarehouseResponseDTO();
        destDto.setId(destination.getId());
        destDto.setName(destination.getName());
        destDto.setAddress(destination.getAddress());
        destDto.setMaxCapacity(destination.getMaxCapacity());
        destDto.setCurrentLoad(destination.getCurrentLoad());
        routeDto.setDestination(destDto);

        response.setRoute(routeDto);

        try {
            List<Long> shipmentIds = shipmentSafeService.getShipmentsByTripId(trip.getId())
                    .stream()
                    .map(com.logistics.vurban.routesservice.trip.dto.ShipmentResponseDTO::getId)
                    .collect(Collectors.toList());
            response.setShipmentIds(shipmentIds);
        } catch (Exception e) {
            response.setShipmentIds(java.util.Collections.emptyList());
        }

        return response;
    }
}