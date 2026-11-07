package com.lalit.devpilot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.IncidentEvent;
import com.lalit.devpilot.model.Role;
import com.lalit.devpilot.model.Severity;
import com.lalit.devpilot.model.User;
import com.lalit.devpilot.repository.IncidentEventRepository;
import com.lalit.devpilot.repository.IncidentRepository;
import com.lalit.devpilot.repository.UserRepository;

// Real Spring context + real MySQL, but a separate database so nothing real is touched.
// Credentials still come from application.properties; only the URL is overridden.
//
// Deliberately NOT @Transactional: a test-managed transaction would wrap the service
// call and hide exactly the commit/rollback behavior we want to observe.
@SpringBootTest(properties = "spring.datasource.url=jdbc:mysql://localhost:3306/devpilot_test")
class IncidentTransactionTest {

    @Autowired IncidentService service;
    @Autowired IncidentRepository incidentRepository;
    @Autowired UserRepository userRepository;

    // A spy runs the real repository unless we tell it to fail.
    @MockitoSpyBean IncidentEventRepository eventRepository;

    User reporter;

    @BeforeEach
    void setUp() {
        reporter = userRepository.save(
                new User("Tx Tester", "tx" + System.nanoTime() + "@test.com", Role.DEVELOPER));
    }

    @AfterEach
    void cleanUp() {
        // Events disappear with their incident (ON DELETE CASCADE in the schema).
        incidentRepository.findAll().stream()
                .filter(i -> i.getReportedBy().getId() == reporter.getId())
                .forEach(i -> incidentRepository.delete(i.getId()));
        userRepository.deleteById(reporter.getId());
    }

    @Test
    void reportIncident_whenEventSaveFails_rollsBackTheIncident() {
        // The incident save succeeds, then the event save blows up.
        doThrow(new RuntimeException("simulated event failure"))
                .when(eventRepository).save(any(IncidentEvent.class));

        assertThrows(RuntimeException.class, () ->
                service.reportIncident("ROLLBACK TEST", "desc", Severity.LOW, reporter.getId()));

        boolean orphanExists = incidentRepository.findAll().stream()
                .anyMatch(i -> "ROLLBACK TEST".equals(i.getTitle())
                        && i.getReportedBy().getId() == reporter.getId());

        // With @Transactional, the incident insert was rolled back too.
        assertFalse(orphanExists, "incident was left behind without its CREATED event");
    }

    @Test
    void reportIncident_whenEverythingWorks_commitsIncidentAndEvent() {
        Incident saved = service.reportIncident("COMMIT TEST", "desc", Severity.LOW, reporter.getId());

        assertEquals(1, service.getIncidentTimeline(saved.getId()).size());
    }
}
