package com.lalit.devpilot.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lalit.devpilot.dto.IncidentResponse;
import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.service.IncidentService;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

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

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncident(
            @PathVariable int id) {

        return incidentService.getIncident(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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
                incident.getUpdatedAt()
        );
    }
}
