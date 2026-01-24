package io.gitlab.kalmemarq.bso;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BsoTest {
    @Test
    void test1() {
        Assertions.assertTrue(BsoUtils.returnTrue());
    }

    @Test
    void test2() {
        Assertions.assertTrue(true);
    }
}
