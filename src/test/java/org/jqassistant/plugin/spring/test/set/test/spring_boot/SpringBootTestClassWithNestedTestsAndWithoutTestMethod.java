package org.jqassistant.plugin.spring.test.set.test.spring_boot;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringBootTestClassWithNestedTestsAndWithoutTestMethod {

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
