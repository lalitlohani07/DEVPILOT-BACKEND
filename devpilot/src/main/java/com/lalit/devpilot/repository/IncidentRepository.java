package com.lalit.devpilot.repository;

import java.util.List;
import java.util.Optional;

import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.User;

public interface IncidentRepository {
     
    Incident save(Incident incident);

    Optional<Incident> findById(int id);

    List<Incident> findAll();

    boolean update(Incident incident);
    boolean delete(int id);
}
