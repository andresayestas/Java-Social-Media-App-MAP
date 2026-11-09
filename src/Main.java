import domain.Friendship;
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

public class Main {

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

    public static void main(String[] args) {

        Properties config = incarcaConfiguratie();

        RepositoryFactory factory = new RepositoryFactory(config);


        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long, User> userRepository = factory.createUserRepository(userValidator);
        Repository<Long, Friendship> friendshipRepository = factory.createFriendshipRepository(friendshipValidator);

        UserService userService = new UserService(userRepository, friendshipRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipRepository, userRepository);
        CommunityService communityService = new CommunityService(userRepository, friendshipRepository);

        Console console = new Console(userService, friendshipService, communityService);

        console.run();
    }
}