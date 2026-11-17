package domain;
import java.util.Objects;

/**
 * Class for a pair of two objects
 * @param <E1> type of first element
 * @param <E2> type of second element
 */
public class Tuple<E1, E2> {
    private E1 left;
    private E2 right;

    /**
     * creates a tuple with two elements
     * @param left the first element
     * @param right the second element
     */
    public Tuple(E1 left, E2 right){
        this.left = left;
        this.right = right;
    }

    /**
     * gets the first element
     * @return first element
     */
    public E1 getLeft(){
        return left;
    }

    /**
     * gets the second element
     * @return second element
     */
    public E2 getRight(){
        return right;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Tuple)) return false;
        Tuple<?, ?> that = (Tuple<?, ?>) o;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode(){
        return Objects.hash(left, right);
    }

    @Override
    public String toString(){
        return left + "," + right;
    }
}