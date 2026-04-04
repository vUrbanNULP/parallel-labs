package com.logistics.vurban.lab2.transport.repository;

import com.logistics.vurban.lab2.transport.model.Transport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {
}