package validators;
import domain.User;

public class UserValidator implements Validator<User> {

    @Override
    public void validate(User entity) throws ValidationException{
        if (entity == null){
            throw new ValidationException("User-ul nu poate fi null");
        }

        if (entity.getFirstName() == null || entity.getFirstName().trim().isEmpty()){
            throw new ValidationException("prenume de user no poate fi gol");
        }

        if (entity.getLastName() == null || entity.getLastName().trim().isEmpty()){
            throw new ValidationException("Nume de user no poate fi gol");
        }
    }
}
