package ch.awae.utils.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.awae.utils.collection.mutable.PriorityQueue;

/**
 * Path Finder based on the A-Star algorithm.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.2
 *
 * @param <V>
 *            the vertex type of the path finder
 */
public final class AStarPathfinder<V> implements Pathfinder<V> {

    private GraphDataProvider<V> graph;
    private long timeout = 0;

    public AStarPathfinder(GraphDataProvider<V> graph) {
        this.graph = graph;
    }

    public static <T> AStarPathfinder<T> create(GraphDataProvider<T> graph) {
        return new AStarPathfinder<>(graph);
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public PathfindingResult<V> execute(V from, V to) {
        long start = System.currentTimeMillis();
        long steps = 0;
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        Map<V, Double> distances = new HashMap<>();
        Map<V, V> backsteps = new HashMap<>();
        PriorityQueue<V> queue = PriorityQueue.minQueue();

        distances.put(from, Double.valueOf(0.0));
        queue.add(from, 0);

        // build global map
        while (!queue.isEmpty()) {
            if (timeout > 0 && System.currentTimeMillis() > (start + timeout))
                return PathfindingResult.timeout(System.currentTimeMillis() - start, steps);
            steps++;
            V vertex = queue.remove();
            if (vertex.equals(to))
                break;
            double distance = distances.get(vertex);
            for (V neighbour : graph.getNeighbours(vertex)) {
                double dist = distance + graph.getDistance(vertex, neighbour);
                if (!distances.containsKey(neighbour) || distances.get(neighbour) > dist) {
                    distances.put(neighbour, dist);
                    backsteps.put(neighbour, vertex);
                    if (queue.contains(neighbour))
                        queue.remove(neighbour);
                    queue.add(neighbour, dist + graph.getHeuristicDistance(neighbour, to));
                }
            }
        }

        // extract path
        List<V> route = new ArrayList<>();

        V step = to;
        while (step != null && !step.equals(from)) {
            route.add(step);
            step = backsteps.get(step);
        }

        return step == null //
                ? PathfindingResult.failure(System.currentTimeMillis() - start, steps)
                : PathfindingResult.success(System.currentTimeMillis() - start, steps, route);

    }

}
