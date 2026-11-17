package repository;

/**
 * Custom exception for repository and database errors
 */

public class RepositoryException extends RuntimeException {

    /**
     * creates a repository exception with a message and cause
     * @param message the error message
     * @param cause the original exception
     */

    public RepositoryException(String message, Throwable cause){
        super(message, cause);
    }
}
