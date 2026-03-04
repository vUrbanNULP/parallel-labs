package com.logistics.vurban.lab2.route.controller;

import com.logistics.vurban.lab2.route.dto.RouteRequestDTO;
import com.logistics.vurban.lab2.route.dto.RouteResponseDTO;
import com.logistics.vurban.lab2.route.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public List<RouteResponseDTO> getAll() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public RouteResponseDTO getById(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }
}