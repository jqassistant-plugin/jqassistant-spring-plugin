package org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CallingSubClassOfSimpleNonTransactionalClass extends SimpleNonTransactionalClass {

    public void anotherMethod() {
        method();
    }

}
