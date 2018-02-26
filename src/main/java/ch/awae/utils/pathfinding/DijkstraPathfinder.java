package ch.awae.utils.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.awae.utils.collection.mutable.PriorityQueue;

/**
 * Pathfinder based on the Dijkstra algorithm.
 * 
 * This algorithm requires the vertices to provide an equals-equivalent hash
 * function, i.e. a hash function where equality of the hash implies actual
 * object equality
 * 
 * @author Andreas Wälchli
 * @version 1.1, 2015-05-09
 *
 * @param <V>
 *            the vertex type of the pathfinder
 */
public final class DijkstraPathfinder<V extends Vertex<V>> implements Pathfinder<V> {

    @Override
    public List<V> findPath(V from, V to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        Map<V, Double> distances = new HashMap<>();
        Map<V, V> backsteps = new HashMap<>();
        PriorityQueue<V> queue = PriorityQueue.minQueue();

        distances.put(from, Double.valueOf(0.0));
        queue.add(from, 0);

        // build global map
        while (!queue.isEmpty()) {
            V vertex = queue.element();
            double distance = distances.get(vertex);
            for (V neighbour : vertex.getNeighbours()) {
                double dist = distance + vertex.getDistance(neighbour);
                if (!distances.containsKey(neighbour) || distances.get(neighbour) > dist) {
                    distances.put(neighbour, dist);
                    backsteps.put(neighbour, vertex);
                    if (!queue.contains(neighbour))
                        queue.add(neighbour, dist);
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
}
