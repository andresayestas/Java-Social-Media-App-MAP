package validators;
import domain.Friendship;

public class FriendshipValidator implements Validator<Friendship>{

    @Override
    public void validate(Friendship entity) throws ValidationException{
        if (entity == null){
            throw new ValidationException("Prietenie nu poate fi null");
        }

        if (entity.getFirstFriend() == null || entity.getSecondFriend() == null){
            throw new ValidationException("Users care sunt amici nu pot fi null");
        }

        if (entity.getFirstFriend().getId() == null || entity.getSecondFriend().getId() == null){
            throw new ValidationException("ID-ul utilizator nu poate fi null");
        }

        if (entity.getFirstFriend().getId().equals(entity.getSecondFriend().getId())){
            throw new ValidationException("Un user no pot fi amici cu sine");
        }
    }

}
