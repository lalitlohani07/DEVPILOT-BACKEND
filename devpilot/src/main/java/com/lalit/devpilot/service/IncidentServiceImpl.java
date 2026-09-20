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
import com.lalit.devpilot.model.Status;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.lalit.devpilot.exception.IncidentNotFoundException;
import com.lalit.devpilot.model.EventType;
@Service 
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

/*
assignIncident(incidentId, assignee, actor)
        ↓
   find incident
        ↓
   check it exists
        ↓
   change assignee
        ↓
   save updated incident
        ↓
   create ASSIGNED event
        ↓
   save event
        ↓
   return incident 
   */
  @Override 
public Incident assignIncident(int incidentId, User assignee, User actor){
      Incident incident = incidentRepository.findById(incidentId)
        .orElseThrow(() ->
                new IncidentNotFoundException(
                        "Incident not found: " + incidentId));
if (assignee == null) {
            throw new IllegalArgumentException("Assignee cannot be null");
}
         if (assignee.equals(incident.getAssignedTo())) {
             throw new IllegalArgumentException("Incident is already assigned to this user");
             }

            incident.setAssignedTo(assignee);
            incidentRepository.update(incident);
            IncidentEvent event = new IncidentEvent(
            incident,
            EventType.ASSIGNED,
            "Incident assigned to " + assignee.getName(),
            actor
            );

    incidentEventRepository.save(event);
    return incident;
}
/*
changeStatus(incidentId, newStatus, actor, note)
                │
                ▼
       1. Find the Incident
                │
                ├── Not found → IncidentNotFoundException
                │
                ▼
       2. Get current status
                │
                ▼
       3. Check the transition
          using allowedTransitions Map
                │
                ├── Not allowed → throw exception
                │
                ▼
       4. Change the status
          incident.setStatus(newStatus)
                │
                ▼
       5. Save updated Incident
          incidentRepository.update(incident)
                │
                ▼
       6. Create IncidentEvent
          EventType.STATUS_CHANGED
                │
                │ createdBy = actor
                │ message = note
                ▼
       7. Save the event
          incidentEventRepository.save(event)
                │
                ▼
       8. Return updated Incident
*/
 private static final Map<Status, Set<Status>> ALLOWED_TRANSITIONS = Map.of(

            Status.OPEN, Set.of(
                    Status.INVESTIGATING,
                    Status.RESOLVED
            ),

            Status.INVESTIGATING, Set.of(
                    Status.IDENTIFIED,
                    Status.RESOLVED
            ),

            Status.IDENTIFIED, Set.of(
                    Status.MONITORING,
                    Status.INVESTIGATING
            ),

            Status.MONITORING, Set.of(
                    Status.INVESTIGATING,
                    Status.RESOLVED
            ),

            Status.RESOLVED, Set.of(
                    Status.INVESTIGATING,
                    Status.CLOSED
            ),

            Status.CLOSED, Set.of(
                    Status.INVESTIGATING
            )
    );


    @Override
    public Incident changeStatus(
            int incidentId,
            Status newStatus,
            User actor,
            String note) {

        // 1. Validate note
        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException(
                    "Status change note cannot be blank");
        }

        // 2. Validate new status
        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "New status cannot be null");
        }

        // 3. Find incident
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new IncidentNotFoundException(
                                "Incident not found: " + incidentId));

        // 4. Get current status
        Status currentStatus = incident.getStatus();

        // 5. Find allowed next statuses
        Set<Status> allowedTransitions =
                ALLOWED_TRANSITIONS.get(currentStatus);

        // 6. Check whether requested transition is allowed
        if (allowedTransitions == null ||
                !allowedTransitions.contains(newStatus)) {

            throw new IllegalArgumentException(
                    "Invalid status transition: "
                            + currentStatus
                            + " -> "
                            + newStatus);
        }

        // 7. Change the status
        incident.setStatus(newStatus);

        // 8. Save updated incident
        incidentRepository.update(incident);

        // 9. Create timeline event
        IncidentEvent event = new IncidentEvent(
                incident,
                EventType.STATUS_CHANGED,
                note,
                actor
        );

        // 10. Save event
        incidentEventRepository.save(event);

        // 11. Return updated incident
        return incident;
    }
    @Override
public Optional<Incident> getIncident(int incidentId) {
    return incidentRepository.findById(incidentId);
}

@Override
public List<Incident> getAllIncidents() {
    return incidentRepository.findAll();
}

@Override
public List<IncidentEvent> getIncidentTimeline(int incidentId) {
    return incidentEventRepository.findByIncidentId(incidentId);
}
    }
