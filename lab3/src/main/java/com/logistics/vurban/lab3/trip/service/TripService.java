package com.logistics.vurban.lab3.trip.service;

import com.logistics.vurban.lab3.common.exception.ResourceNotFoundException;
import com.logistics.vurban.lab3.common.model.Status;
import com.logistics.vurban.lab3.route.dto.RouteRequestDTO;
import com.logistics.vurban.lab3.route.dto.RouteResponseDTO;
import com.logistics.vurban.lab3.route.model.Route;
import com.logistics.vurban.lab3.route.repository.RouteRepository;
import com.logistics.vurban.lab3.shipment.model.Shipment;
import com.logistics.vurban.lab3.shipment.repository.ShipmentRepository;
import com.logistics.vurban.lab3.tracking.service.TrackingService;
import com.logistics.vurban.lab3.transport.dto.TransportResponseDTO;
import com.logistics.vurban.lab3.transport.model.Transport;
import com.logistics.vurban.lab3.transport.repository.TransportRepository;
import com.logistics.vurban.lab3.trip.dto.TripRequestDTO;
import com.logistics.vurban.lab3.trip.dto.TripResponseDTO;
import com.logistics.vurban.lab3.trip.model.Trip;
import com.logistics.vurban.lab3.trip.repository.TripRepository;
import com.logistics.vurban.lab3.warehouse.dto.WarehouseResponseDTO;
import com.logistics.vurban.lab3.warehouse.model.Warehouse;
import com.logistics.vurban.lab3.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TripService {
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final TransportRepository transportRepository;
    private final ShipmentRepository shipmentRepository;
    private final WarehouseService warehouseService;
    private final TrackingService trackingService;

    @Transactional
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

    @Transactional
    public TripResponseDTO loadShipments(Long tripId, List<Long> shipmentIds) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (trip.getIsCompleted()) throw new IllegalArgumentException("Trip is already completed");

        Transport transport = trip.getAssignedTransport();
        double currentWeight = transport.getCurrentLoad();

        for (Long sId : shipmentIds) {
            Shipment shipment = shipmentRepository.findById(sId)
                    .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

            if (shipment.getCurrentWarehouse() == null ||
                    !shipment.getCurrentWarehouse().getId().equals(trip.getRoute().getOrigin().getId())) {
                throw new IllegalArgumentException("Shipment " + sId + " is not at the origin warehouse");
            }

            if (shipment.getStatus() != Status.CREATED && shipment.getStatus() != Status.TRANSIT_HUB) {
                throw new IllegalArgumentException(
                        String.format("Shipment %d cannot be loaded. Only CREATED or TRANSIT_HUB status is allowed. Current: %s",
                                sId, shipment.getStatus())
                );
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
            shipment.setCurrentWarehouse(null);
            shipment.setStatus(Status.IN_TRANSIT);
            shipment.setAssignedTrip(trip);

            if (shipment.getDispatchedAt() == null) {
                shipment.setDispatchedAt(LocalDateTime.now());
            }

            currentWeight += shipment.getWeight();
            shipmentRepository.save(shipment);
            trackingService.recordLog(shipment, "Loaded onto trip from " + trip.getRoute().getOrigin().getName() + " to " + trip.getRoute().getDestination().getName()
                    + ". Estimated duration: " + trip.getRoute().getEstimatedDurationHours());
        }

        transport.setCurrentLoad(currentWeight);
        transport.setIsAvailable(false);
        transportRepository.save(transport);

        return mapToResponse(trip);
    }

    @Transactional
    public void completeTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (trip.getIsCompleted()) throw new IllegalArgumentException("Trip is already completed");

        Warehouse destination = trip.getRoute().getDestination();
        List<Shipment> tripShipments = shipmentRepository.findByAssignedTripId(tripId);

        for (Shipment shipment : tripShipments) {
            if (shipment.getStatus() == Status.IN_TRANSIT) {
                warehouseService.addLoad(destination.getId(), shipment.getWeight());
                shipment.setCurrentWarehouse(destination);
                shipment.setAssignedTrip(null);

                if (destination.getId().equals(shipment.getDestinationWarehouse().getId())) {
                    shipment.setStatus(Status.READY_FOR_PICKUP);
                    trackingService.recordLog(shipment, "Arrived at destination warehouse: " + destination.getName());
                } else {
                    shipment.setStatus(Status.TRANSIT_HUB);
                    trackingService.recordLog(shipment, "Arrived at transit hub: " + destination.getName());
                }
                shipmentRepository.save(shipment);
            }
        }

        Transport transport = trip.getAssignedTransport();
        transport.setIsAvailable(true);
        transport.setCurrentLoad(0.0);
        transportRepository.save(transport);

        trip.setIsCompleted(true);
        tripRepository.save(trip);
    }

    public Page<TripResponseDTO> getAllTrips(Pageable pageable) {
        return tripRepository.findAll(pageable).map(this::mapToResponse);
    }

    private TripResponseDTO mapToResponse(Trip trip) {
        TripResponseDTO response = new TripResponseDTO();
        response.setId(trip.getId());
        response.setIsCompleted(trip.getIsCompleted());

        TransportResponseDTO transportDto = new TransportResponseDTO();
        transportDto.setId(trip.getAssignedTransport().getId());
        transportDto.setVehicleType(trip.getAssignedTransport().getVehicleType());
        transportDto.setLicensePlate(trip.getAssignedTransport().getLicensePlate());
        transportDto.setLoadCapacity(trip.getAssignedTransport().getLoadCapacity());
        transportDto.setCurrentLoad(trip.getAssignedTransport().getCurrentLoad());
        transportDto.setIsAvailable(trip.getAssignedTransport().getIsAvailable());
        response.setAssignedTransport(transportDto);

        RouteResponseDTO routeDto = new RouteResponseDTO();
        routeDto.setId(trip.getRoute().getId());
        routeDto.setDistanceKm(trip.getRoute().getDistanceKm());
        routeDto.setEstimatedDurationHours(trip.getRoute().getEstimatedDurationHours());

        WarehouseResponseDTO originDto = new WarehouseResponseDTO();
        originDto.setId(trip.getRoute().getOrigin().getId());
        originDto.setName(trip.getRoute().getOrigin().getName());
        originDto.setAddress(trip.getRoute().getOrigin().getAddress());
        originDto.setMaxCapacity(trip.getRoute().getOrigin().getMaxCapacity());
        originDto.setCurrentLoad(trip.getRoute().getOrigin().getCurrentLoad());
        routeDto.setOrigin(originDto);

        WarehouseResponseDTO destDto = new WarehouseResponseDTO();
        destDto.setId(trip.getRoute().getDestination().getId());
        destDto.setName(trip.getRoute().getDestination().getName());
        destDto.setAddress(trip.getRoute().getDestination().getAddress());
        destDto.setMaxCapacity(trip.getRoute().getDestination().getMaxCapacity());
        destDto.setCurrentLoad(trip.getRoute().getDestination().getCurrentLoad());
        routeDto.setDestination(destDto);

        response.setRoute(routeDto);

        List<Long> shipmentIds = shipmentRepository.findByAssignedTripId(trip.getId())
                .stream()
                .map(Shipment::getId)
                .collect(Collectors.toList());
        response.setShipmentIds(shipmentIds);

        return response;
    }

    @Transactional
    public TripResponseDTO updateTrip(Long tripId, TripRequestDTO request) {
        Trip existing = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (existing.getIsCompleted()) {
            throw new IllegalArgumentException("Cannot update a completed trip");
        }

        List<Shipment> assignedShipments = shipmentRepository.findByAssignedTripId(tripId);
        if (!assignedShipments.isEmpty()) {
            throw new IllegalArgumentException("Cannot change transport for a trip that already has loaded shipments. Unload them first or create a new trip.");
        }

        Transport newTransport = transportRepository.findById(request.getTransportId())
                .orElseThrow(() -> new ResourceNotFoundException("Transport not found"));

        if (!newTransport.getIsAvailable() && !existing.getAssignedTransport().getId().equals(newTransport.getId())) {
            throw new IllegalArgumentException("New transport is unavailable");
        }

        existing.setAssignedTransport(newTransport);
        return mapToResponse(tripRepository.save(existing));
    }

    @Transactional
    public void deleteTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (!trip.getIsCompleted()) {
            Transport transport = trip.getAssignedTransport();
            transport.setIsAvailable(true);
            transport.setCurrentLoad(0.0);
            transportRepository.save(transport);

            List<Shipment> shipments = shipmentRepository.findByAssignedTripId(tripId);
            Warehouse tripOrigin = trip.getRoute().getOrigin();

            for (Shipment shipment : shipments) {
                warehouseService.addLoad(tripOrigin.getId(), shipment.getWeight());
                shipment.setCurrentWarehouse(tripOrigin);

                if (tripOrigin.getId().equals(shipment.getOriginWarehouse().getId())) {
                    shipment.setStatus(Status.CREATED);
                } else {
                    shipment.setStatus(Status.TRANSIT_HUB);
                }

                shipment.setAssignedTrip(null);
                shipment.setDispatchedAt(null);

                trackingService.recordLog(shipment, "Trip cancelled. Cargo returned to: " + tripOrigin.getName());
                shipmentRepository.save(shipment);
            }
        }
        tripRepository.deleteById(tripId);
    }
}