package ch.awae.utils.pathfinding;

import java.util.List;

/**
 * Base interface for pathfinding graph vertices
 * 
 * @author Andreas Wälchli
 * @version 1.1, 2015-05-09
 *
 * @param <T>
 *            the edge type
 */
public interface Vertex<T extends Vertex<T>> {

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

}
