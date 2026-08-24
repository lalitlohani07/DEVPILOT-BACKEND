package com.lalit.devpilot.repository;
import com.lalit.devpilot.exception.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import javax.sql.DataSource;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.lalit.devpilot.model.EmploymentStatus;
import com.lalit.devpilot.model.User;
import com.lalit.devpilot.model.Role;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final DataSource dataSource;

    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User save(User user) {

        String sql = """
                INSERT INTO users(name, email, role, employment_status)
                VALUES (?, ?, ?, ?)
                """;

             try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getRole().name());
            statement.setString(4, user.getEmploymentStatus().name());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Failed to insert user.");
            }

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Failed to retrieve generated ID.");
                }
            }

            return user;

        }

        // If the code inside try throws a SQLException come here
        catch (SQLException e) {
            throw new DataAccessException("Error saving user.", e);
        }
    }

    ///// find by id fxn implementation
    @Override
    public Optional<User> findById(int id) {

        String sql = """
                SELECT id, name, email, role, employment_status FROM users WHERE id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    User user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            Role.valueOf(resultSet.getString("role")),
                            EmploymentStatus.valueOf(
                                    resultSet.getString("employment_status")));

                    return Optional.of(user);
                }

                return Optional.empty();
            }

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Failed to find user with id: " + id,
                    e);
        }
    }

    @Override
    public List<User> findAll() {

        String sql = """
                SELECT id, name, email, role, employment_status FROM users
                """;
        List<User> users = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            Role.valueOf(resultSet.getString("role")),
                            EmploymentStatus.valueOf(
                                    resultSet.getString("employment_status")));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find all users", e);
        }

        return users;
    }

    @Override
    public Optional<User> findByEmail(String email) {

        String sql = """
                SELECT id, name, email, role, employment_status FROM users WHERE email = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    User user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            Role.valueOf(resultSet.getString("role")),
                            EmploymentStatus.valueOf(
                                    resultSet.getString("employment_status")));

                    return Optional.of(user);
                }

                return Optional.empty();
            }

        } catch (SQLException e) {

            throw new DataAccessException(
                    "Failed to find user with email: " + email,
                    e);
        }
    }
    @Override
    public boolean update(User user) {

        String sql = """
                UPDATE users SET name = ?,email = ?,role = ?,employment_status = ? WHERE id = ?
                """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getRole().name());
            statement.setString(4, user.getEmploymentStatus().name());
            statement.setInt(5, user.getId());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
        catch (SQLException e) {

            throw new DataAccessException(
                    "Failed to update user: " ,
                    e);
        }
     }
     @Override
    public boolean deactivate(int id) {
        String sql = """
               UPDATE users SET employment_status = ? WHERE id = ?
                """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, EmploymentStatus.INACTIVE.name());
            statement.setInt(2, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
        catch (SQLException e) {

            throw new DataAccessException(
                    "Failed to delete user with id: " + id ,
                    e);
        }
    }
}
