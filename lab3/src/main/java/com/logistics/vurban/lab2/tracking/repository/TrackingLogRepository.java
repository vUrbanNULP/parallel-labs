package com.logistics.vurban.lab2.tracking.repository;

import com.logistics.vurban.lab2.tracking.model.TrackingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingLogRepository extends JpaRepository<TrackingLog, Long> {
    List<TrackingLog> findByShipmentIdOrderByTimestampAsc(Long shipmentId);
}