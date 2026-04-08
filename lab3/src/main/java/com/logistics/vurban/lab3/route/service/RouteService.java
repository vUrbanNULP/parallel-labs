package com.logistics.vurban.lab3.route.service;

import com.logistics.vurban.lab3.common.exception.ResourceNotFoundException;
import com.logistics.vurban.lab3.route.dto.RouteRequestDTO;
import com.logistics.vurban.lab3.route.dto.RouteResponseDTO;
import com.logistics.vurban.lab3.route.model.Route;
import com.logistics.vurban.lab3.route.repository.RouteRepository;
import com.logistics.vurban.lab3.trip.repository.TripRepository;
import com.logistics.vurban.lab3.warehouse.dto.WarehouseResponseDTO;
import com.logistics.vurban.lab3.warehouse.model.Warehouse;
import com.logistics.vurban.lab3.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RouteService {
    private final RouteRepository routeRepository;
    private final WarehouseRepository warehouseRepository;
    private final TripRepository tripRepository;


    @Transactional
    public RouteResponseDTO createRoute(RouteRequestDTO request) {
        Warehouse origin = warehouseRepository.findById(request.getOriginWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Origin Warehouse not found"));

        Warehouse destination = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination Warehouse not found"));

        if (origin.getId().equals(destination.getId())) {
            throw new IllegalArgumentException("Origin and Destination cannot be the same warehouse");
        }

        Route route = new Route();
        route.setOrigin(origin);
        route.setDestination(destination);
        route.setDistanceKm(request.getDistanceKm());
        route.setEstimatedDurationHours(request.getEstimatedDurationHours());

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
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Route not found: " + id);
        }

        if (tripRepository.existsByRouteId(id)) {
            throw new IllegalArgumentException("Cannot delete Route ID " + id + ". It is being used by one or more physical Trips.");
        }

        routeRepository.deleteById(id);
    }
}