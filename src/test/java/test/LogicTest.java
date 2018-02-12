package test;

import org.junit.Test;

import ch.awae.utils.logic.Logic;

public class LogicTest {

    @Test
    public void testAnd() {
        Logic a = Logic.TRUE;
        Logic b = Logic.TRUE;

        Logic.and(a, b);
    }

}
