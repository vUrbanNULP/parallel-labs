package com.logistics.vurban.shipmentservice.client.feign;

import com.logistics.vurban.shipmentservice.common.model.Status;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "tracking-service", url = "http://localhost:8083/api")
public interface TrackingClient {
    @PostMapping("/tracking/logs")
    void createLog(@RequestParam("shipmentId") Long shipmentId, @RequestParam("trackingNumber") String trackingNumber, @RequestParam("status") Status status, @RequestParam("details") String details);

    @DeleteMapping("/tracking/logs/shipment/{shipmentId}")
    void deleteLogsByShipmentId(@PathVariable("shipmentId") Long shipmentId);
}