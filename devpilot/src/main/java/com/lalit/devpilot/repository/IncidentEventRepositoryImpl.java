package com.lalit.devpilot.repository;
import com.lalit.devpilot.exception.DataAccessException;
import com.lalit.devpilot.model.EventType;
import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.IncidentEvent;
import com.lalit.devpilot.model.User;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class IncidentEventRepositoryImpl implements IncidentEventRepository {

    private final DataSource dataSource;
    private final UserRepository userRepository;
    private final IncidentRepository incidentRepository;

    public IncidentEventRepositoryImpl(
            DataSource dataSource,
            UserRepository userRepository,
            IncidentRepository incidentRepository) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
        this.incidentRepository = incidentRepository;
    }

    @Override
    public IncidentEvent save(IncidentEvent event) {

        String sql = """
                INSERT INTO incident_events
                (incident_id, event_type, message, created_by_id)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, event.getIncident().getId());
            statement.setString(2, event.getEventType().name());
            statement.setString(3, event.getMessage());
            statement.setInt(4, event.getCreatedBy().getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to insert incident event.");
            }

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Failed to retrieve generated ID.");
                }
            }

            return event;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to save incident event.", e);
        }
    }

    @Override
    public Optional<IncidentEvent> findById(int id) {

        String sql = """
                SELECT id, incident_id, event_type, message, created_by_id, created_at
                FROM incident_events
                WHERE id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to find incident event by id.", e);
        }
    }

    @Override
    public List<IncidentEvent> findByIncidentId(int incidentId) {

        String sql = """
                SELECT id, incident_id, event_type, message, created_by_id, created_at
                FROM incident_events
                WHERE incident_id = ?
                ORDER BY created_at ASC
                """;

        List<IncidentEvent> events = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, incidentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    events.add(mapRow(resultSet));
                }
            }

            return events;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to find events for incident.", e);
        }
    }

    // Shared row-mapping logic used by findById and findByIncidentId
    private IncidentEvent mapRow(ResultSet resultSet) throws SQLException {

        Incident incident = incidentRepository.findById(
                resultSet.getInt("incident_id")).orElse(null);

        User createdBy = userRepository.findById(
                resultSet.getInt("created_by_id")).orElse(null);

        return new IncidentEvent(
                resultSet.getInt("id"),
                incident,
                EventType.valueOf(resultSet.getString("event_type")),
                resultSet.getString("message"),
                createdBy,
                resultSet.getTimestamp("created_at").toLocalDateTime());
    }
}
