package com.lalit.devpilot.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;  
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
//import jakarta.persistence.FetchType;
import static jakarta.persistence.FetchType.LAZY;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
@Entity 
@Table(name = "incidents")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false, length = 200)
private String title;

@Column(name = "description", columnDefinition = "TEXT")
private String description;

  @Enumerated(EnumType.STRING)
@Column(name = "severity", nullable = false, length = 50)
private Severity severity;

   @Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false, length = 50)
private Status status;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "reported_by_id", nullable = false)
    private User reportedBy;

    @ManyToOne(fetch = LAZY)
@JoinColumn(name = "assigned_to_id")
private User assignedTo;
    
    @Generated 
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;


@Generated(event = {EventType.INSERT, EventType.UPDATE})
@Column(name = "updated_at", insertable = false, updatable = false)
private LocalDateTime updatedAt;
    protected Incident(){

    }
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
public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
}

public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
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
