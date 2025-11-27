package org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple;

import org.springframework.transaction.annotation.Transactional;

public class SimpleClassWithTransactionalMethod {

    @Transactional
    public void method() {

    }

}
