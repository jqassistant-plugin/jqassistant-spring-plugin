package org.jqassistant.plugin.spring.test.set.transaction.inheritance;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CallingSubClassOfGenericNonTransactionalClass extends GenericNonTransactionalClass<Long> {

    public void anotherMethod() {
        method(1L);
    }

}
