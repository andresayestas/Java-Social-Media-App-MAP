package util.paging;

/**
 * Encapsulates pagination parameters (page index and page size)
 */
public class Pageable {
    private final int pageNumber;
    private final int pageSize;

    /**
     * Creates a new Pageable instance with the specified page number and size
     *
     * @param pageNumber zero-based index of the requested page (must be >= 0)
     * @param pageSize   maximum number of elements per page (must be > 0)
     * @throws IllegalArgumentException if pageNumber < 0 or pageSize <= 0
     */
    public Pageable(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    /**
     * Gets the zero-based page number
     *
     * @return the page number
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Gets the maximum number of elements per page
     *
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }
}