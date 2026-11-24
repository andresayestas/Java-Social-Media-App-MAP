package util.paging;

/**
 * Represents a single page of elements along with the total count across all pages
 *
 * @param <E> the type of elements contained in the page
 */
public class Page<E> {
    private final Iterable<E> elementsOnPage;
    private final int totalNumberOfElements;

    /**
     * Creates a new Page containing a subset of elements and the total element count
     *
     * @param elementsOnPage        the elements belonging to the current page
     * @param totalNumberOfElements total number of elements available in the data source
     */
    public Page(Iterable<E> elementsOnPage, int totalNumberOfElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    /**
     * Gets the elements present on this page
     *
     * @return an iterable collection of elements on the page
     */
    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    /**
     * Gets the total number of elements across all pages
     *
     * @return the total number of elements
     */
    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}