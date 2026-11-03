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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lalit.devpilot.exception.IncidentNotFoundException;
import com.lalit.devpilot.exception.UserNotFoundException;
import com.lalit.devpilot.model.EventType;
import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.IncidentEvent;
import com.lalit.devpilot.model.Severity;
import com.lalit.devpilot.model.Status;
import com.lalit.devpilot.model.User;
import com.lalit.devpilot.repository.IncidentEventRepository;
import com.lalit.devpilot.repository.IncidentRepository;
import com.lalit.devpilot.repository.UserRepository;

@Service
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentEventRepository incidentEventRepository;
    private final UserRepository userRepository;

    public IncidentServiceImpl(
            IncidentRepository incidentRepository,
            IncidentEventRepository incidentEventRepository,
            UserRepository userRepository) {

        this.incidentRepository = incidentRepository;
        this.incidentEventRepository = incidentEventRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Incident reportIncident(
            String title,
            String description,
            Severity severity,
            Integer reportedById) {

        // 1. Validate input
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }

        if (severity == null) {
            throw new IllegalArgumentException("Severity is required");
        }

        if (reportedById == null) {
            throw new IllegalArgumentException("Reporter ID is required");
        }

        // 2. Find the reporter
        User reporter = userRepository.findById(reportedById)
                .orElseThrow(() -> new UserNotFoundException(reportedById));

        // 3. Create and save the incident
        Incident incident = new Incident(
                title,
                description,
                severity,
                reporter);

        Incident saved = incidentRepository.save(incident);

        // 4. Create and save the CREATED event
        IncidentEvent event = new IncidentEvent(
                saved,
                EventType.CREATED,
                "Incident reported",
                reporter);

        incidentEventRepository.save(event);

        // 5. Return the saved incident
        return saved;
    }

    @Override
    @Transactional
    public Incident assignIncident(int incidentId, User assignee, User actor) {

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new IncidentNotFoundException(
                                "Incident not found: " + incidentId));

        if (assignee == null) {
            throw new IllegalArgumentException("Assignee cannot be null");
        }

        if (assignee.equals(incident.getAssignedTo())) {
            throw new IllegalArgumentException(
                    "Incident is already assigned to this user");
        }

        incident.setAssignedTo(assignee);
        incidentRepository.update(incident);

        IncidentEvent event = new IncidentEvent(
                incident,
                EventType.ASSIGNED,
                "Incident assigned to " + assignee.getName(),
                actor);

        incidentEventRepository.save(event);

        return incident;
    }

    private static final Map<Status, Set<Status>> ALLOWED_TRANSITIONS = Map.of(
            Status.OPEN, Set.of(
                    Status.INVESTIGATING,
                    Status.RESOLVED),

            Status.INVESTIGATING, Set.of(
                    Status.IDENTIFIED,
                    Status.RESOLVED),

            Status.IDENTIFIED, Set.of(
                    Status.MONITORING,
                    Status.INVESTIGATING),

            Status.MONITORING, Set.of(
                    Status.INVESTIGATING,
                    Status.RESOLVED),

            Status.RESOLVED, Set.of(
                    Status.INVESTIGATING,
                    Status.CLOSED),

            Status.CLOSED, Set.of(
                    Status.INVESTIGATING));

    @Override
    @Transactional
    public Incident changeStatus(
            int incidentId,
            Status newStatus,
            User actor,
            String note) {

        // 1. Validate input
        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException(
                    "Status change note cannot be blank");
        }

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "New status cannot be null");
        }

        // 2. Find the incident
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new IncidentNotFoundException(
                                "Incident not found: " + incidentId));

        // 3. Check whether the transition is allowed
        Status currentStatus = incident.getStatus();
        Set<Status> allowedTransitions =
                ALLOWED_TRANSITIONS.get(currentStatus);

        if (allowedTransitions == null
                || !allowedTransitions.contains(newStatus)) {

            throw new IllegalArgumentException(
                    "Invalid status transition: "
                            + currentStatus + " -> " + newStatus);
        }

        // 4. Update the incident
        incident.setStatus(newStatus);
        incidentRepository.update(incident);

        // 5. Create and save the status-change event
        IncidentEvent event = new IncidentEvent(
                incident,
                EventType.STATUS_CHANGED,
                note,
                actor);

        incidentEventRepository.save(event);

        return incident;
    }

    @Override
@Transactional(readOnly = true)
public Incident getIncident(int incidentId) {
    return incidentRepository.findById(incidentId)
            .orElseThrow(() -> new IncidentNotFoundException(
                    "Incident not found with id: " + incidentId
            ));
}

    @Override
    @Transactional(readOnly = true)
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidentEvent> getIncidentTimeline(int incidentId) {
        return incidentEventRepository.findByIncidentId(incidentId);
    }
}