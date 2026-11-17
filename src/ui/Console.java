package ui;

import domain.User;
import repository.RepositoryException;
import service.CommunityService;
import service.FriendshipService;
import service.UserService;
import validators.ValidationException;

import java.util.List;
import java.util.Scanner;
import java.util.stream.StreamSupport;

/**
 * Console user interface for the social network
 */

public class Console {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final CommunityService communityService;
    private final Scanner scanner;

    /**
     * creates the console interface with required services
     * @param userService service for user operations
     * @param friendshipService service for friendship operations
     * @param communityService service for community operations
     */
    public Console(UserService userService, FriendshipService friendshipService, CommunityService communityService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.communityService = communityService;
        this.scanner = new Scanner(System.in);
    }

    private void printMeniu(){
        System.out.println("\n Red social");
        System.out.println("1- Adauga users");
        System.out.println("2- Sterge users");
        System.out.println("3- Afiseaza users");
        System.out.println("4- Adauga prietenie");
        System.out.println("5- Sterge prietenie");
        System.out.println("6- Afiseaza prietenie");
        System.out.println("7- Afiseaza numarul de comunitate");
        System.out.println("8- Afiseaza comunitatea cea mai sociabila");
        System.out.println("9- Actualizeaza user");
        System.out.println("0- Exit");
        System.out.print("Alege optiune = ");
    }

    /**
     * runs the main menu loop
     */
    public void run(){
        boolean meniu = true;
        while(meniu) {
            printMeniu();

            try{
                int option = scanner.nextInt();
                scanner.nextLine();

                switch(option){
                    case 1:
                        addUser();
                        break;
                    case 2:
                        deleteUser();
                        break;
                    case 3:
                        showUsers();
                        break;
                    case 4:
                        addFriendship();
                        break;
                    case 5:
                        deleteFriendship();
                        break;
                    case 6:
                        showFriendships();
                        break;
                    case 7:
                        showCommunityCount();
                        break;
                    case 8:
                        showBiggestCommunity();
                        break;
                    case 9:
                        updateUser();
                        break;
                    case 10:
                        cautaUserCombinat();
                        break;
                    case 11:
                        showValidUsers();
                        break;
                    case 0:
                        meniu = false;
                        System.out.println("pa pa");
                        break;
                    default:
                        System.out.println("Optiune invalida");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("\n Eroare: Trebuie sa introduci un numar valid, nu text!");
                scanner.nextLine();
            } catch (ValidationException | IllegalArgumentException e){
                System.out.println("\n Exista eroare: " + e.getMessage());
            } catch (RepositoryException e){
                System.out.println("\n Eroare baza de date: " + e.getMessage());
            }
        }
    }

    private void addUser(){
        System.out.print("Prenume: ");
        String firstName = scanner.nextLine();
        System.out.print("Nume: ");
        String lastName = scanner.nextLine();

        User savedUser = userService.addUser(firstName, lastName);
        System.out.println("User a fost adaugat cu id: " + savedUser.getId());
    }

    private void deleteUser(){
        System.out.print("Id-ul de sters: ");
        Long id = scanner.nextLong();
        userService.deleteUser(id);
        System.out.println("User a fost sters cu succes");
    }

    private void showUsers(){
        System.out.println("\n Lista de users");

        StreamSupport.stream(userService.getAllUsers().spliterator(), false)
                .map(user -> "Id " + user.getId() + " Nume : " + user.getFirstName() + " " + user.getLastName())
                .forEach(System.out::println);
    }

    private void addFriendship(){
        System.out.print("ID de primul user: ");
        Long id1 = scanner.nextLong();
        System.out.print("ID de al doilea user: ");
        Long id2 = scanner.nextLong();
        friendshipService.addFriendship(id1, id2);
        System.out.println("Prietenie adaugata cu succes");
    }

    private void deleteFriendship(){
        System.out.print("ID de primul user: ");
        Long id1 = scanner.nextLong();
        System.out.print("ID de al doilea user: ");
        Long id2 = scanner.nextLong();
        friendshipService.deleteFriendship(id1, id2);
        System.out.println("Prietenie a fost stersa");
    }

    private void showFriendships(){
        System.out.println("\n Lista de prietenii");

        StreamSupport.stream(friendshipService.getAllFriendship().spliterator(), false)
                .map(f -> "Prietenie id: " + f.getId() + ", user 1: " + f.getFirstFriend().getId() + ", user 2: " + f.getSecondFriend().getId())
                .forEach(System.out::println);

    }

    private void showCommunityCount(){
        int count = communityService.getNumberOfCommunities();
        System.out.println("\n Numar total de comunitati: " + count);
    }

    private void showBiggestCommunity(){
        List<Long> big = communityService.getMostSociableCommunity();
        System.out.println("\n Comunitatea cea mai sociabila este formata de Id: " + big);
    }

    private void updateUser() {
        System.out.print("Id user de actualizat: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Noul prenume: ");
        String firstName = scanner.nextLine();

        System.out.print("Noul nume: ");
        String lastName = scanner.nextLine();

        userService.updateUser(id, firstName, lastName);
        System.out.println("User a fost actualizat");
    }

    private void cautaUserCombinat(){
        System.out.print("Introdu prenumele cautat (ex: Andres): ");
        String firstName = scanner.nextLine();
        System.out.print("Introdu prima litera din numele de familie (Ex:P): ");
        String prefix = scanner.nextLine();

        System.out.println("\n Rezultate cautare combinata: ");
        List<User> results = userService.getFilteredUsersCombinat(firstName, prefix);

        if(results.isEmpty()) {
            System.out.println("Nu s-au gasit utilizatori.");
        } else {
            results.forEach(user -> System.out.println("Gasit: " + user.getFirstName() + " " + user.getLastName()));
        }

    }

    private void showValidUsers(){
        System.out.println("\n Utilizatori cu nume valide:");
        List<domain.User> validUsers = userService.getValidUsers();

        if (validUsers.isEmpty()) {
            System.out.println("Nu s-au gasit utilizatori valizi");
        } else {
            validUsers.forEach(user ->
                    System.out.println("Id: " + user.getId() + " - " + user.getFirstName() + " " + user.getLastName()));
        }

    }
}