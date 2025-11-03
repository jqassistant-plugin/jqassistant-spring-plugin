package org.jqassistant.plugin.spring.test.set.transaction.inheritance;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CallingSubClassOfSimpleClassWithTransactionalMethod extends SimpleClassWithTransactionalMethod {

    public void anotherMethod() {
        method();
    }

}
