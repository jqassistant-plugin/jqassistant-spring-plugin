package org.jqassistant.plugin.spring.test.set.components.dependencies.virtual;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TestController1 extends AbstractTestController implements TestController {

    private TestService testService;

    @Autowired
    public TestController1(TestService testService) {
        this.testService = testService;
    }
}
