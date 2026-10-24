// package com.lalit.devpilot.repository;
// import com.lalit.devpilot.model.IncidentEvent;
// import java.util.List;
// import java.util.Optional;

// public interface IncidentEventRepository {
//     IncidentEvent save(IncidentEvent event);
//     Optional<IncidentEvent> findById(int id);
//     List<IncidentEvent> findByIncidentId(int incidentId);
// }
package com.lalit.devpilot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lalit.devpilot.model.IncidentEvent;


public interface IncidentEventRepository extends JpaRepository<IncidentEvent, Integer> {

    // Load the incident with its users and the author in the same query,
    // so nothing returned is a lazy proxy.
    @Override
    @Query("""
            select e from IncidentEvent e
            join fetch e.incident i
            join fetch i.reportedBy
            left join fetch i.assignedTo
            join fetch e.createdBy
            where e.id = :id
            """)
    Optional<IncidentEvent> findById(@Param("id") Integer id);

    // Timeline of one incident, oldest first, in a single query.
    @Query("""
            select e from IncidentEvent e
            join fetch e.createdBy
            where e.incident.id = :incidentId
            order by e.createdAt asc, e.id asc
            """)
    List<IncidentEvent> findByIncidentId(@Param("incidentId") int incidentId);
}