package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OverridingSubClassOfGenericClassWithTransactionalMethod extends GenericClassWithTransactionalMethod<Long> {

    @Override
    public void methodWithRequiredSemantics(Long l) {
        super.methodWithRequiredSemantics(l);
    }

    // This method always runs without a transaction. The REQUIRED semantics of methodWithOverriddenSemantics() would have no effect if called.
    // Transaction semantics is overridden.
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void methodWithOverriddenSemantics(Long l) {
        super.methodWithOverriddenSemantics(l);
    }

}
