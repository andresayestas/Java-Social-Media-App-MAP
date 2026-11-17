package repository;
import domain.Entity;
import validators.ValidationException;

import java.util.Optional;
import java.util.stream.StreamSupport;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Interface for repository
 * @param <ID> id type
 * @param <E> entity type
 */
public interface Repository<ID, E extends Entity<ID>> {

    /**
     *  finds one entity by id
     * @param id is the id to find
     * @return IllegalArgumentException if id is null
     */
    Optional<E> findOne(ID id);

    /**
     * gets all entities
     * @return
     */
    Iterable<E> findAll();

    /**
     *
      * @param entity the entity to save
     * @return null if saved, or entity if it exists
     * @throws ValidationException if entity is not valid
     * @throws IllegalArgumentException if entity is null
     */

    Optional<E> save(E entity) throws ValidationException;

    /**
     * deletes an entity by id
     * @param id - id to delete
     * @return the deleted entity or null if it didnt find it
     * @throws  IllegalArgumentException if id is null
     */
    Optional<E> delete(ID id);

    /**
     * update an entity
     * @param entity - entity to update
     * @return null if updated, or the entity if not found
     * @throws ValidationException if entity is not valid
     * @throws IllegalArgumentException if entity is null
     */
    Optional<E> update(E entity) throws ValidationException;


    /**
     * returns a list of filtered entities
     * @param filter - the condition to check
     * @return a list of entities
     */
    default List<E> findAll(Predicate<E> filter){
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(filter)
                .collect(Collectors.toList());
    }


}
