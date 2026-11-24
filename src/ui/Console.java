package ui;

import domain.User;
import repository.RepositoryException;
import service.CommunityService;
import service.FriendshipService;
import service.UserService;
import util.paging.Page;
import util.paging.Pageable;
import validators.ValidationException;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.StreamSupport;

/**
 - Console user interface for the social network
 */

public class Console {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final CommunityService communityService;
    private final int pageSize;
    private final Scanner scanner;

    /**
     - creates the console interface with required services
     - @param userService service for user operations
     - @param friendshipService service for friendship operations
     - @param communityService service for community operations
     - @param pageSize number of elements displayed per page
     */
    public Console(UserService userService, FriendshipService friendshipService, CommunityService communityService, int pageSize) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.communityService = communityService;
        this.pageSize = pageSize;
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
        System.out.println("12- Afiseaza users (paginat)");
        System.out.println("13- Afiseaza prietenii unui user (paginat)");
        System.out.println("0- Exit");
        System.out.print("Alege optiune = ");
    }

    /**
     - runs the main menu loop
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
                    case 12:
                        showUsersPaginated();
                        break;
                    case 13:
                        showUserFriendsPaginated();
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
            } catch (ValidationException | IllegalArgumentException | UnsupportedOperationException e){
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

    /**
     * Displays all users in a paginated interactive view with forward and backward navigation
     */
    private void showUsersPaginated() {
        navigatePages("Lista de users (paginat)", userService::getUsersOnPage);
    }

    /**
     * Prompts for a user ID and displays their friends in a paginated interactive view
     */
    private void showUserFriendsPaginated() {
        System.out.print("ID utilizator: ");
        Long userId = scanner.nextLong();
        scanner.nextLine();

        navigatePages("Prietenii utilizatorului " + userId + " (paginat)",
                pageable -> friendshipService.getFriendsOnPage(userId, pageable));
    }

    /**
     * Handles interactive forward and backward page navigation for a paginated User source
     *
     * @param title       header title displayed above each page
     * @param pageFetcher function that retrieves a Page of users for a given Pageable request
     */
    private void navigatePages(String title, Function<Pageable, Page<User>> pageFetcher) {
        int currentPage = 0;

        while (true) {
            Page<User> page = pageFetcher.apply(new Pageable(currentPage, pageSize));
            int totalElements = page.getTotalNumberOfElements();
            int totalPages = (int) Math.ceil((double) totalElements / pageSize);
            int displayPage = totalPages == 0 ? 0 : currentPage + 1;

            System.out.println("\ntitle");
            List<User> elements = StreamSupport.stream(page.getElementsOnPage().spliterator(), false).toList();

            if (elements.isEmpty()) {
                System.out.println("Nu exista elemente de afisat.");
            } else {
                elements.forEach(user ->
                        System.out.println("Id " + user.getId() + " - " + user.getFirstName() + " " + user.getLastName()));
            }

            System.out.println("Pagina " + displayPage + " din " + totalPages + " (Total elemente: " + totalElements + ")");
            System.out.print("Navigare: [n] Inainte | [p] Inapoi | [x] Iesire = ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "n":
                    if (currentPage + 1 < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("Esti deja pe ultima pagina!");
                    }
                    break;
                case "p":
                    if (currentPage > 0) {
                        currentPage--;
                    } else {
                        System.out.println("Esti deja pe prima pagina!");
                    }
                    break;
                case "x":
                    return;
                default:
                    System.out.println("Optiune invalida! Foloseste n, p sau x.");
            }
        }
    }
}