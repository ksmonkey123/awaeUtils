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
	List<V> findPath(V from, V to);

}
