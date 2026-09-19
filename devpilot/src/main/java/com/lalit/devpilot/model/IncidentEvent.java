package com.lalit.devpilot.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class IncidentEvent {
    private int id;
    private Incident incident;
    private EventType eventType;
    private String message;
    private User createdBy;
    private LocalDateTime createdAt;

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
        this.incident = incident;
        this.eventType = eventType;
        this.message = message;
        this.createdBy = createdBy;
        this.id = id;
        this.createdAt = createdAt;

    }
public int getId(){
    return id;
}
   public Incident getIncident(){
    return incident;
   }
public EventType getEventType(){
    return eventType;
}
public String getMessage(){
    return message;
}
public User getCreatedBy(){
    return createdBy;
}
public LocalDateTime getCreatedAt(){
    return createdAt;
}
public void setId(int id) {
    this.id = id;
}
@Override
public String toString() {
    return "IncidentEvent{" +
            "id=" + id +
            ", incident=" + incident +
            ", eventType=" + eventType +
            ", message='" + message + '\'' +
            ", createdBy=" + createdBy +
            ", createdAt=" + createdAt +
            '}';
}

@Override
public boolean equals(Object obj) {
    if (this == obj)
        return true;
    if (obj == null || getClass() != obj.getClass())
        return false;
    IncidentEvent other = (IncidentEvent) obj;
    if (this.id == 0 || other.id == 0)
        return false;
    return this.id == other.id;
}

@Override
public int hashCode() {
    return Objects.hash(id);
}



}
