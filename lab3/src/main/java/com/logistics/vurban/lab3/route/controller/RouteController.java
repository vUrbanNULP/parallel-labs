package com.logistics.vurban.lab3.route.controller;

import com.logistics.vurban.lab3.route.dto.RouteRequestDTO;
import com.logistics.vurban.lab3.route.dto.RouteResponseDTO;
import com.logistics.vurban.lab3.route.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RouteResponseDTO create(@Valid @RequestBody RouteRequestDTO request) {
        return routeService.createRoute(request);
    }

    @GetMapping
    public Page<RouteResponseDTO> getAll(
            @PageableDefault(sort = "distanceKm", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        return routeService.getAllRoutes(pageable);
    }

    @GetMapping("/{id}")
    public RouteResponseDTO getById(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }

    @PutMapping("/{id}")
    public RouteResponseDTO update(@PathVariable Long id, @Valid @RequestBody RouteRequestDTO request) {
        return routeService.updateRoute(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        routeService.deleteRoute(id);
    }
}