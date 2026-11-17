package validators;

/**
 * FuntionalInterface to validate entities
 * @param <T> the entity type
*/

@FunctionalInterface
public interface Validator<T>{

    void validate(T entity) throws ValidationException;

    default Validator<T> andThen(Validator<T> other){
        return (T entity) -> {
          this.validate(entity);
          other.validate(entity);
        };
    }
}