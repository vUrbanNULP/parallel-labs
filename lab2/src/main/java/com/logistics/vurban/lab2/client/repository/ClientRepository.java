package com.logistics.vurban.lab2.client.repository;

import com.logistics.vurban.lab2.client.model.Client;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ClientRepository {
    private final Map<Long, Client> clients = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Client save(Client client) {
        if (client.getId() == null) {
            client.setId(idGenerator.getAndIncrement());
        }
        clients.put(client.getId(), client);
        return client;
    }

    public List<Client> findAll() {
        return new ArrayList<>(clients.values());
    }

    public Optional<Client> findById(Long id) {
        return Optional.ofNullable(clients.get(id));
    }
}