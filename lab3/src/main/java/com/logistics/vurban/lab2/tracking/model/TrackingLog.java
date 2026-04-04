package com.logistics.vurban.lab2.tracking.model;

import com.logistics.vurban.lab2.common.model.Status;
import com.logistics.vurban.lab2.shipment.model.Shipment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrackingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String details;
}