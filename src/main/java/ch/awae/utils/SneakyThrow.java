package ch.awae.utils;

import lombok.SneakyThrows;

public class SneakyThrow {

    @SneakyThrows(Throwable.class)
    public static void doThrow(Throwable t) {
        throw t;
    }

}
