package com.lalit.devpilot.dto;

import java.time.LocalDateTime;
import com.lalit.devpilot.model.EventType;

public record IncidentEventResponse(
        int id,
        EventType eventType,
        String message,
        int createdById,
        String createdByName,
        LocalDateTime createdAt) {
}
