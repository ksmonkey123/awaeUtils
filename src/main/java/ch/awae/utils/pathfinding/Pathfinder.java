package ch.awae.utils.pathfinding;

import java.util.List;

/**
 * Base Interface for pathfinding algorithm implementations
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 * 
 * @param <V>
 *            the vertex type supported by the pathfinder
 */
public interface Pathfinder<V> {

    /**
     * Finds a path for the given starting point and the given destination
     * 
     * @param from
     *            the starting vertex
     * @param to
     *            the destination vertex
     * @return an ordered list containing the path
     */
    default List<V> findPath(V from, V to) {
        return execute(from, to).getPath();
    }

    PathfindingResult<V> execute(V from, V to);

    /**
     * set a timeout for the pathfinding.
     * 
     * when the timeout has been exceeded the pathfinder implementation must
     * terminate and return {@code null}.
     * 
     * By default timeouts are not supported and an
     * {@link UnsupportedOperationException} is thrown.
     * 
     * @param timeout
     *            the timeout in milliseconds. A value of {@code 0} or less
     *            disables timeout.
     */
    default void setTimeout(long timeout) {
        throw new UnsupportedOperationException("timeout not supported");
    }

    /**
     * gets the currently set timeout value or {@code 0} if timeouts are
     * disabled.
     * 
     * By default timeouts are not supported and an
     * {@link UnsupportedOperationException} is thrown.
     * 
     * @return the current timeout in milliseconds.
     */
    default long getTimeout() {
        throw new UnsupportedOperationException("timeout not supported");
    }

}
