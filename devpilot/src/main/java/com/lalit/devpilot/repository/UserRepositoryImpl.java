package com.lalit.devpilot.repository;

import com.lalit.devpilot.exception.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.lalit.devpilot.model.User;

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
    User findById(int id){
        String sql = """SELECT * FROM USERS WHERE id = ?
                """;
             try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql))  {
                            statement.setInt(1, id);
                      ResultSet resultSet = statement.executeQuery();
                       if (resultSet.next()) {
resultSet.getInt("id");
resultSet.getString("name");
resultSet.getString("email");

                        }
    }


}
}
