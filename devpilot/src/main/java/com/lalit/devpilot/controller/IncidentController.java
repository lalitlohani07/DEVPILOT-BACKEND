
package com.lalit.devpilot.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.lalit.devpilot.dto.CreateIncidentRequest;
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
    private final UserRepository userRepository;

    public IncidentController(
            IncidentService incidentService,
            UserRepository userRepository) {
        this.incidentService = incidentService;
        this.userRepository = userRepository;
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
    //         @PathVariable int id) {

    //     return incidentService.getIncident(id)
    //             .map(this::toResponse)
    //             .map(ResponseEntity::ok)
    //             .orElseGet(() -> ResponseEntity.notFound().build());
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

        User reporter = userRepository.findById(request.reportedById())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reporter not found"));

        Incident incident = incidentService.reportIncident(
                request.title(),
                request.description(),
                request.severity(),
                reporter.getId());

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
}

