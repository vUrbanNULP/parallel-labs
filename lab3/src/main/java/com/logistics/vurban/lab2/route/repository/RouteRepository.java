package com.logistics.vurban.lab2.route.repository;

import com.logistics.vurban.lab2.route.model.Route;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RouteRepository {
    private final Map<Long, Route> routes = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Route save(Route route) {
        if (route.getId() == null) {
            route.setId(idGenerator.getAndIncrement());
        }
        routes.put(route.getId(), route);
        return route;
    }

    public List<Route> findAll() {
        return new ArrayList<>(routes.values());
    }

    public Optional<Route> findById(Long id) {
        return Optional.ofNullable(routes.get(id));
    }
}