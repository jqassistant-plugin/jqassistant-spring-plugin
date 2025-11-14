package org.jqassistant.plugin.spring.test.set.transaction.inheritance;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CallingSubClassOfSimpleTransactionalClass extends SimpleTransactionalClass {

    public void anotherMethod() {
        method();
    }

}
