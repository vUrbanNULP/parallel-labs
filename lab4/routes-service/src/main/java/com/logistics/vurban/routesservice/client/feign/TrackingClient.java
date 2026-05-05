package com.logistics.vurban.routesservice.client.feign;

import com.logistics.vurban.routesservice.common.model.Status;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "tracking-service", url = "http://localhost:8083/api")
public interface TrackingClient {
    @PostMapping("/tracking/logs")
    void createLog(
            @RequestParam("shipmentId") Long shipmentId,
            @RequestParam("trackingNumber") String trackingNumber,
            @RequestParam("status") Status status,
            @RequestParam("details") String details);
}