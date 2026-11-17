package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import validators.ValidationException;
import validators.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Database repository for friendships
 */
public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private final Validator<Friendship> validator;
    private final String url;
    private final String username;
    private final String password;

    /**
     * creates a database repository for friendships
     * @param validator the validator for friendships
     * @param url the database url
     * @param username the database user
     * @param password the database password
     */
    public FriendshipDbRepository(Validator<Friendship> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * finds one friendship by composite id
     * @param id the pair of user ids
     * @return Optional with the friendship or empty if not found
     * @throws IllegalArgumentException if id is null
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        if (id == null || id.getLeft() == null || id.getRight() == null) {
            throw new IllegalArgumentException("Id-ul nu poate fi nul");
        }

        long id1 = Math.min(id.getLeft(), id.getRight());
        long id2 = Math.max(id.getLeft(), id.getRight());

        String sql = "SELECT * FROM friendships WHERE id1 = ? AND id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id1);
            statement.setLong(2, id2);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(extractFriendship(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw handleSqlException("findOne", e);
        }
    }

    /**
     * gets all friendships from the database
     * @return all friendships ordered by ids
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        String sql = "SELECT * FROM friendships ORDER BY id1, id2";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                friendships.add(extractFriendship(resultSet));
            }
        } catch (SQLException e) {
            throw handleSqlException("findAll", e);
        }
        return friendships;
    }

    /**
     * saves a new friendship in the database
     * @param entity the friendship to save
     * @return Optional.empty() if saved, or Optional with the friendship if it already exists
     * @throws IllegalArgumentException if entity is null
     * @throws ValidationException if entity is not valid
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<Friendship> save(Friendship entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity nu poate fi nula");
        }
        validator.validate(entity);

        long id1 = Math.min(entity.getFirstFriend().getId(), entity.getSecondFriend().getId());
        long id2 = Math.max(entity.getFirstFriend().getId(), entity.getSecondFriend().getId());
        LocalDateTime date = entity.getDate() != null ? entity.getDate() : LocalDateTime.now();

        String sql = "INSERT INTO friendships (id1, id2, friends_from) VALUES (?, ?, ?) ON CONFLICT (id1, id2) DO NOTHING";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setTimestamp(3, Timestamp.valueOf(date));

            int rows = statement.executeUpdate();
            return rows == 0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException e) {
            throw handleSqlException("save", e);
        }
    }

    /**
     * deletes a friendship by composite id
     * @param id the pair of user ids
     * @return Optional with the removed friendship or empty if not found
     * @throws IllegalArgumentException if id is null
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        Optional<Friendship> found = findOne(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }

        long id1 = found.get().getId().getLeft();
        long id2 = found.get().getId().getRight();

        String sql = "DELETE FROM friendships WHERE id1 = ? AND id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw handleSqlException("delete", e);
        }
        return found;
    }

    /**
     * updates the date of an existing friendship
     * @param entity the friendship to update
     * @return Optional.empty() if updated, or Optional with the friendship if not found
     * @throws IllegalArgumentException if entity is null
     * @throws ValidationException if entity is not valid
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<Friendship> update(Friendship entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity nu poate fi nula");
        }
        validator.validate(entity);

        long id1 = Math.min(entity.getFirstFriend().getId(), entity.getSecondFriend().getId());
        long id2 = Math.max(entity.getFirstFriend().getId(), entity.getSecondFriend().getId());
        LocalDateTime date = entity.getDate() != null ? entity.getDate() : LocalDateTime.now();

        String sql = "UPDATE friendships SET friends_from = ? WHERE id1 = ? AND id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(date));
            statement.setLong(2, id1);
            statement.setLong(3, id2);

            int rows = statement.executeUpdate();
            return rows == 0 ? Optional.of(entity) : Optional.empty();
        } catch (SQLException e) {
            throw handleSqlException("update", e);
        }
    }

    private Friendship extractFriendship(ResultSet resultSet) throws SQLException {
        Long id1 = resultSet.getLong("id1");
        Long id2 = resultSet.getLong("id2");
        Timestamp timestamp = resultSet.getTimestamp("friends_from");

        User user1 = new User("..", "..");
        user1.setId(id1);

        User user2 = new User("..", "..");
        user2.setId(id2);

        Friendship friendship = new Friendship(user1, user2);
        if (timestamp != null) {
            friendship.setDate(timestamp.toLocalDateTime());
        }
        return friendship;
    }

    private RepositoryException handleSqlException(String operation, SQLException e) {
        String sqlState = e.getSQLState();
        if (sqlState != null && sqlState.startsWith("08")) {
            return new RepositoryException("Nu se poate conecta la baza de date, server e oprit", e);
        }
        if (sqlState != null && sqlState.startsWith("23")) {
            return new RepositoryException("Date invalide", e);
        }
        return new RepositoryException("Eroare la baza de date (" + operation + "): " + e.getMessage(), e);
    }
}