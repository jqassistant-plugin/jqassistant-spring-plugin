package org.jqassistant.plugin.spring.test.set.transaction.inheritance;

import org.springframework.transaction.annotation.Transactional;

public class SimpleClassWithTransactionalMethod {

    @Transactional
    public void method() {

    }

}
