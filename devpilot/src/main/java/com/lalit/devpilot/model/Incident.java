package com.lalit.devpilot.model;

import java.time.LocalDateTime;

public class Incident {
    private int id;
    private String title;
    private String description;
    private Severity severity;
    private Status status;
    private User reportedBy;
    private User assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Incident(String title,
            String description,
            Severity severity,
            User reportedBy) {

        this.title = title;
        this.description = description;
        this.severity = severity;
        this.reportedBy = reportedBy;
        this.status = Status.OPEN;

    }

    public Incident(int id,
            String title,
            String description,
            Severity severity,
            Status status,
            User reportedBy,
            User assignedTo,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.reportedBy = reportedBy;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    // ---------------------------------------------

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Status getStatus() {
        return status;
    }

    public User getReportedBy() {
        return reportedBy;

    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description ='" + description + '\'' +
                ",severity=" + severity +
                ", status=" + status +
                ", reportedBy ='" + reportedBy + '\'' +
                ",assignedTo =" + assignedTo +
                ", createdAt =" + createdAt +
                ", updatedAt =" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Incident other = (Incident) obj;

        if (this.id == 0 || other.id == 0) {
            return false;
        }

        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
