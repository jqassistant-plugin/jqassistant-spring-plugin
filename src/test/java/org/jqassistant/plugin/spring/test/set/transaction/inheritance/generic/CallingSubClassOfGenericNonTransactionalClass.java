package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CallingSubClassOfGenericNonTransactionalClass extends GenericNonTransactionalClass<Long> {

    public void methodWithRequiredSemantics() {
        method(1L);
    }

}
