package com.lalit.devpilot.controller;
import com.lalit.devpilot.repository.UserRepository;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.lalit.devpilot.exception.IncidentNotFoundException;
import com.lalit.devpilot.exception.UserNotFoundException;
import com.lalit.devpilot.model.EmploymentStatus;
import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.Role;
import com.lalit.devpilot.model.Severity;
import com.lalit.devpilot.model.Status;
import com.lalit.devpilot.model.User;
import com.lalit.devpilot.service.IncidentService;

@WebMvcTest(IncidentController.class)
class IncidentControllerTest {

    @MockitoBean
UserRepository userRepository;
    @Autowired
    MockMvc mockMvc;

    // The real service is replaced by a mock; we control what it returns or throws.
    @MockitoBean
    IncidentService incidentService;

    private Incident sampleIncident() {
        User reporter = new User(6, "Reporter Dev", "r@test.com", Role.DEVELOPER, EmploymentStatus.ACTIVE);
        return new Incident(9, "Payment service down", "desc", Severity.CRITICAL,
                Status.OPEN, reporter, null, null, null);
    }

    // ---------- GET ----------

    @Test
    void getIncident_unknownId_returns404WithErrorBody() throws Exception {
        when(incidentService.getIncident(99999))
                .thenThrow(new IncidentNotFoundException("Incident not found with id: 99999"));

        mockMvc.perform(get("/api/incidents/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Incident not found with id: 99999"))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    // ---------- POST ----------

    @Test
    void createIncident_valid_returns201AndBody() throws Exception {
        when(incidentService.reportIncident(any(), any(), any(), any())).thenReturn(sampleIncident());

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Payment service down","description":"desc",
                                 "severity":"CRITICAL","reportedById":6}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.assignedToId").doesNotExist());
    }

    @Test
    void createIncident_blankTitle_returns400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"  ","severity":"LOW","reportedById":6}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[0]").value(org.hamcrest.Matchers.startsWith("title")));
    }

    @Test
    void createIncident_unknownReporter_returns404() throws Exception {
        when(incidentService.reportIncident(any(), any(), any(), any()))
                .thenThrow(new UserNotFoundException(99));

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"T","severity":"LOW","reportedById":99}
                                """))
                .andExpect(status().isNotFound());
    }

    // ---------- PUT assignee ----------

    @Test
    void assign_missingAssigneeId_returns400() throws Exception {
        mockMvc.perform(put("/api/incidents/9/assignee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"actorId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0]").value(org.hamcrest.Matchers.startsWith("assigneeId")));
    }

    @Test
    void assign_duplicate_returns400WithServiceMessage() throws Exception {
        when(incidentService.assignIncident(anyInt(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Incident is already assigned to this user"));

        mockMvc.perform(put("/api/incidents/9/assignee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"assigneeId":22,"actorId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incident is already assigned to this user"));
    }

    // ---------- PATCH status ----------

    @Test
    void changeStatus_blankNote_returns400WithFieldError() throws Exception {
        mockMvc.perform(patch("/api/incidents/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"INVESTIGATING","actorId":1,"note":"   "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0]").value("note: must not be blank"));
    }

    @Test
    void changeStatus_invalidTransition_returns400() throws Exception {
        when(incidentService.changeStatus(anyInt(), any(), anyInt(), any()))
                .thenThrow(new IllegalArgumentException("Invalid status transition: OPEN -> CLOSED"));

        mockMvc.perform(patch("/api/incidents/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"CLOSED","actorId":1,"note":"skip ahead"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid status transition: OPEN -> CLOSED"));
    }

    // ---------- 500 ----------

    @Test
    void unexpectedError_returns500WithoutLeakingDetails() throws Exception {
        when(incidentService.getAllIncidents())
                .thenThrow(new RuntimeException("secret database detail"));

        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("secret"))));
    }
}