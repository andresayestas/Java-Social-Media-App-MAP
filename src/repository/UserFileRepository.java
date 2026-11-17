package repository;
import domain.User;
import validators.Validator;


public class UserFileRepository extends AbstractFileRepository<Long, User> {
    public UserFileRepository(Validator<User> validator, String fileName) {
        super(validator, fileName);
        loadData();
    }

    @Override
    protected User createEntity(String line){
        String[] fields = line.split(";");

        Long id = Long.parseLong(fields[0]);
        String firstName = fields[1];
        String lastName = fields[2];

        User user = new User(firstName, lastName);
        user.setId(id);

        return user;

    }
    @Override
    protected String saveEntity(User entity){
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }
}
