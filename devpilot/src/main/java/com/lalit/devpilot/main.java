// package com.lalit.devpilot;
// import com.lalit.devpilot.config.appConfig;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.context.annotation.AnnotationConfigApplicationContext;
// import com.lalit.devpilot.model.User;
// import com.lalit.devpilot.repository.UserRepository;
// import com.lalit.devpilot.model.Role;
// public class main {
// public static void main(String[] args){
//     AnnotationConfigApplicationContext context =
//         new AnnotationConfigApplicationContext(appConfig.class);

// UserRepository repository =
//         context.getBean(UserRepository.class);
//         System.out.println(repository);
//     //User user = new User("anki","ankt07@gmail",Role.ADMIN);
//     //repository.save(user);
//     //System.out.println(user.getId() + "<here is the id");
//     testUserRepository(repository);
// }
// private static void testUserRepository(UserRepository repository) {

//     // 1. Find by ID
//     Optional<User> userById = repository.findById(3);
//     System.out.println("findById: " + userById);

//     // 2. Find all users
//     List<User> users = repository.findAll();
//     System.out.println("findAll: " + users);

//     // 3. Find by email
//     Optional<User> userByEmail =
//             repository.findByEmail("rohi07@gmail");
//     System.out.println("findByEmail: " + userByEmail);

//     // 4. Update
//     if (userById.isPresent()) {
//         User user = userById.get();

//         user.setName("Rohit Updated");

//         boolean updated = repository.update(user);

//         System.out.println("update: " + updated);
//     }

//     // 5. Find again to verify the update
//     Optional<User> updatedUser = repository.findById(3);
//     System.out.println("after update: " + updatedUser);

//     // 6. Soft delete / deactivate
//     boolean deleted = repository.deactivate(3);
//     System.out.println("delete/deactivate: " + deleted);

//     // 7. Find again to verify soft delete
//     Optional<User> deactivatedUser = repository.findById(3);
//     System.out.println("after delete: " + deactivatedUser);
// }
//}
package com.lalit.devpilot;

import com.lalit.devpilot.config.appConfig;
import com.lalit.devpilot.exception.IncidentNotFoundException;
import com.lalit.devpilot.model.*;
import com.lalit.devpilot.repository.UserRepository;
import com.lalit.devpilot.service.IncidentService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Optional;

public class main {

    public static void main(String[] args) {

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(appConfig.class)) {

            UserRepository userRepository =
                    context.getBean(UserRepository.class);

            IncidentService incidentService =
                    context.getBean(IncidentService.class);

            // ============================================================
            // SETUP
            // ============================================================

            String uniqueId = String.valueOf(System.currentTimeMillis());

            User reporter = userRepository.save(
                    new User(
                            "Reporter Dev",
                            "reporter" + uniqueId + "@devpilot.com",
                            Role.DEVELOPER
                    )
            );

            User oncallEngineer = userRepository.save(
                    new User(
                            "Oncall Eng",
                            "oncall" + uniqueId + "@devpilot.com",
                            Role.ON_CALL_ENGINEER
                    )
            );

            System.out.println("========================================");
            System.out.println("SERVICE LAYER TEST");
            System.out.println("========================================");

            System.out.println("\nReporter: " + reporter);
            System.out.println("On-call engineer: " + oncallEngineer);


            // ============================================================
            // 1. REPORT INCIDENT
            // ============================================================

            System.out.println("\n========== 1. REPORT INCIDENT ==========");

            Incident incident = incidentService.reportIncident(
                    "Payment service down",
                    "Users unable to complete checkout",
                    Severity.CRITICAL,
                    reporter
            );

            System.out.println("Created incident: " + incident);
            System.out.println("Generated ID: " + incident.getId());
            System.out.println("Initial status: " + incident.getStatus());

            if (incident.getId() > 0) {
                System.out.println("[PASS] Incident received generated ID");
            } else {
                System.out.println("[FAIL] Incident ID was not generated");
            }

            if (incident.getStatus() == Status.OPEN) {
                System.out.println("[PASS] Initial status is OPEN");
            } else {
                System.out.println("[FAIL] Initial status is not OPEN");
            }


            // ============================================================
            // 2. ASSIGN INCIDENT
            // ============================================================

            System.out.println("\n========== 2. ASSIGN INCIDENT ==========");

            Incident assigned = incidentService.assignIncident(
                    incident.getId(),
                    oncallEngineer,
                    reporter
            );

            System.out.println("Assigned incident: " + assigned);
            System.out.println("Assigned to: "
                    + assigned.getAssignedTo().getName());

            if (assigned.getAssignedTo().getId()
                    == oncallEngineer.getId()) {

                System.out.println(
                        "[PASS] Incident assigned to correct engineer"
                );

            } else {

                System.out.println(
                        "[FAIL] Incident assigned to wrong engineer"
                );
            }


            // ============================================================
            // 3. DUPLICATE ASSIGNMENT
            // ============================================================

            System.out.println("\n========== 3. DUPLICATE ASSIGNMENT ==========");

            try {

                incidentService.assignIncident(
                        incident.getId(),
                        oncallEngineer,
                        reporter
                );

                System.out.println(
                        "[FAIL] Duplicate assignment was allowed"
                );

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "[PASS] Duplicate assignment blocked: "
                                + e.getMessage()
                );
            }


