package repository;

import domain.Entity;
import validators.ValidationException;
import validators.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Repository that keeps entities in memory
 * @param <ID> type of entity id
 * @param <E> type of entity
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private Validator<E> validator;
    protected Map<ID, E> entities;

    /**
     * creates an in-memory repository
     * @param validator the validator for entities
     */
    public InMemoryRepository(Validator<E> validator){
        this.validator = validator;
        this.entities = new HashMap<>();
    }

    /**
     * finds one entity by id
     * @param id the id of the entity
     * @return Optional with the entity or empty if not found
     * @throws IllegalArgumentException if id is null
     */
    @Override
    public Optional<E> findOne(ID id){
        if (id == null){
            throw new IllegalArgumentException("Id-ul nu poate fi nul");
        }
        return Optional.ofNullable(entities.get(id));
    }

    /**
     * gets all entities
     * @return all entities from memory
     */
    @Override
    public Iterable<E> findAll(){
        return entities.values();
    }

    /**
     * saves a new entity in memory and generates a Long id if it is null
     * @param entity the entity to save
     * @return Optional.empty() if saved, or Optional with the entity if id exists
     * @throws IllegalArgumentException if entity is null
     * @throws ValidationException if entity is not valid
     */
    @Override
    public Optional<E> save(E entity) throws ValidationException{
        if (entity == null){
            throw new IllegalArgumentException("Entity nu poate fi nula");
        }

        validator.validate(entity);

        if (entity.getId() == null) {
            long nextId = entities.keySet().stream()
                    .filter(k -> k instanceof Long)
                    .mapToLong(k -> (Long) k)
                    .max()
                    .orElse(0L) + 1L;
            @SuppressWarnings("unchecked")
            ID generatedId = (ID) Long.valueOf(nextId);
            entity.setId(generatedId);
        }

        if (entities.containsKey(entity.getId())){
            return Optional.of(entity);
        } else {
            entities.put(entity.getId(), entity);
            return Optional.empty();
        }
    }

    /**
     * deletes an entity by id
     * @param id the id to delete
     * @return Optional with the removed entity or empty if not found
     * @throws IllegalArgumentException if id is null
     */
    @Override
    public Optional<E> delete(ID id){
        if (id == null){
            throw new IllegalArgumentException("Id-ul nu poate fi nul");
        }
        return Optional.ofNullable(entities.remove(id));
    }

    /**
     * updates an existing entity
     * @param entity the entity to update
     * @return Optional.empty() if updated, or Optional with the entity if not found
     * @throws IllegalArgumentException if entity is null
     * @throws ValidationException if entity is not valid
     */
    @Override
    public Optional<E> update(E entity) throws ValidationException{
        if (entity == null){
            throw new IllegalArgumentException("Entity nu poate fi nula");
        }
        validator.validate(entity);

        if(entities.containsKey(entity.getId())){
            entities.put(entity.getId(), entity);
            return Optional.empty();
        }
        return Optional.of(entity);
    }

}