package com.lalit.devpilot.model;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "incident_events")
public class IncidentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventType eventType;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    // Database-owned: MySQL fills it on INSERT and it never changes afterwards.
    // Fully qualified because our own EventType enum is in this package.
    @Generated(event = org.hibernate.generator.EventType.INSERT)
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Required by JPA/Hibernate (it creates objects by reflection, then fills fields).
    // protected so application code must use the real constructors.
    protected IncidentEvent() {
    }

    public IncidentEvent(Incident incident,
            EventType eventType,
            String message,
            User createdBy) {
        this.incident = incident;
        this.eventType = eventType;
        this.message = message;
        this.createdBy = createdBy;
    }

    public IncidentEvent(
            int id,
            Incident incident,
            EventType eventType,
            String message,
            User createdBy,
            LocalDateTime createdAt) {
        this.id = id;
        this.incident = incident;
        this.eventType = eventType;
        this.message = message;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public Incident getIncident() {
        return incident;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getMessage() {
        return message;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // TEMPORARY: used only by the JDBC IncidentEventRepositoryImpl.
    // Remove when IncidentEvent persistence moves to JPA.
    public void setId(int id) {
        this.id = id;
    }

    // Prints ids only for the relationships: with LAZY loading, printing the full
    // Incident/User could trigger extra queries or a LazyInitializationException.
    @Override
    public String toString() {
        return "IncidentEvent{" +
                "id=" + id +
                ", incidentId=" + (incident == null ? null : incident.getId()) +
                ", eventType=" + eventType +
                ", message='" + message + '\'' +
                ", createdById=" + (createdBy == null ? null : createdBy.getId()) +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof IncidentEvent other))
            return false;
        if (this.id == 0 || other.id == 0)
            return false;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}