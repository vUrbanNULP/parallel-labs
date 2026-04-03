package com.logistics.vurban.lab2.transport.repository;

import com.logistics.vurban.lab2.transport.model.Transport;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TransportRepository {
    private final Map<Long, Transport> transports = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Transport save(Transport transport) {
        if (transport.getId() == null) {
            transport.setId(idGenerator.getAndIncrement());
            transport.setCurrentLoad(0.0);
            transport.setIsAvailable(true); // Новий транспорт за замовчуванням доступний
        }
        transports.put(transport.getId(), transport);
        return transport;
    }

    public List<Transport> findAll() {
        return new ArrayList<>(transports.values());
    }

    public Optional<Transport> findById(Long id) {
        return Optional.ofNullable(transports.get(id));
    }
}