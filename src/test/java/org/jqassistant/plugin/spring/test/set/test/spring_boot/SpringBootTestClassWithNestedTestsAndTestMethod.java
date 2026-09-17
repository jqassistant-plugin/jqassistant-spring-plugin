package org.jqassistant.plugin.spring.test.set.test.spring_boot;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringBootTestClassWithNestedTestsAndTestMethod {

    @Test
    public void test() {}

    @Nested
    public class NestedTest {
        @Test
        public void test() {}
    }
}
