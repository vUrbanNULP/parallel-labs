package com.logistics.vurban.lab2.route.service;

import com.logistics.vurban.lab2.common.exception.ResourceNotFoundException;
import com.logistics.vurban.lab2.route.dto.RouteRequestDTO;
import com.logistics.vurban.lab2.route.dto.RouteResponseDTO;
import com.logistics.vurban.lab2.route.model.Route;
import com.logistics.vurban.lab2.route.repository.RouteRepository;
import com.logistics.vurban.lab2.transport.dto.TransportResponseDTO;
import com.logistics.vurban.lab2.transport.model.Transport;
import com.logistics.vurban.lab2.transport.repository.TransportRepository;
import com.logistics.vurban.lab2.warehouse.dto.WarehouseResponseDTO;
import com.logistics.vurban.lab2.warehouse.model.Warehouse;
import com.logistics.vurban.lab2.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
    private final WarehouseRepository warehouseRepository;
    private final TransportRepository transportRepository;

    public RouteResponseDTO createRoute(RouteRequestDTO request) {
        Warehouse origin = warehouseRepository.findById(request.getOriginWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin Warehouse not found: " + request.getOriginWarehouseId()));

        Warehouse destination = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination Warehouse not found: " + request.getDestinationWarehouseId()));

        if (origin.getId().equals(destination.getId())) {
            throw new IllegalArgumentException("Origin and Destination cannot be the same warehouse");
        }

        Transport transport = transportRepository.findById(request.getTransportId())
                .orElseThrow(() -> new ResourceNotFoundException("Transport not found: " + request.getTransportId()));

        Route route = new Route();
        route.setOrigin(origin);
        route.setDestination(destination);
        route.setAssignedTransport(transport);
        route.setDistanceKm(request.getDistanceKm());
        route.setEstimatedDurationHours(request.getEstimatedDurationHours());

        Route savedRoute = routeRepository.save(route);

        return mapToResponse(savedRoute);
    }

    public List<RouteResponseDTO> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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

        return response;
    }
}