package service;

import domain.CriteriiUtilizator;
import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.PagingRepository;
import repository.Repository;
import util.paging.Page;
import util.paging.Pageable;
import validators.ValidationException;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 - Service handling business logic for users
 */
public class UserService {

    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;

    /**
     - creates a user service with user and friendship repositories
     - @param userRepository the repository for users
     - @param friendshipRepository the repository for friendships
     */
    public UserService(Repository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship> friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     - adds a new user and returns it with the generated id
     - @param firstName the first name of the user
     - @param lastName the last name of the user
     - @return the saved user with its generated id
     - @throws ValidationException if user is invalid or already exists
     */
    public User addUser(String firstName, String lastName) throws ValidationException{
        User newUser = new User(firstName, lastName);

        userRepository.save(newUser).ifPresent(r -> {
            throw new ValidationException("Exista deja un user cu id asta");
        });

        return newUser;
    }

    /**
     - gets all users
     - @return all users in repo
     */
    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * retrieves a specific page of users
     *
     * @param pageable pagination parameters specifying page number and page size
     * @return a Page containing the users on the requested page and the total user count
     * @throws IllegalArgumentException if pageable is null, pageNumber < 0, or pageSize <= 0
     * @throws UnsupportedOperationException if the configured repository does not support pagination
     */
    public Page<User> getUsersOnPage(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Page number must be >= 0 and page size must be > 0");
        }
        if (!(userRepository instanceof PagingRepository)) {
            throw new UnsupportedOperationException("Pagination is only supported for DATABASE repository");
        }

        @SuppressWarnings("unchecked")
        PagingRepository<Long, User> pagingUserRepository = (PagingRepository<Long, User>) userRepository;
        return pagingUserRepository.findAllOnPage(pageable);
    }

    /**
     - deletes a user and their friendships
     - @param id the id to delete
     - @throws ValidationException if user is not found
     */
    public void deleteUser(Long id) throws ValidationException {
        userRepository.delete(id)
                .orElseThrow(() -> new ValidationException("Nu s-a gasit niciun user cu id-ul acesta"));

        List<Tuple<Long, Long>> friendshipIdsToDelete = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(f -> f.getFirstFriend().getId().equals(id) || f.getSecondFriend().getId().equals(id))
                .map(Friendship::getId)
                .collect(Collectors.toList());

        friendshipIdsToDelete.forEach(friendshipRepository::delete);
    }

    /**
     - updates an existing user
     - @param id id of the user to update
     - @param newFirstName the new first name
     - @param newLastName the new last name
     - @throws ValidationException if user not found
     */
    public void updateUser(Long id, String newFirstName, String newLastName) throws ValidationException {
        User updatedUser = new User(newFirstName, newLastName);
        updatedUser.setId(id);

        userRepository.update(updatedUser).ifPresent(r -> {
            throw new ValidationException("Nu s-a putut actualiza, userul cu id-ul " + id + " nu exista");
        });
    }

    /**
     - gets valid users based on the static criterion
     - @return a list of users with valid names
     */
    public List<User> getValidUsers() {
        return userRepository.findAll(CriteriiUtilizator::areNumeValid);
    }

    /**
     - filters users using combined predicates
     - @param firstName the exact first name to search for
     - @param prefix the prefix of the last name to match
     - @return a list of users matching both criteria
     */
    public List<User> getFilteredUsersCombinat(String firstName, String prefix){
        Predicate<User> byFirstName = domain.CriteriiUtilizator.hasFirstName(firstName);
        Predicate<User> byPrefix = domain.CriteriiUtilizator.lastNameStartsWith(prefix);

        return userRepository.findAll(byFirstName.and(byPrefix));
    }

}