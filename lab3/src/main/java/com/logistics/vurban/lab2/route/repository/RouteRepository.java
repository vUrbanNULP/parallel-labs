package com.logistics.vurban.lab2.route.repository;

import com.logistics.vurban.lab2.route.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findFirstByOriginIdAndDestinationIdOrderByDistanceKmAsc(Long originId, Long destinationId);
}