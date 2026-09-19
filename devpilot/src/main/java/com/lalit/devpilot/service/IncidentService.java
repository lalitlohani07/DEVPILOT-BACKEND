package com.lalit.devpilot.service;
import com.lalit.devpilot.model.Severity;
import java.util.List;
import java.util.Optional;
import com.lalit.devpilot.model.Status;
import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.IncidentEvent;
import com.lalit.devpilot.model.User;

public interface IncidentService {

    Incident reportIncident(String title, String description, Severity severity, User reportedBy);

    Incident assignIncident(int incidentId, User assignee, User actor);

    Incident changeStatus(int incidentId, Status newStatus, User actor, String note);

    Optional<Incident> getIncident(int incidentId);

    List<Incident> getAllIncidents();

    List<IncidentEvent> getIncidentTimeline(int incidentId);
} 
