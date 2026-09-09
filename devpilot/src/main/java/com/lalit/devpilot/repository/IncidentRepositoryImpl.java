package com.lalit.devpilot.repository;

import com.lalit.devpilot.model.EmploymentStatus;
import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.User;
import com.lalit.devpilot.model.Severity;
import com.lalit.devpilot.model.Status;
import com.lalit.devpilot.repository.UserRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;
import com.lalit.devpilot.exception.DataAccessException;
import java.sql.Types;
@Repository
public class IncidentRepositoryImpl implements IncidentRepository {
    private final DataSource dataSource;
    private final UserRepository userRepository;

    public IncidentRepositoryImpl(
            DataSource dataSource,
            UserRepository userRepository) {

        this.dataSource = dataSource;
        this.userRepository = userRepository;
    }

    @Override
    public Incident save(Incident incident) {

        String sql = """
                INSERT INTO incidents
                (title, description, severity, status, reported_by_id, assigned_to_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, incident.getTitle());
            statement.setString(2, incident.getDescription());
            statement.setString(3, incident.getSeverity().name());
            statement.setString(4, incident.getStatus().name());
            statement.setInt(5, incident.getReportedBy().getId());
            if (incident.getAssignedTo() != null) {
    statement.setInt(6, incident.getAssignedTo().getId());
} else {
    statement.setNull(6, Types.INTEGER);
}

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Failed to insert incident.");
            }

            try (var generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    incident.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Failed to retrieve generated ID.");
                }
            }

            return incident;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to save incident.", e);
        }
    }

    @Override
    public Optional<Incident> findById(int id) {

        String sql = """
                SELECT id,
       title,
       description,
       severity,
       status,
       reported_by_id,
       assigned_to_id,
       created_at,
       updated_at
FROM incidents
WHERE id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            // set id

            // execute query
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    Incident incident = new Incident(
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            Severity.valueOf(resultSet.getString("severity")),
                            Status.valueOf(resultSet.getString("status")),
                            userRepository.findById(
                                    resultSet.getInt("reported_by_id")).orElse(null),
                            userRepository.findById(
                                    resultSet.getInt("assigned_to_id")).orElse(null),
                            resultSet.getTimestamp("created_at").toLocalDateTime(),
                            resultSet.getTimestamp("updated_at").toLocalDateTime());
                    return Optional.of(incident);
                }
                return Optional.empty();
            }

        }

        catch (SQLException e) {
            throw new DataAccessException("Failed to find incident by id.", e);
        }

    }
    // TODO: N+1 query problem — each row triggers 2 extra queries via userRepository.findById().
    //  Optimize with JOIN in improvements pass.
    @Override
public List<Incident> findAll() {

    String sql = """
              SELECT id,
       title,
       description,
       severity,
       status,
       reported_by_id,
       assigned_to_id,
       created_at,
       updated_at
FROM incidents
            """;

    List<Incident> incidents = new ArrayList<>();

    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()
    ) {

        while (resultSet.next()) {

            Incident incident = new Incident(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getString("description"),
                    Severity.valueOf(resultSet.getString("severity")),
                    Status.valueOf(resultSet.getString("status")),
                    userRepository.findById(
                            resultSet.getInt("reported_by_id")
                    ).orElse(null),
                    userRepository.findById(
                            resultSet.getInt("assigned_to_id")
                    ).orElse(null),
                    resultSet.getTimestamp("created_at").toLocalDateTime(),
                    resultSet.getTimestamp("updated_at").toLocalDateTime()
            );

            incidents.add(incident);
        }

        return incidents;

    } catch (SQLException e) {
        throw new DataAccessException("Failed to find all incidents.", e);
    }
}
@Override
public boolean update(Incident incident) {

    String sql = """
            UPDATE incidents
            SET title = ?,
                description = ?,
                severity = ?,
                status = ?,
                reported_by_id = ?,
                assigned_to_id = ?
            WHERE id = ?
            """;

    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)
    ) {

        statement.setString(1, incident.getTitle());
        statement.setString(2, incident.getDescription());
        statement.setString(3, incident.getSeverity().name());
        statement.setString(4, incident.getStatus().name());
        statement.setInt(5, incident.getReportedBy().getId());
       if (incident.getAssignedTo() != null) {
    statement.setInt(6, incident.getAssignedTo().getId());
} else {
    statement.setNull(6, Types.INTEGER);
}
        statement.setInt(7, incident.getId());

        int rowsAffected = statement.executeUpdate();

        return rowsAffected > 0;

    } catch (SQLException e) {
        throw new DataAccessException("Failed to update incident.", e);
    }
}
}
