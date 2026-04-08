package com.logistics.vurban.lab3.transport.repository;

import com.logistics.vurban.lab3.transport.model.Transport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {
}