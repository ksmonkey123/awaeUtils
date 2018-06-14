package ch.awae.utils.pathfinding;

import ch.awae.utils.functional.Function2;

public interface GraphDataProvider<T> {

    /**
     * Looks up and provides the vertices directly reachable from the given
     * vertex.
     */
    Iterable<T> getNeighbours(T vertex);

    /**
     * Provides the distance between two vertices. If they are neighbours, this
     * must provide the exact distance. Otherwise a heuristic distance is
     * acceptable as well.
     * 
     * @param from
     * @param to
     * @return
     */
    double getDistance(T from, T to);

    /**
     * Provides a heuristic distance between two vertices. By default this
     * forwards to {@link #getDistance(Object, Object)}.
     */
    default double getHeuristicDistance(T from, T to) {
        return getDistance(from, to);
    }

    default GraphDataProvider<T> withHeuristic(Function2<T, T, Double> heuristic) {
        GraphDataProvider<T> self = this;
        return new GraphDataProvider<T>() {

            @Override
            public Iterable<T> getNeighbours(T vertex) {
                return self.getNeighbours(vertex);
            }

            @Override
            public double getDistance(T from, T to) {
                return self.getDistance(from, to);
            }

            @Override
            public double getHeuristicDistance(T from, T to) {
                return heuristic.apply(from, to);
            }

        };
    }

}
