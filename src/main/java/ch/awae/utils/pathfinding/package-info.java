/**
 * General Path-Finding library.
 * 
 * The path finding operates on vertices, where each vertex knows all its
 * reachable neighbours and the distance to these neighbours. The path finding
 * algorithms can then walk through these links as required. Since awaeUtils
 * 1.0.2 vertices may also provide n-dimensional spatial information for
 * heuristics-based path finders (e.g. A*).
 * 
 * Currently only a few implementations are provided, custom implementations can
 * however be written as needed. Provided are:
 * <ul>
 * <li>Dijkstra</li>
 * <li>A-Star (using cartesian spatial coordinates as heuristic)</li>
 * </ul>
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
package ch.awae.utils.pathfinding;