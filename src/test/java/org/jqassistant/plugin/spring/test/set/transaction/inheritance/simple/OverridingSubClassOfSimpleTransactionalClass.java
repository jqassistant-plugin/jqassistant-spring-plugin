package org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OverridingSubClassOfSimpleTransactionalClass extends SimpleTransactionalClass{

    @Override
    public void methodWithRequiredSemantics() {
        super.methodWithRequiredSemantics();
    }

    // This method always runs without a transaction. The REQUIRED semantic of oneMoreMethodWithRequiredSemantics() would have no effect if called.
    // Transaction semantics is overridden.
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void oneMoreMethodWithRequiredSemantics() {
        super.oneMoreMethodWithRequiredSemantics();
    }

}
