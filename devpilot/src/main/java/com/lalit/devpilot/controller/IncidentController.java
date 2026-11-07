
package com.lalit.devpilot.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.lalit.devpilot.dto.AssignIncidentRequest;
import com.lalit.devpilot.dto.ChangeStatusRequest;
import com.lalit.devpilot.dto.CreateIncidentRequest;
import com.lalit.devpilot.dto.IncidentEventResponse;
import com.lalit.devpilot.dto.IncidentResponse;
import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.User;
import com.lalit.devpilot.repository.UserRepository;
import com.lalit.devpilot.service.IncidentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

        private final IncidentService incidentService;
       // private final UserRepository userRepository;

        public IncidentController(IncidentService incidentService) {
    this.incidentService = incidentService;
}

        @GetMapping
        public List<IncidentResponse> getAllIncidents() {
                return incidentService.getAllIncidents()
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        // @GetMapping("/{id}")
        // public ResponseEntity<IncidentResponse> getIncident(
        // @PathVariable int id) {

        // return incidentService.getIncident(id)
        // .map(this::toResponse)
        // .map(ResponseEntity::ok)
        // .orElseGet(() -> ResponseEntity.notFound().build());
        // }
        @GetMapping("/{id}")
        public ResponseEntity<IncidentResponse> getIncident(
                        @PathVariable int id) {

                Incident incident = incidentService.getIncident(id);
                return ResponseEntity.ok(toResponse(incident));
        }
        // =============================================== post mapping

        @PostMapping
public ResponseEntity<IncidentResponse> createIncident(
        @Valid @RequestBody CreateIncidentRequest request) {

    Incident incident = incidentService.reportIncident(
            request.title(),
            request.description(),
            request.severity(),
            request.reportedById());

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(toResponse(incident));
}
        // Converts an Incident entity into an IncidentResponse DTO.
        private IncidentResponse toResponse(Incident incident) {

                Integer assignedToId = null;
                String assignedToName = null;

                if (incident.getAssignedTo() != null) {
                        assignedToId = incident.getAssignedTo().getId();
                        assignedToName = incident.getAssignedTo().getName();
                }

                return new IncidentResponse(
                                incident.getId(),
                                incident.getTitle(),
                                incident.getDescription(),
                                incident.getSeverity(),
                                incident.getStatus(),
                                incident.getReportedBy().getId(),
                                incident.getReportedBy().getName(),
                                assignedToId,
                                assignedToName,
                                incident.getCreatedAt(),
                                incident.getUpdatedAt());

        }

        // assign incident

        @PutMapping("/{id}/assignee")
        public IncidentResponse assign (@PathVariable int id,@Valid @RequestBody AssignIncidentRequest request)
         {
                return
                toResponse(incidentService.assignIncident(id, request.assigneeId(), request.actorId()));
         }
        // change incident status

        @PatchMapping("/{id}/status")
        public IncidentResponse changeStatus (@PathVariable int id,@Valid @RequestBody ChangeStatusRequest request)
         {
                return
                 toResponse(incidentService.changeStatus(id, request.status(), request.actorId(), request.note()));
         }

        // get incident time line

        @GetMapping("/{id}/events")
        public List<IncidentEventResponse> getTimeline(@PathVariable int id) {
                return incidentService.getIncidentTimeline(id).stream()
                                .map(e -> new IncidentEventResponse(
                                                e.getId(),
                                                e.getEventType(),
                                                e.getMessage(),
                                                e.getCreatedBy().getId(),
                                                e.getCreatedBy().getName(),
                                                e.getCreatedAt()))
                                .toList();
        }
}
