package org.jqassistant.plugin.spring.test.set.test.spring_batch;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.context.SpringBatchTest;

@SpringBatchTest
public class SpringBatchTestClassWithNestedTestsAndTestMethod {

    @Test
    public void test() {}

    @Nested
    public class NestedTest {
        @Test
        public void test() {}
    }
}
