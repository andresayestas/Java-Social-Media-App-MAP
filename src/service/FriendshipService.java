package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.FriendshipDbRepository;
import repository.Repository;
import util.paging.Page;
import util.paging.Pageable;
import validators.ValidationException;

import java.util.stream.StreamSupport;

/**
 - Service handling business logic for friendships
 */
public class FriendshipService {

    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final Repository<Long, User> userRepository;

    /**
     - creates a friendship service with friendship and user repositories
     - @param friendshipRepository the repository for friendships
     - @param userRepository the repository for users
     */
    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> friendshipRepository, Repository<Long, User> userRepository){
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    /**
     - adds a friendship between two users
     - @param idUser1 id of the first user
     - @param idUser2 id of the second user
     - @throws ValidationException if one user does not exist or they are already friends
     */
    public void addFriendship(Long idUser1, Long idUser2) throws ValidationException{

        User user1 = userRepository.findOne(idUser1)
                .orElseThrow(() -> new ValidationException("Primul user nu exista"));

        User user2 = userRepository.findOne(idUser2)
                .orElseThrow(() -> new ValidationException("Al doilea user nu exista"));

        boolean alreadyFriends = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .anyMatch(f -> {
                    Long u1 = f.getFirstFriend().getId();
                    Long u2 = f.getSecondFriend().getId();
                    return (u1.equals(idUser1) && u2.equals(idUser2)) || (u1.equals(idUser2) && u2.equals(idUser1));
                });

        if (alreadyFriends) {
            throw new ValidationException("Users sunt deja amici");
        }

        Friendship newFriendship = new Friendship(user1, user2);

        friendshipRepository.save(newFriendship).ifPresent(r -> {
            throw new ValidationException("Error cand se a incerca sa stocheaza prietenie");
        });
    }

    /**
     - gets all friendships
     - @return all friendships in repo
     */
    public Iterable<Friendship> getAllFriendship(){
        return friendshipRepository.findAll();
    }

    /**
     * retrieves a specific page of friends for a given user
     *
     * @param userId   the id of the user whose friends are retrieved
     * @param pageable pagination parameters specifying page number and page size
     * @return a Page containing the User objects of the friends on the requested page and the total friend count
     * @throws IllegalArgumentException if userId or pageable is null, or if pagination parameters are invalid
     * @throws ValidationException if the user does not exist
     * @throws UnsupportedOperationException if the configured repository does not support pagination
     */
    public Page<User> getFriendsOnPage(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Page number must be >= 0 and page size must be > 0");
        }
        if (userRepository.findOne(userId).isEmpty()) {
            throw new ValidationException("Utilizatorul cu id-ul " + userId + " nu exista");
        }
        if (!(friendshipRepository instanceof FriendshipDbRepository)) {
            throw new UnsupportedOperationException("Pagination is only supported for DATABASE repository");
        }

        return ((FriendshipDbRepository) friendshipRepository).findFriendsOnPage(userId, pageable);
    }

    /**
     - deletes a friendship between two users
     - @param idUser1 id of the first user
     - @param idUser2 id of the second user
     - @throws IllegalArgumentException if any id is null
     - @throws ValidationException if the friendship is not found
     */
    public void deleteFriendship(Long idUser1, Long idUser2) throws ValidationException {
        if (idUser1 == null || idUser2 == null) {
            throw new IllegalArgumentException("Id-ul nu poate fi nul");
        }
        Tuple<Long, Long> id = new Tuple<>(Math.min(idUser1, idUser2), Math.max(idUser1, idUser2));
        friendshipRepository.delete(id)
                .orElseThrow(() -> new ValidationException("Nu s-a gasit nicio prietenie intre acesti utilizatori"));
    }
}