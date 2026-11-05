package com.lalit.devpilot.dto;

import jakarta.validation.constraints.NotNull;

public record AssignIncidentRequest(
        @NotNull Integer assigneeId,
        @NotNull Integer actorId) {
}
