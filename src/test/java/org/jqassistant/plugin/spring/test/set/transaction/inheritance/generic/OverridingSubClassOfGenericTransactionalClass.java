package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OverridingSubClassOfGenericTransactionalClass extends GenericTransactionalClass<Long> {

    @Override
    public void methodWithRequiredSemantics(Long l) {
        super.methodWithRequiredSemantics(l);
    }

    // This method always runs without a transaction. The REQUIRED semantics of oneMoreMethodWithRequiredSemantics() would have no effect if called.
    // Transaction semantics is overridden.
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void oneMoreMethodWithRequiredSemantics(Long l) {
        super.oneMoreMethodWithRequiredSemantics(l);
    }

}
