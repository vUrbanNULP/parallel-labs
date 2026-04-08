package com.logistics.vurban.lab3.trip.repository;

import com.logistics.vurban.lab3.trip.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    boolean existsByRouteId(Long routeId);
}