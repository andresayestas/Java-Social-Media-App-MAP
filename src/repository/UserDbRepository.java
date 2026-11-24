package repository;

import domain.User;
import util.paging.Page;
import util.paging.Pageable;
import validators.ValidationException;
import validators.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 - Database repository for users
 */
public class UserDbRepository implements PagingRepository<Long, User> {
    private final Validator<User> validator;
    private final String url;
    private final String username;
    private final String password;

    /**
     - creates a database repository for users
     - @param validator the validator for users
     - @param url the database url
     - @param username the database user
     - @param password the database password
     */
    public UserDbRepository(Validator<User> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     - finds one user by id
     - @param id the id of the user
     - @return Optional with the user or empty if not found
     - @throws IllegalArgumentException if id is null
     - @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<User> findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id-ul nu poate fi nul");
        }

        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    User user = new User(firstName, lastName);
                    user.setId(id);
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw handleSqlException("findOne", e);
        }
    }

    /**
     - gets all users from the database
     - @return all users ordered by id
     - @throws RepositoryException if a database error occurs
     */
    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                User user = new User(firstName, lastName);
                user.setId(id);
                users.add(user);
            }
        } catch (SQLException e) {
            throw handleSqlException("findAll", e);
        }
        return users;
    }

    /**
     - retrieves a specific page of users from the database
     - @param pageable pagination parameters specifying page number and size
     - @return a Page containing the users for the requested page and the total user count
     - @throws IllegalArgumentException if pageable is null or pagination parameters are invalid
     - @throws RepositoryException if a database error occurs
     */
    @Override
    public Page<User> findAllOnPage(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        List<User> users = new ArrayList<>();
        int totalUsers = 0;
        int offset = pageable.getPageNumber() * pageable.getPageSize();

        String countSql = "SELECT COUNT(*) FROM users";
        String pageSql = "SELECT * FROM users ORDER BY id LIMIT ? OFFSET ?";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            try (PreparedStatement countStatement = connection.prepareStatement(countSql);
                 ResultSet countResult = countStatement.executeQuery()) {
                if (countResult.next()) {
                    totalUsers = countResult.getInt(1);
                }
            }

            try (PreparedStatement pageStatement = connection.prepareStatement(pageSql)) {
                pageStatement.setInt(1, pageable.getPageSize());
                pageStatement.setInt(2, offset);

                try (ResultSet resultSet = pageStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Long id = resultSet.getLong("id");
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");

                        User user = new User(firstName, lastName);
                        user.setId(id);
                        users.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw handleSqlException("findAllOnPage", e);
        }

        return new Page<>(users, totalUsers);
    }

    /**
     - saves a new user and sets the generated id from the database
     - @param entity the user to save
     - @return Optional.empty() when saved
     - @throws IllegalArgumentException if entity is null
     - @throws ValidationException if entity is not valid
     - @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<User> save(User entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity nu poate fi nula");
        }
        validator.validate(entity);

        String sql = "INSERT INTO users (first_name, last_name) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong("id"));
                }
            }
        } catch (SQLException e) {
            throw handleSqlException("save", e);
        }
        return Optional.empty();
    }

    /**
     - deletes a user by id
     - @param id the id to delete
     - @return Optional with the removed user or empty if not found
     - @throws IllegalArgumentException if id is null
     - @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<User> delete(Long id) {
        Optional<User> found = findOne(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw handleSqlException("delete", e);
        }
        return found;
    }

    /**
     - updates an existing user
     - @param entity the user to update
     - @return Optional.empty() if updated, or Optional with the user if id does not exist
     - @throws IllegalArgumentException if entity or its id is null
     - @throws ValidationException if entity is not valid
     - @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<User> update(User entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity nu poate fi nula");
        }
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Id-ul nu poate fi nul");
        }
        validator.validate(entity);

        String sql = "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3, entity.getId());

            int rows = statement.executeUpdate();
            return rows == 0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException e) {
            throw handleSqlException("update", e);
        }
    }

    private RepositoryException handleSqlException(String operation, SQLException e) {
        String sqlState = e.getSQLState();
        if (sqlState != null && sqlState.startsWith("08")) {
            return new RepositoryException("Nu se poate conecta la baza de date, server e oprit)", e);
        }
        if (sqlState != null && sqlState.startsWith("23")) {
            return new RepositoryException("Date invalide", e);
        }
        return new RepositoryException("Eroare la baza de date (" + operation + "): " + e.getMessage(), e);
    }
}