package com.logistics.vurban.trackingservice.tracking.repository;

import com.logistics.vurban.trackingservice.tracking.model.TrackingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingLogRepository extends JpaRepository<TrackingLog, Long> {
    List<TrackingLog> findByTrackingNumberOrderByTimestampAsc(String trackingNumber);

    void deleteByShipmentId(Long shipmentId);
}