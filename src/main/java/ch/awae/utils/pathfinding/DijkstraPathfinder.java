package ch.awae.utils.pathfinding;

import java.util.List;

/**
 * Pathfinder based on the Dijkstra algorithm.
 * 
 * This algorithm requires the vertices to provide an equals-equivalent hash
 * function, i.e. a hash function where equality of the hash implies actual
 * object equality
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 *
 * @param <V>
 *            the vertex type of the pathfinder
 */
public final class DijkstraPathfinder<V> implements Pathfinder<V> {

    private AStarPathfinder<V> backer;

    public DijkstraPathfinder(GraphDataProvider<V> graph) {
        backer = new AStarPathfinder<>(graph.withHeuristic((a, b) -> 0.0));
    }

    public static <T> DijkstraPathfinder<T> create(GraphDataProvider<T> graph) {
        return new DijkstraPathfinder<>(graph);
    }

    @Override
    public List<V> findPath(V from, V to) {
        return backer.findPath(from, to);
    }
    
    @Override
    public long getTimeout() {
        return backer.getTimeout();
    }
    
    @Override
    public void setTimeout(long timeout) {
        backer.setTimeout(timeout);
    }

}
