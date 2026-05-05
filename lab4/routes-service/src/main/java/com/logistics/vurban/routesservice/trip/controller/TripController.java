package com.logistics.vurban.routesservice.trip.controller;

import com.logistics.vurban.routesservice.trip.dto.TripRequestDTO;
import com.logistics.vurban.routesservice.trip.dto.TripResponseDTO;
import com.logistics.vurban.routesservice.trip.service.TripService;
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
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponseDTO create(@Valid @RequestBody TripRequestDTO request) {
        return tripService.createTrip(request);
    }

    @GetMapping
    public Page<TripResponseDTO> getAll(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        return tripService.getAllTrips(pageable);
    }

    @PutMapping("/{id}/load")
    public TripResponseDTO loadShipments(
            @PathVariable Long id,
            @RequestBody List<Long> shipmentIds) {
        return tripService.loadShipments(id, shipmentIds);
    }

    @PutMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable Long id) {
        tripService.completeTrip(id);
    }

    @PutMapping("/{id}")
    public TripResponseDTO update(@PathVariable Long id, @Valid @RequestBody TripRequestDTO request) {
        return tripService.updateTrip(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        tripService.deleteTrip(id);
    }
}