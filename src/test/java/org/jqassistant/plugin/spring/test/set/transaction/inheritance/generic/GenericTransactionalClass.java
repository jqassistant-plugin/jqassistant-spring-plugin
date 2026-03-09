package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class GenericTransactionalClass<T> {

    public void methodWithRequiredSemantics(T parameter) {

    }

    public void methodWithOverriddenSemantics(T parameter) {

    }

}
