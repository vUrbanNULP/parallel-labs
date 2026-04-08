package com.logistics.vurban.lab3.shipment.repository;

import com.logistics.vurban.lab3.shipment.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Page<Shipment> findBySenderIdOrReceiverId(Long senderId, Long receiverId, Pageable pageable);

    List<Shipment> findByAssignedTripId(Long tripId);

    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}