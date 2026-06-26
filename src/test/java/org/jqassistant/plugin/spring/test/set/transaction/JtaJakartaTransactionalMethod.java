package org.jqassistant.plugin.spring.test.set.transaction;

import jakarta.transaction.Transactional;

public class JtaJakartaTransactionalMethod {

    @Transactional
    public void transactionalMethodWithRequiredSemantics() {

    }

    private void privateCallingTransactional() {
        transactionalMethodWithRequiredSemantics();
    }

    @Transactional // Illegal combination of private and @Transactional is subject to tests
    private void privateMethod() {
    }

    private void callingPrivateMethod() {
        privateMethod(); // Private methods are not transactional and may be called.
    }

    // The rollback configuration is ignored if the method is called within the same bean.
    @Transactional(rollbackOn =  Exception.class)
    public void transactionalMethodWithAdditionalConfiguration() {}

    // This method always runs without a transaction. The REQUIRED semantics of transactionalMethodWithRequiredSemantics() would have no effect if called.
    @Transactional(value = Transactional.TxType.NEVER)
    public void transactionalMethodWithNeverSemantics(){
        transactionalMethodWithRequiredSemantics();
    }

    // This transaction semantics is always compatible with the caller.
    // Calling it within the same bean should lead to finding anyway as it contains an additional configuration attribute.
    @Transactional(value = Transactional.TxType.SUPPORTS, rollbackOn =  Exception.class)
    public void transactionalMethodWithSupportsSemanticsAndAdditionalConfiguration() {}

    // This transaction semantics is always compatible with the caller.
    @Transactional(value = Transactional.TxType.SUPPORTS)
    public void transactionalMethodWithSupportsSemantics() {}

    @Transactional
    public void anotherTransactionalMethodWithRequiredSemantics(){
        transactionalMethodWithRequiredSemantics();
    }

    @Transactional
    public void requiredTransactionalCallingRequiredTransactionalTransitively() {
        privateCallingTransactional();
    }

    @Transactional(value = Transactional.TxType.NEVER)
    public void neverTransactionalCallingRequiredTransactionalTransitively() {
        privateCallingTransactional();
    }

    @Transactional
    public void transactionalMethodCallingMethodWithAdditionalConfiguration() {
        transactionalMethodWithAdditionalConfiguration();
    }

    @Transactional
    public void transactionalMethodCallingMethodWithSupportsSemanticsAndAdditionalConfiguration() {
        transactionalMethodWithSupportsSemanticsAndAdditionalConfiguration();
    }

    @Transactional
    public void transactionalMethodCallingMethodWithSupportsSemantics() {
        transactionalMethodWithSupportsSemantics();
    }
}
