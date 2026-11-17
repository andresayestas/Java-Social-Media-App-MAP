package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import validators.Validator;

import java.time.LocalDateTime;

/**
 * File repository for friendships
 */
public class FriendshipFileRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {

    /**
     * creates a file repository for friendships
     * @param validator the validator for friendships
     * @param fileName the path of the file
     */
    public FriendshipFileRepository(Validator<Friendship> validator, String fileName) {
        super(validator, fileName);
        loadData();
    }

    /**
     * creates a friendship entity from a line of text
     * @param line line from the file
     * @return the friendship object
     */
    @Override
    protected Friendship createEntity(String line) {
        String[] fields = line.split(";");

        Long idUser1 = Long.parseLong(fields[0]);
        Long idUser2 = Long.parseLong(fields[1]);
        LocalDateTime date = LocalDateTime.parse(fields[2]);

        User user1 = new User("..", "..");
        user1.setId(idUser1);

        User user2 = new User("..", "..");
        user2.setId(idUser2);

        Friendship friendship = new Friendship(user1, user2);
        friendship.setDate(date);

        return friendship;
    }

    /**
     * converts a friendship entity into a line of text
     * @param entity the friendship to save
     * @return string line for the file
     */
    @Override
    protected String saveEntity(Friendship entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + entity.getDate();
    }
}