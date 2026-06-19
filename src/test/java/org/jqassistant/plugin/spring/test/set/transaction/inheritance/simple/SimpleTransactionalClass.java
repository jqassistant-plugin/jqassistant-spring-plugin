package org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class SimpleTransactionalClass {

    @Transactional
    public void methodWithRequiredSemantics() {

    }

    public void methodWithOverriddenSemantics() {

    }

}
