package com.logistics.vurban.lab3.shipment.repository;

import com.logistics.vurban.lab3.common.model.Status;
import com.logistics.vurban.lab3.shipment.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Page<Shipment> findBySenderIdOrReceiverId(Long senderId, Long receiverId, Pageable pageable);

    List<Shipment> findByAssignedTripId(Long tripId);

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.createdAt BETWEEN :start AND :end")
    Long countShipmentsInPeriod(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(s.cost) FROM Shipment s WHERE s.createdAt BETWEEN :start AND :end")
    Double sumRevenueInPeriod(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.status = :status AND s.createdAt BETWEEN :start AND :end")
    Long countByStatusInPeriod(Status status, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (delivered_at - created_at)) / 3600) " +
            "FROM shipments WHERE status = 'DELIVERED' AND created_at BETWEEN :start AND :end",
            nativeQuery = true)
    Double getAverageDeliveryTimeInHours(LocalDateTime start, LocalDateTime end);
}