package org.jqassistant.plugin.spring.test.set.test.spring_batch;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.context.SpringBatchTest;

@SpringBatchTest
public class SpringBatchTestClassWithNestedTestsAndWithoutTestMethod {

    @Nested
    public class NestedTestWithTestcase {
        @Test
        public void exampleTestCase(){}
    }

    @Nested
    public class NestedTestWithoutTestcase {
        public void exampleTestCase(){}
    }
}
