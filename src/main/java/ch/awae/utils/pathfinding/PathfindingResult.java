package ch.awae.utils.pathfinding;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PathfindingResult<T> {

    public static enum TYPE {
        SUCCESS,
        FAILURE,
        TIMEOUT
    }

    private TYPE type;
    private long searchTime;
    private long searchSteps;
    private List<T> path;

    public boolean isSuccess() {
        return type == TYPE.SUCCESS;
    }

    public boolean isFailure() {
        return type == TYPE.FAILURE;
    }

    public boolean isTimeout() {
        return type == TYPE.TIMEOUT;
    }

    public static <V> PathfindingResult<V> success(long time, long steps, List<V> path) {
        return new PathfindingResult<>(TYPE.SUCCESS, time, steps, path);
    }

    public static <V> PathfindingResult<V> failure(long time, long steps) {
        return new PathfindingResult<>(TYPE.FAILURE, time, steps, null);
    }

    public static <V> PathfindingResult<V> timeout(long time, long steps) {
        return new PathfindingResult<V>(TYPE.TIMEOUT, time, steps, null);
    }

}
