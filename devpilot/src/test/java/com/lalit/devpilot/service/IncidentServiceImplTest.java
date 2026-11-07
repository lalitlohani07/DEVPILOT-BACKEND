package com.lalit.devpilot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lalit.devpilot.exception.IncidentNotFoundException;
import com.lalit.devpilot.exception.UserNotFoundException;
import com.lalit.devpilot.model.EmploymentStatus;
import com.lalit.devpilot.model.EventType;
import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.IncidentEvent;
import com.lalit.devpilot.model.Role;
import com.lalit.devpilot.model.Severity;
import com.lalit.devpilot.model.Status;
import com.lalit.devpilot.model.User;
import com.lalit.devpilot.repository.IncidentEventRepository;
import com.lalit.devpilot.repository.IncidentRepository;
import com.lalit.devpilot.repository.UserRepository;

// Mocks replace the three repositories, so only the service's own rules are tested.
@ExtendWith(MockitoExtension.class)
class IncidentServiceImplTest {

    @Mock IncidentRepository incidentRepository;
    @Mock IncidentEventRepository incidentEventRepository;
    @Mock UserRepository userRepository;

    IncidentServiceImpl service;

    User actor;
    User assignee;

    @BeforeEach
    void setUp() {
        service = new IncidentServiceImpl(
                incidentRepository, incidentEventRepository, userRepository);
        actor = new User(1, "Actor", "actor@test.com", Role.DEVELOPER, EmploymentStatus.ACTIVE);
        assignee = new User(2, "Assignee", "assignee@test.com", Role.ON_CALL_ENGINEER, EmploymentStatus.ACTIVE);
    }

    // Builds an incident that already exists in the "database".
    private Incident incidentWith(Status status, User assignedTo) {
        return new Incident(10, "Payment down", "desc", Severity.CRITICAL,
                status, actor, assignedTo, null, null);
    }

    // ---------- reportIncident ----------

    @Test
    void reportIncident_savesIncidentAndCreatedEvent() {
        when(userRepository.findById(1)).thenReturn(Optional.of(actor));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(i -> i.getArgument(0));

        Incident result = service.reportIncident("Payment down", "desc", Severity.CRITICAL, 1);

        assertEquals(Status.OPEN, result.getStatus());
        ArgumentCaptor<IncidentEvent> captor = ArgumentCaptor.forClass(IncidentEvent.class);
        verify(incidentEventRepository).save(captor.capture());
        assertEquals(EventType.CREATED, captor.getValue().getEventType());
        assertSame(actor, captor.getValue().getCreatedBy());
    }

    @Test
    void reportIncident_blankTitle_throwsAndSavesNothing() {
        assertThrows(IllegalArgumentException.class,
                () -> service.reportIncident("  ", "desc", Severity.LOW, 1));
        verifyNoInteractions(incidentRepository, incidentEventRepository);
    }

    @Test
    void reportIncident_unknownReporter_throwsUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.reportIncident("Title", "desc", Severity.LOW, 99));
        verifyNoInteractions(incidentRepository, incidentEventRepository);
    }

    // ---------- assignIncident ----------

    @Test
    void assignIncident_setsAssigneeAndLogsEventByActor() {
        Incident incident = incidentWith(Status.OPEN, null);
        when(incidentRepository.findById(10)).thenReturn(Optional.of(incident));
        when(userRepository.findById(2)).thenReturn(Optional.of(assignee));
        when(userRepository.findById(1)).thenReturn(Optional.of(actor));

        Incident result = service.assignIncident(10, 2, 1);

        assertSame(assignee, result.getAssignedTo());
        ArgumentCaptor<IncidentEvent> captor = ArgumentCaptor.forClass(IncidentEvent.class);
        verify(incidentEventRepository).save(captor.capture());
        assertEquals(EventType.ASSIGNED, captor.getValue().getEventType());
        // The event records who did the assigning, not who was assigned.
        assertSame(actor, captor.getValue().getCreatedBy());
    }

    @Test
    void assignIncident_sameUserAgain_throwsAndLogsNoEvent() {
        Incident incident = incidentWith(Status.OPEN, assignee);
        when(incidentRepository.findById(10)).thenReturn(Optional.of(incident));
        when(userRepository.findById(2)).thenReturn(Optional.of(assignee));
        when(userRepository.findById(1)).thenReturn(Optional.of(actor));

        assertThrows(IllegalArgumentException.class, () -> service.assignIncident(10, 2, 1));
        verify(incidentEventRepository, never()).save(any());
    }

    @Test
    void assignIncident_unknownIncident_throwsNotFound() {
        when(incidentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IncidentNotFoundException.class, () -> service.assignIncident(99, 2, 1));
    }

    // ---------- changeStatus ----------

    @Test
    void changeStatus_validTransition_updatesAndLogsEvent() {
        Incident incident = incidentWith(Status.OPEN, null);
        when(incidentRepository.findById(10)).thenReturn(Optional.of(incident));
        when(userRepository.findById(1)).thenReturn(Optional.of(actor));

        Incident result = service.changeStatus(10, Status.INVESTIGATING, 1, "Looking at logs");

        assertEquals(Status.INVESTIGATING, result.getStatus());
        ArgumentCaptor<IncidentEvent> captor = ArgumentCaptor.forClass(IncidentEvent.class);
        verify(incidentEventRepository).save(captor.capture());
        assertEquals(EventType.STATUS_CHANGED, captor.getValue().getEventType());
        assertEquals("Looking at logs", captor.getValue().getMessage());
    }

    @Test
    void changeStatus_openToClosed_isRejectedWithNoEvent() {
        Incident incident = incidentWith(Status.OPEN, null);
        when(incidentRepository.findById(10)).thenReturn(Optional.of(incident));
        when(userRepository.findById(1)).thenReturn(Optional.of(actor));

        assertThrows(IllegalArgumentException.class,
                () -> service.changeStatus(10, Status.CLOSED, 1, "skip ahead"));
        assertEquals(Status.OPEN, incident.getStatus());
        verify(incidentEventRepository, never()).save(any());
    }

    @Test
    void changeStatus_blankNote_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.changeStatus(10, Status.INVESTIGATING, 1, "   "));
    }

    @Test
    void changeStatus_nullStatus_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.changeStatus(10, null, 1, "note"));
    }

    @Test
    void changeStatus_unknownIncident_throwsNotFound() {
        when(incidentRepository.findById(99)).thenReturn(Optional.empty());


        assertThrows(IncidentNotFoundException.class,
                () -> service.changeStatus(99, Status.INVESTIGATING, 1, "note"));
    }
}
