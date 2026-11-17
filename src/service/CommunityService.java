package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service class for analyzing communities in the network
 */
public class CommunityService {

    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;

    /**
     * creates a community service with user and friendship repositories
     * @param userRepository the repository for users
     * @param friendshipRepository the repository for friendships
     */
    public CommunityService(Repository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship> friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    private Map<Long, List<Long>> createAdjacencyList() {
        Map<Long, List<Long>> adjacencyList = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(User::getId, u -> new ArrayList<>()));

        StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .forEach(f -> {
                    Long u1 = f.getFirstFriend().getId();
                    Long u2 = f.getSecondFriend().getId();

                    if (adjacencyList.containsKey(u1) && adjacencyList.containsKey(u2)) {
                        adjacencyList.get(u1).add(u2);
                        adjacencyList.get(u2).add(u1);
                    }
                });
        return adjacencyList;
    }

    private void dfs(Long currentUserId, Map<Long, List<Long>> adjacencyList, Set<Long> visited, List<Long> currentCommunity) {
        visited.add(currentUserId);
        currentCommunity.add(currentUserId);

        adjacencyList.get(currentUserId).stream()
                .filter(neighborId -> !visited.contains(neighborId))
                .forEach(neighborId -> dfs(neighborId, adjacencyList, visited, currentCommunity));
    }

    /**
     * gets all communities in the network
     * @return a list of all communities
     */
    public List<List<Long>> getAllCommunities() {
        Map<Long, List<Long>> adjacencyList = createAdjacencyList();
        Set<Long> visited = new HashSet<>();
        List<List<Long>> communityList = new ArrayList<>();

        StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(User::getId)
                .filter(userId -> !visited.contains(userId))
                .forEach(userId -> {
                    List<Long> currentCommunity = new ArrayList<>();
                    dfs(userId, adjacencyList, visited, currentCommunity);
                    communityList.add(currentCommunity);
                });
        return communityList;
    }

    /**
     * gets the total number of communities
     * @return the number of communities
     */
    public int getNumberOfCommunities() {
        return getAllCommunities().size();
    }

    /**
     * gets the biggest community
     * @return a list with the ids of the biggest community
     */
    public List<Long> getMostSociableCommunity() {
        return getAllCommunities().stream()
                .max(Comparator.comparingInt(List::size))
                .orElseGet(ArrayList::new);
    }
}