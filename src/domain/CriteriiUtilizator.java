package domain;

import java.util.function.Predicate;

public class CriteriiUtilizator {

    /**
     * checks if the user has a specific first name
     * @param name the first name to search
     * @return a predicate checking the first time
     */

    public static Predicate<User> hasFirstName(String name){
        return user -> user.getFirstName().equalsIgnoreCase(name);
    }


    /**
     * Check if the user's last name starts with the given prefix
     * @param prefix is the prefix we will check
     * @return a predicate for filtering by last name prefix
     */
    public static Predicate<User> lastNameStartsWith(String prefix){
        return user -> user.getLastName().startsWith(prefix);
    }

    /**
     * Checks if the user has a valid name (at least 3 characters).
     *
     * @param user the user entity to check
     * @return true if the first name is not null and has 3 or more characters, false otherwise
     */
    public static boolean areNumeValid(User user){
        return user.getFirstName() != null && user.getLastName().length() >= 3;
    }
}
