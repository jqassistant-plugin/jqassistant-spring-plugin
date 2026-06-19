package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OverridingSubClassOfGenericTransactionalClass extends GenericTransactionalClass<Long> {

    @Override
    public void methodWithRequiredSemanticsAndOverriddenReadOnlyFlag(Long l) {
        super.methodWithRequiredSemanticsAndOverriddenReadOnlyFlag(l);
    }

    // This method always runs without a transaction. The REQUIRED semantics of methodWithOverriddenSemantics() would have no effect if called.
    // Transaction semantics is overridden.
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void methodWithOverriddenSemantics(Long l) {
        super.methodWithOverriddenSemantics(l);
    }

}
