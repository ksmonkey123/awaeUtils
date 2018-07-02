package ch.awae.utils.pathfinding;

/**
 * Pathfinder based on the Dijkstra algorithm.
 * 
 * This algorithm is based of the A-Star algorithm and uses a fixed distance
 * heuristic of {@code 0}.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 *
 * @param <V>
 *            the vertex type of the pathfinder
 * @see AStarPathfinder
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
    public PathfindingResult<V> execute(V from, V to) {
        return backer.execute(from, to);
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
