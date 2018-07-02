package ch.awae.utils.pathfinding;

import java.util.List;

import ch.awae.utils.functional.Function1;

public class VertexPathfinder<T extends Vertex<T>> implements Pathfinder<T> {

    private Pathfinder<T> backer;

    public VertexPathfinder(Function1<GraphDataProvider<T>, Pathfinder<T>> generator) {
        backer = generator.apply(new GraphDataProvider<T>() {

            @Override
            public Iterable<T> getNeighbours(T vertex) {
                return vertex.getNeighbours();
            }

            @Override
            public double getDistance(T from, T to) {
                return from.getDistance(to);
            }

            @Override
            public double getHeuristicDistance(T from, T to) {
                return getHeuristics(from.getSpatialPosition(), to.getSpatialPosition());
            }

        });
    }

    @Override
    public List<T> findPath(T from, T to) {
        return backer.findPath(from, to);
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

    @Override
    public void setTimeout(long timeout) {
        backer.setTimeout(timeout);
    }

    @Override
    public long getTimeout() {
        return backer.getTimeout();
    }

}