            // ============================================================
            // 4. VALID STATUS TRANSITION
            // OPEN -> INVESTIGATING
            // ============================================================

            System.out.println("\n========== 4. VALID STATUS CHANGE ==========");

            Incident investigating = incidentService.changeStatus(
                    incident.getId(),
                    Status.INVESTIGATING,
                    oncallEngineer,
                    "Started investigating payment gateway logs"
            );

            System.out.println(
                    "New status: " + investigating.getStatus()
            );

            if (investigating.getStatus() == Status.INVESTIGATING) {

                System.out.println(
                        "[PASS] OPEN -> INVESTIGATING succeeded"
                );

            } else {

                System.out.println(
                        "[FAIL] Status did not change correctly"
                );
            }


            // ============================================================
            // 5. INVALID STATUS TRANSITION
            // INVESTIGATING -> CLOSED
            // ============================================================

            System.out.println("\n========== 5. INVALID STATUS CHANGE ==========");

            try {

                incidentService.changeStatus(
                        incident.getId(),
                        Status.CLOSED,
                        oncallEngineer,
                        "Skipping ahead"
                );

                System.out.println(
                        "[FAIL] Invalid transition was allowed"
                );

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "[PASS] Invalid transition blocked: "
                                + e.getMessage()
                );
            }


            // ============================================================
            // 6. VALID STATUS CHAIN
            //
            // INVESTIGATING
            //       ↓
            // IDENTIFIED
            //       ↓
            // MONITORING
            //       ↓
            // RESOLVED
            //       ↓
            // CLOSED
            // ============================================================

            System.out.println("\n========== 6. STATUS LIFECYCLE ==========");

            incidentService.changeStatus(
                    incident.getId(),
                    Status.IDENTIFIED,
                    oncallEngineer,
                    "Found root cause: expired API key"
            );

            incidentService.changeStatus(
                    incident.getId(),
                    Status.MONITORING,
                    oncallEngineer,
                    "Rotated key, watching metrics"
            );

            incidentService.changeStatus(
                    incident.getId(),
                    Status.RESOLVED,
                    oncallEngineer,
                    "Confirmed stable for 30 minutes"
            );

            Incident closed = incidentService.changeStatus(
                    incident.getId(),
                    Status.CLOSED,
                    oncallEngineer,
                    "Postmortem complete"
            );

            System.out.println(
                    "Final status: " + closed.getStatus()
            );

            if (closed.getStatus() == Status.CLOSED) {

                System.out.println(
                        "[PASS] Complete status lifecycle succeeded"
                );

            } else {

                System.out.println(
                        "[FAIL] Incident did not reach CLOSED"
                );
            }


            // ============================================================
            // 7. NONEXISTENT INCIDENT
            // ============================================================

            System.out.println(
                    "\n========== 7. NONEXISTENT INCIDENT =========="
            );

            try {

                incidentService.assignIncident(
                        99999,
                        oncallEngineer,
                        reporter
                );

                System.out.println(
                        "[FAIL] Expected IncidentNotFoundException"
                );

            } catch (IncidentNotFoundException e) {

                System.out.println(
                        "[PASS] IncidentNotFoundException correctly thrown: "
                                + e.getMessage()
                );
            }


            // ============================================================
            // 8. BLANK STATUS NOTE
            // ============================================================

            System.out.println(
                    "\n========== 8. BLANK STATUS NOTE =========="
            );

            try {

                incidentService.changeStatus(
                        incident.getId(),
                        Status.INVESTIGATING,
                        oncallEngineer,
                        "   "
                );

                System.out.println(
                        "[FAIL] Blank note was allowed"
                );

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "[PASS] Blank note correctly rejected: "
                                + e.getMessage()
                );
            }


            // ============================================================
            // 9. NULL NEW STATUS
            // ============================================================

            System.out.println(
                    "\n========== 9. NULL STATUS =========="
            );

            try {

                incidentService.changeStatus(
                        incident.getId(),
                        null,
                        oncallEngineer,
                        "Trying null status"
                );

                System.out.println(
                        "[FAIL] Null status was allowed"
                );

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "[PASS] Null status correctly rejected: "
                                + e.getMessage()
                );
            }


            // ============================================================
            // 10. INCIDENT TIMELINE
            // ============================================================

            System.out.println(
                    "\n========== 10. INCIDENT TIMELINE =========="
            );

            List<IncidentEvent> timeline =
                    incidentService.getIncidentTimeline(
                            incident.getId()
                    );

            System.out.println(
                    "Total events: " + timeline.size()
            );

            for (IncidentEvent event : timeline) {

                System.out.println(
                        event.getEventType()
                                + " | "
                                + event.getMessage()
                                + " | by "
                                + event.getCreatedBy().getName()
                                + " | at "
                                + event.getCreatedAt()
                );
            }

            /*
             * Expected events:
             *
             * 1. CREATED
             * 2. ASSIGNED
             * 3. STATUS_CHANGED -> INVESTIGATING
             * 4. STATUS_CHANGED -> IDENTIFIED
             * 5. STATUS_CHANGED -> MONITORING
             * 6. STATUS_CHANGED -> RESOLVED
             * 7. STATUS_CHANGED -> CLOSED
             *
             * Invalid operations should NOT create events.
             */

            if (timeline.size() == 7) {

                System.out.println(
                        "[PASS] Timeline contains expected 7 events"
                );

            } else {

                System.out.println(
                        "[FAIL] Expected 7 events but found "
                                + timeline.size()
                );
            }


            // ============================================================
            // 11. GET INCIDENT
            // ============================================================

            System.out.println(
                    "\n========== 11. GET INCIDENT =========="
            );

            Optional<Incident> fetched =
                    incidentService.getIncident(
                            incident.getId()
                    );

            if (fetched.isPresent()) {

                System.out.println(
                        "[PASS] getIncident() found incident"
                );

                System.out.println(
                        "Fetched: " + fetched.get()
                );

            } else {

                System.out.println(
                        "[FAIL] getIncident() did not find incident"
                );
            }


            // ============================================================
            // 12. GET ALL INCIDENTS
            // ============================================================

            System.out.println(
                    "\n========== 12. GET ALL INCIDENTS =========="
            );

            List<Incident> all =
                    incidentService.getAllIncidents();

            System.out.println(
                    "Total incidents in database: "
                            + all.size()
            );

            if (!all.isEmpty()) {

                System.out.println(
                        "[PASS] getAllIncidents() returned data"
                );

            } else {

                System.out.println(
                        "[FAIL] getAllIncidents() returned empty list"
                );
            }


            // ============================================================
            // FINISHED
            // ============================================================

            System.out.println("\n========================================");
            System.out.println("SERVICE LAYER TEST COMPLETED");
            System.out.println("========================================");
        }
    }
}
