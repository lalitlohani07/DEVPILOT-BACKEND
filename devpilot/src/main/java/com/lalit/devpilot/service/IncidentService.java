package com.lalit.devpilot.service;
import com.lalit.devpilot.model.Severity;
import java.util.List;
import java.util.Optional;
import com.lalit.devpilot.model.Status;
import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.IncidentEvent;
import com.lalit.devpilot.model.User;

public interface IncidentService {

    Incident reportIncident(String title, String description,
               Severity severity, Integer reportedById);

   Incident assignIncident(int incidentId, int assigneeId, int actorId);
Incident changeStatus(int incidentId, Status newStatus, int actorId, String note);

    Incident getIncident(int incidentId);

    List<Incident> getAllIncidents();

    List<IncidentEvent> getIncidentTimeline(int incidentId);
} 
