package com.lalit.devpilot.dto;

import java.time.LocalDateTime;

import com.lalit.devpilot.model.Severity;
import com.lalit.devpilot.model.Status;

public record IncidentResponse(
        int id,
        String title,
        String description,
        Severity severity,
        Status status,
        int reportedById,
        String reportedByName,
        Integer assignedToId,
        String assignedToName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}