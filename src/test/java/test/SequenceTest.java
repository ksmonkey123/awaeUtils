package test;

import org.junit.Test;

import ch.awae.utils.sequence.Sequence;

public class SequenceTest {

    @Test
    public void test() throws InterruptedException {

        Sequence sequence = Sequence.builder()//
                .step(() -> System.out.println("hi"))//
                .sleep(1000)//
                .step(() -> System.out.println("there"))//
                .loop(2)//
                .loop(3)//
                .step(() -> System.out.println("everyone"))//
                .end()//
                .step(() -> System.out.println("!"))//
                .end()//
                .step(() -> System.out.println("####"))//
                .compile();

        sequence.start();
        sequence.join();

    }

}
