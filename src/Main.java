import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.Repository;
import repository.RepositoryFactory;
import service.CommunityService;
import service.FriendshipService;
import service.UserService;
import ui.Console;
import validators.FriendshipValidator;
import validators.UserValidator;
import validators.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 - Main class of the application
 */
public class Main {

    /**
     - loads configuration from config.properties
     - @return properties object with configuration
     */
    private static Properties incarcaConfiguratie(){
        Properties props = new Properties();

        try (InputStream is = Main.class.getResourceAsStream("/resources/config.properties")){
            if (is == null){
                throw new RuntimeException("Fisierul config.properties nu a fost gasit!");

            }
            props.load(is);
        } catch (IOException e){
            throw new RuntimeException("Error cand se a citi fisierul de configurare");
        }
        return props;
    }

    /**
     - starts the social network application
     - @param args command line arguments
     */
    public static void main(String[] args) {

        Properties config = incarcaConfiguratie();

        RepositoryFactory factory = new RepositoryFactory(config);


        Validator<User> baseUserValidator = new UserValidator();

        Validator<User> userValidator = baseUserValidator.andThen(u -> {
            if (u.getFirstName().length() < 2 || u.getLastName().length() < 2){
                throw new validators.ValidationException("Nume si prenume trebuie sa aiba cel putin 2 caractere");
            }
        });



        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long, User> userRepository = factory.createUserRepository(userValidator);
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = factory.createFriendshipRepository(friendshipValidator);

        UserService userService = new UserService(userRepository, friendshipRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipRepository, userRepository);
        CommunityService communityService = new CommunityService(userRepository, friendshipRepository);

        int pageSize = Integer.parseInt(config.getProperty("page.size", "3").trim());
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }

        Console console = new Console(userService, friendshipService, communityService, pageSize);

        console.run();
    }
}