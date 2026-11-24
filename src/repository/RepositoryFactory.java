package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import validators.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

/**
 - Factory class that creates repositories based on configuration
 */
public class RepositoryFactory {
    private final Properties config;
    private final Map<RepositoryType, Function<Validator<User>, Repository<Long, User>>> userCreators = new HashMap<>();
    private final Map<RepositoryType, Function<Validator<Friendship>, Repository<Tuple<Long, Long>, Friendship>>> friendshipCreators = new HashMap<>();

    /**
     - creates a repository factory with the given properties
     - @param config the configuration properties
     */
    public RepositoryFactory(Properties config) {
        this.config = config;
        initCreators();
    }

    private void initCreators() {
        userCreators.put(RepositoryType.MEMORY, InMemoryRepository::new);
        userCreators.put(RepositoryType.FILE, val -> new UserFileRepository(val, config.getProperty("file.utilizatori")));
        userCreators.put(RepositoryType.DATABASE, val -> new UserDbRepository(
                val,
                config.getProperty("db.url"),
                config.getProperty("db.user"),
                config.getProperty("db.password")
        ));

        friendshipCreators.put(RepositoryType.MEMORY, InMemoryRepository::new);
        friendshipCreators.put(RepositoryType.FILE, val -> new FriendshipFileRepository(val, config.getProperty("file.prietenii")));
        friendshipCreators.put(RepositoryType.DATABASE, val -> new FriendshipDbRepository(
                val,
                config.getProperty("db.url"),
                config.getProperty("db.user"),
                config.getProperty("db.password")
        ));
    }

    /**
     - creates a repository for users
     - @param validator the validator for users
     - @return the user repository
     - @throws IllegalArgumentException if repository type is unknown
     */
    public Repository<Long, User> createUserRepository(Validator<User> validator) {
        RepositoryType type = getRepositoryType();
        return Optional.ofNullable(userCreators.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unknown repository type: " + type))
                .apply(validator);
    }

    /**
     * creates a paging repository for users (supported only for DATABASE type)
     *
     * @param validator the validator for users
     * @return the paging user repository
     * @throws UnsupportedOperationException if repository type does not support pagination
     */
    public PagingRepository<Long, User> createPagingUserRepository(Validator<User> validator) {
        Repository<Long, User> repo = createUserRepository(validator);
        if (repo instanceof PagingRepository) {
            @SuppressWarnings("unchecked")
            PagingRepository<Long, User> pagingRepo = (PagingRepository<Long, User>) repo;
            return pagingRepo;
        }
        throw new UnsupportedOperationException("Pagination is only supported for DATABASE repository");
    }

    /**
     - creates a repository for friendships
     - @param validator the validator for friendships
     - @return the friendship repository
     - @throws IllegalArgumentException if repository type is unknown
     */
    public Repository<Tuple<Long, Long>, Friendship> createFriendshipRepository(Validator<Friendship> validator) {
        RepositoryType type = getRepositoryType();
        return Optional.ofNullable(friendshipCreators.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unknown repository type: " + type))
                .apply(validator);
    }

    /**
     * creates a database repository for friendships supporting paginated friend queries
     *
     * @param validator the validator for friendships
     * @return the database friendship repository
     * @throws UnsupportedOperationException if repository type is not DATABASE
     */
    public FriendshipDbRepository createFriendshipDbRepository(Validator<Friendship> validator) {
        Repository<Tuple<Long, Long>, Friendship> repo = createFriendshipRepository(validator);
        if (repo instanceof FriendshipDbRepository) {
            return (FriendshipDbRepository) repo;
        }
        throw new UnsupportedOperationException("Pagination is only supported for DATABASE repository");
    }

    private RepositoryType getRepositoryType() {
        String typeString = config.getProperty("repository.type", "MEMORY");
        return RepositoryType.valueOf(typeString.trim().toUpperCase());
    }
}