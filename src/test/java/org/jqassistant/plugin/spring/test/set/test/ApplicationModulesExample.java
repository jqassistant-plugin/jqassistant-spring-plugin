package org.jqassistant.plugin.spring.test.set.test;

import org.springframework.modulith.core.ApplicationModules;

public class ApplicationModulesExample {

    void applicationModulesVerifyExampleMethod() {
        final ApplicationModules modules = ApplicationModules.of("my.package");
        modules.verify();
    }
}
