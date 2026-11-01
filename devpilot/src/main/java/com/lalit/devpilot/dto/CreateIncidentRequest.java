package com.lalit.devpilot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.lalit.devpilot.model.Severity;

public record CreateIncidentRequest(

        @NotBlank
        String title,

        String description,

        @NotNull
        Severity severity,

        @NotNull
        Integer reportedById

) {
}
