package ch.awae.utils.pathfinding;

import java.util.List;

/**
 * Base interface for pathfinding graph vertices
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 *
 * @param <T>
 *            the edge type
 */
public interface Vertex<T extends Vertex<T>> {

    /**
     * An empty double array used as the default 0-dimensional spatial
     * coordinates for any vertex.
     */
    static final double[] EMPTY_SPATIAL_ARRAY = new double[0];

    /**
     * returns a list of all neighbours of the vertex
     * 
     * @return the complete vertex list
     */
    List<T> getNeighbours();

    /**
     * returns the distance to the given neighbour
     * 
     * @param neighbour
     *            the neighbour to get the distance for
     * @return the distance
     * 
     * @throws IllegalArgumentException
     *             if the given neighbour is no neighbour of the vertex
     */
    double getDistance(T neighbour);

    /**
     * provides spatial information about a vertex if applicable.
     * 
     * This information can be used by heuristics-based path finders (e.g. A*).
     * The spatial information can be of arbitrary dimensionality (even
     * 0-dimensional). It is recommended to use a fixed number of dimensions for
     * any implementation.
     * 
     * @return a potentially empty array containing n-dimensional spatial
     *         coordinates of the vertex.
     * 
     * @since awaeUtils 1.0.2
     */
    default double[] getSpatialPosition() {
        return EMPTY_SPATIAL_ARRAY;
    }

}
