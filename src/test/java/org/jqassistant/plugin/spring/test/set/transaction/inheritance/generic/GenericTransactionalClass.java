package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class GenericTransactionalClass<T> {

    @Transactional
    public void methodWithRequiredSemantics(T parameter) {

    }

    public void methodWithOverriddenSemantics(T parameter) {

    }

}
