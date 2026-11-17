package domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a friendship between two users
 */
public class Friendship extends Entity<Tuple<Long, Long>> {

    private User firstFriend;
    private User secondFriend;
    private LocalDateTime date;

    /**
     * creates a friendship between two users with smaller id first
     * @param firstFriend the first user
     * @param secondFriend the second user
     */
    public Friendship(User firstFriend, User secondFriend) {
        if (firstFriend != null && secondFriend != null
                && firstFriend.getId() != null && secondFriend.getId() != null
                && firstFriend.getId() > secondFriend.getId()) {
            this.firstFriend = secondFriend;
            this.secondFriend = firstFriend;
        } else {
            this.firstFriend = firstFriend;
            this.secondFriend = secondFriend;
        }

        if (this.firstFriend != null && this.secondFriend != null) {
            setId(new Tuple<>(this.firstFriend.getId(), this.secondFriend.getId()));
        }
        this.date = LocalDateTime.now();
    }

    /**
     * gets the first friend
     * @return first friend
     */
    public User getFirstFriend(){
        return firstFriend;
    }

    /**
     * sets the first friend
     * @param firstFriend the new first friend
     */
    public void setFirstFriend(User firstFriend){
        this.firstFriend = firstFriend;
    }

    /**
     * gets the second friend
     * @return second friend
     */
    public User getSecondFriend(){
        return secondFriend;
    }

    /**
     * gets the date of the friendship
     * @return the date when they became friends
     */
    public LocalDateTime getDate(){
        return date;
    }

    /**
     * sets the date of the friendship
     * @param date the new date
     */
    public void setDate(LocalDateTime date){
        this.date = date;
    }

    /**
     * sets the second friend
     * @param secondFriend the new second friend
     */
    public void setSecondFriend(User secondFriend){
        this.secondFriend = secondFriend;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Friendship)) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode(){
        return Objects.hash(getId());
    }
}