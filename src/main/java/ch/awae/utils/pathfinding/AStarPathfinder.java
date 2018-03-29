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
 * This algorithm uses the Cartesian distance between the spatial position of
 * vertices as a distance heuristic. This Cartesian distance supports any
 * arbitrary number of dimensions. If not all vertices provide spatial positions
 * of the same dimension the smaller <em>vector</em> is internally expanded to
 * match the larger one. This expansion implies 0-values for all missing
 * dimensions.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.2
 *
 * @param <V>
 *            the vertex type of the path finder
 */
public final class AStarPathfinder<V extends Vertex<V>> implements Pathfinder<V> {

    @Override
    public List<V> findPath(V from, V to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        double[] targetPosition = to.getSpatialPosition();

        Map<V, Double> distances = new HashMap<>();
        Map<V, V> backsteps = new HashMap<>();
        PriorityQueue<V> queue = PriorityQueue.minQueue();

        distances.put(from, Double.valueOf(0.0));
        queue.add(from, 0);

        // build global map
        while (!queue.isEmpty()) {
            V vertex = queue.remove();
            double distance = distances.get(vertex);
            for (V neighbour : vertex.getNeighbours()) {
                double dist = distance + vertex.getDistance(neighbour);
                if (!distances.containsKey(neighbour) || distances.get(neighbour) > dist) {
                    distances.put(neighbour, dist);
                    backsteps.put(neighbour, vertex);
                    if (queue.contains(neighbour))
                        queue.remove(neighbour);
                    queue.add(neighbour, dist + getHeuristics(from.getSpatialPosition(), targetPosition));
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

        return step == null ? null : route;

    }

    private double getHeuristics(double[] from, double[] to) {
        int fromLength = from.length;
        int toLength = to.length;
        int longest = Math.max(fromLength, toLength);
        double acc = 0.0;
        for (int i = 0; i < longest; i++) {
            double delta = (i < fromLength ? from[i] : 0) - (i < toLength ? to[i] : 0);
            acc += delta * delta;
        }
        return Math.sqrt(acc);
    }

}
