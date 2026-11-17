package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.Repository;
import validators.ValidationException;

import java.util.stream.StreamSupport;

/**
 * Service handling business logic for friendships
 */
public class FriendshipService {

    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final Repository<Long, User> userRepository;

    /**
     * creates a friendship service with friendship and user repositories
     * @param friendshipRepository the repository for friendships
     * @param userRepository the repository for users
     */
    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> friendshipRepository, Repository<Long, User> userRepository){
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    /**
     * adds a friendship between two users
     * @param idUser1 id of the first user
     * @param idUser2 id of the second user
     * @throws ValidationException if one user does not exist or they are already friends
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
     * gets all friendships
     * @return all friendships in repo
     */
    public Iterable<Friendship> getAllFriendship(){
        return friendshipRepository.findAll();
    }

    /**
     * deletes a friendship between two users
     * @param idUser1 id of the first user
     * @param idUser2 id of the second user
     * @throws IllegalArgumentException if any id is null
     * @throws ValidationException if the friendship is not found
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