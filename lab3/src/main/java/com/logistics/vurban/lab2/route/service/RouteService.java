package com.logistics.vurban.lab2.route.service;

import com.logistics.vurban.lab2.common.exception.ResourceNotFoundException;
import com.logistics.vurban.lab2.common.model.Status;
import com.logistics.vurban.lab2.route.dto.RouteRequestDTO;
import com.logistics.vurban.lab2.route.dto.RouteResponseDTO;
import com.logistics.vurban.lab2.route.model.Route;
import com.logistics.vurban.lab2.route.repository.RouteRepository;
import com.logistics.vurban.lab2.shipment.model.Shipment;
import com.logistics.vurban.lab2.shipment.repository.ShipmentRepository;
import com.logistics.vurban.lab2.transport.dto.TransportResponseDTO;
import com.logistics.vurban.lab2.transport.model.Transport;
import com.logistics.vurban.lab2.transport.repository.TransportRepository;
import com.logistics.vurban.lab2.warehouse.dto.WarehouseResponseDTO;
import com.logistics.vurban.lab2.warehouse.model.Warehouse;
import com.logistics.vurban.lab2.warehouse.repository.WarehouseRepository;
import com.logistics.vurban.lab2.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RouteService {
    private final RouteRepository routeRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseService warehouseService;
    private final TransportRepository transportRepository;
    private final ShipmentRepository shipmentRepository;


    @Transactional
    public RouteResponseDTO createRoute(RouteRequestDTO request) {
        Warehouse origin = warehouseRepository.findById(request.getOriginWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin Warehouse not found"));

        Warehouse destination = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination Warehouse not found"));

        if (origin.getId().equals(destination.getId())) {
            throw new IllegalArgumentException("Origin and Destination cannot be the same warehouse");
        }

        Transport transport = transportRepository.findById(request.getTransportId())
                .orElseThrow(() -> new ResourceNotFoundException("Transport not found"));

        if (!transport.getIsAvailable()) {
            throw new IllegalArgumentException("Transport is currently on another route and unavailable");
        }

        Route route = new Route();
        route.setOrigin(origin);
        route.setDestination(destination);
        route.setAssignedTransport(transport);
        route.setDistanceKm(request.getDistanceKm());
        route.setEstimatedDurationHours(request.getEstimatedDurationHours());
        route.setIsCompleted(false);

        return mapToResponse(routeRepository.save(route));
    }

    public Page<RouteResponseDTO> getAllRoutes(Pageable pageable) {
        return routeRepository.findAll(pageable).map(this::mapToResponse);
    }

    public RouteResponseDTO getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        return mapToResponse(route);
    }

    private RouteResponseDTO mapToResponse(Route route) {
        RouteResponseDTO response = new RouteResponseDTO();
        response.setId(route.getId());
        response.setDistanceKm(route.getDistanceKm());
        response.setEstimatedDurationHours(route.getEstimatedDurationHours());

        WarehouseResponseDTO originDto = new WarehouseResponseDTO();
        originDto.setId(route.getOrigin().getId());
        originDto.setName(route.getOrigin().getName());
        originDto.setAddress(route.getOrigin().getAddress());
        originDto.setMaxCapacity(route.getOrigin().getMaxCapacity());
        originDto.setCurrentLoad(route.getOrigin().getCurrentLoad());
        response.setOrigin(originDto);

        WarehouseResponseDTO destDto = new WarehouseResponseDTO();
        destDto.setId(route.getDestination().getId());
        destDto.setName(route.getDestination().getName());
        destDto.setAddress(route.getDestination().getAddress());
        destDto.setMaxCapacity(route.getDestination().getMaxCapacity());
        destDto.setCurrentLoad(route.getDestination().getCurrentLoad());
        response.setDestination(destDto);

        TransportResponseDTO transDto = new TransportResponseDTO();
        transDto.setId(route.getAssignedTransport().getId());
        transDto.setVehicleType(route.getAssignedTransport().getVehicleType());
        transDto.setLicensePlate(route.getAssignedTransport().getLicensePlate());
        transDto.setLoadCapacity(route.getAssignedTransport().getLoadCapacity());
        transDto.setCurrentLoad(route.getAssignedTransport().getCurrentLoad());
        transDto.setIsAvailable(route.getAssignedTransport().getIsAvailable());
        response.setAssignedTransport(transDto);

        List<Shipment> shipments = shipmentRepository.findByAssignedRouteId(route.getId());
        List<Long> shipmentIds = shipments.stream().map(Shipment::getId).collect(Collectors.toList());
        response.setShipmentIds(shipmentIds);
        response.setIsCompleted(route.getIsCompleted());

        return response;
    }

    // Тут не змінюються склади та транспорт - це існуючий маршрут, куда там.
    // Як і у випадку з Shipment цей метод є чисто для підправлення. Для якихось махінацій з маршрутами будуть інші методи
    public RouteResponseDTO updateRoute(Long id, RouteRequestDTO request) {
        Route existing = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        existing.setDistanceKm(request.getDistanceKm());
        existing.setEstimatedDurationHours(request.getEstimatedDurationHours());

        return mapToResponse(routeRepository.save(existing));
    }

    @Transactional
    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + id));

        if (!route.getIsCompleted()) {

            Transport transport = route.getAssignedTransport();
            transport.setIsAvailable(true);
            transport.setCurrentLoad(0.0);
            transportRepository.save(transport);

            List<Shipment> shipments = shipmentRepository.findByAssignedRouteId(id);
            for (Shipment shipment : shipments) {
                warehouseService.addLoad(route.getOrigin().getId(), shipment.getWeight());

                shipment.setCurrentWarehouse(route.getOrigin());
                shipment.setStatus(Status.IN_TRANSIT);
                shipment.setAssignedRoute(null);
                shipmentRepository.save(shipment);
            }
        }

        routeRepository.deleteById(id);
    }

    @Transactional
    public void completeRoute(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + routeId));

        if (route.getIsCompleted()) {
            throw new IllegalArgumentException("Route is already completed");
        }

        Transport transport = route.getAssignedTransport();
        Warehouse destination = route.getDestination();

        List<Shipment> routeShipments = shipmentRepository.findByAssignedRouteId(routeId);

        for (Shipment shipment : routeShipments) {
            if (shipment.getStatus() == Status.IN_TRANSIT) {
                warehouseService.addLoad(destination.getId(), shipment.getWeight());

                shipment.setCurrentWarehouse(destination);
                shipment.setAssignedRoute(null);

                if (destination.getId().equals(shipment.getDestinationWarehouse().getId())) {
                    shipment.setStatus(Status.READY_FOR_PICKUP);
                } else {
                    shipment.setStatus(Status.TRANSIT_HUB);
                }

                shipmentRepository.save(shipment);
            }
        }

        transport.setIsAvailable(true);
        transport.setCurrentLoad(0.0);
        transportRepository.save(transport);

        route.setIsCompleted(true);
        routeRepository.save(route);
    }

    @Transactional
    public RouteResponseDTO loadShipments(Long routeId, List<Long> shipmentIds) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + routeId));

        if (route.getIsCompleted()) throw new IllegalArgumentException("Cannot load shipments onto a completed route");

        Transport transport = route.getAssignedTransport();
        double currentWeight = transport.getCurrentLoad();

        for (Long sId : shipmentIds) {
            Shipment shipment = shipmentRepository.findById(sId)
                    .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + sId));

            if (shipment.getCurrentWarehouse() == null ||
                    !shipment.getCurrentWarehouse().getId().equals(route.getOrigin().getId())) {
                throw new IllegalArgumentException("Shipment " + sId + " is not at the origin warehouse");
            }

            boolean isDirectRoute = route.getDestination().getId().equals(shipment.getDestinationWarehouse().getId());

            boolean hasForwardConnection = routeRepository
                    .findFirstByOriginIdAndDestinationIdOrderByDistanceKmAsc(
                            route.getDestination().getId(),
                            shipment.getDestinationWarehouse().getId()
                    ).isPresent();

            if (!isDirectRoute && !hasForwardConnection) {
                throw new IllegalArgumentException(String.format(
                        "Wrong direction! Shipment %d destination is '%s', but this route goes to '%s' with no further connection.",
                        sId, shipment.getDestinationWarehouse().getName(), route.getDestination().getName()
                ));
            }

            if (currentWeight + shipment.getWeight() > transport.getLoadCapacity()) {
                throw new IllegalArgumentException("Transport capacity exceeded");
            }

            warehouseService.removeLoad(route.getOrigin().getId(), shipment.getWeight());
            shipment.setCurrentWarehouse(null);
            shipment.setStatus(Status.IN_TRANSIT);
            shipment.setAssignedRoute(route);

            currentWeight += shipment.getWeight();
            shipmentRepository.save(shipment);
        }

        transport.setCurrentLoad(currentWeight);
        transport.setIsAvailable(false);
        transportRepository.save(transport);

        return mapToResponse(route);
    }
}