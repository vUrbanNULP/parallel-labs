package com.logistics.vurban.lab2.shipment.repository;

import com.logistics.vurban.lab2.shipment.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Page<Shipment> findBySenderIdOrReceiverId(Long senderId, Long receiverId, Pageable pageable);

    List<Shipment> findByAssignedRouteId(Long routeId);
}