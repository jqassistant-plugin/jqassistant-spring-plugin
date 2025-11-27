package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CallingSubClassOfGenericClassWithTransactionalMethod extends GenericClassWithTransactionalMethod<Long> {

    public void anotherMethod() {
        method(1L);
    }

}
