package com.logistics.vurban.lab2.client.repository;

import com.logistics.vurban.lab2.client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    // JpaRepository вже має в собі методи типу save(), findById(), findAll(), deleteById(), фокуси
}