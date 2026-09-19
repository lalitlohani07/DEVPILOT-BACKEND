/* reportIncident()
       │
       ├── Validate input
       │
       ├── Create Incident
       │
       ├── incidentRepository.save()
       │          │
       │          └── generates ID
       │
       ├── Create IncidentEvent
       │       EventType.CREATED
       │       "Incident reported"
       │
       ├── incidentEventRepository.save()
       │
       └── return saved Incident */

package com.lalit.devpilot.service;

import com.lalit.devpilot.repository.IncidentRepository;
import com.lalit.devpilot.repository.IncidentEventRepository;
import com.lalit.devpilot.model.Severity;
import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.IncidentEvent;
import com.lalit.devpilot.model.User;
import com.lalit.devpilot.model.EventType;

public class IncidentServiceImpl implements IncidentService {
     private final IncidentRepository incidentRepository;
     private final IncidentEventRepository incidentEventRepository;

     public IncidentServiceImpl(
               IncidentRepository incidentRepository,
               IncidentEventRepository incidentEventRepository) {

          this.incidentRepository = incidentRepository;
          this.incidentEventRepository = incidentEventRepository;
     }
     @Override
public Incident reportIncident(
        String title,
        String description,
        Severity severity,
        User reportedBy) {

    if (title == null || title.isBlank()) {
        throw new IllegalArgumentException("Title cannot be blank");
    }

    if (severity == null) {
        throw new IllegalArgumentException("Severity is required");
    }

    if (reportedBy == null) {
        throw new IllegalArgumentException("Reporter is required");
    }

    Incident incident =
            new Incident(title, description, severity, reportedBy);

    Incident saved = incidentRepository.save(incident);
// Known limitation: no transaction wrapping these two saves yet.
// If this next line fails, "saved" exists in DB with no CREATED event.
// TODO: wrap in @Transactional once Spring transaction management is learned.

    IncidentEvent event = new IncidentEvent(
            saved,
            EventType.CREATED,
            "Incident reported",
            reportedBy);

    incidentEventRepository.save(event);

    return saved;
}
Incident assignIncident(int incidentId, User assignee, User actor){
          
}
}
