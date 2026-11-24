package repository;

import domain.Entity;
import util.paging.Page;
import util.paging.Pageable;

/**
 * Generic repository interface supporting paginated queries
 *
 * @param <ID> the type of the entity identifier
 * @param <E>  the type of the entity
 */
public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {

    /**
     * Retrieves a specific page of entities
     *
     * @param pageable pagination parameters specifying page number and size
     * @return a Page containing the entities for the requested page and the total count
     */
    Page<E> findAllOnPage(Pageable pageable);
}