package com.lalit.devpilot.dto;

import com.lalit.devpilot.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
        @NotNull Status status,
        @NotNull Integer actorId,
        @NotBlank String note) {
}
