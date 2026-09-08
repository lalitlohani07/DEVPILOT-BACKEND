package com.lalit.devpilot.repository;

import com.lalit.devpilot.model.Incident;
import com.lalit.devpilot.model.User;
import com.lalit.devpilot.repository.UserRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;
import com.lalit.devpilot.exception.DataAccessException;

@Repository
public class IncidentRepositoryImpl implements IncidentRepository {
    private final DataSource dataSource;
    private final UserRepository userrepository;

    public IncidentRepositoryImpl(
            DataSource dataSource,
            UserRepository userrepository) {

        this.dataSource = dataSource;
        this.userrepository = userrepository;
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
                Statement.RETURN_GENERATED_KEYS)
    ) {

        statement.setString(1, incident.getTitle());
        statement.setString(2, incident.getDescription());
        statement.setString(3, incident.getSeverity().name());
        statement.setString(4, incident.getStatus().name());
        statement.setInt(5, incident.getReportedBy().getId());
        statement.setInt(6, incident.getAssignedTo().getId());

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


}
