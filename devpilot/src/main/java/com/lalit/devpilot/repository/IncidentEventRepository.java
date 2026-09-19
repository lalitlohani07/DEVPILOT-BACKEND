package com.lalit.devpilot.repository;
import com.lalit.devpilot.model.IncidentEvent;
import java.util.List;
import java.util.Optional;

public interface IncidentEventRepository {
    IncidentEvent save(IncidentEvent event);
    Optional<IncidentEvent> findById(int id);
    List<IncidentEvent> findByIncidentId(int incidentId);
}
