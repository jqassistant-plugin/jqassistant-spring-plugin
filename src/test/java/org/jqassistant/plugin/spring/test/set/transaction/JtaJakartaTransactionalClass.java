package org.jqassistant.plugin.spring.test.set.transaction;

import jakarta.transaction.Transactional;

@Transactional
public class JtaJakartaTransactionalClass {

    public void transactionalMethodWithRequiredSemantics(){
    }

    private void privateCallingTransactional() {
        transactionalMethodWithRequiredSemantics();
    }

    private void privateMethod() {
    }

    private void callingPrivateMethod() {
        privateMethod(); // Private methods are not transactional and may be called.
    }

    // The rollback configuration is ignored if the method is called within the same bean.
    @Transactional(rollbackOn =  Exception.class)
    public void transactionalMethodWithAdditionalConfiguration() {}

    // This method always runs without a transaction. The REQUIRED semantic of transactionalMethodWithRequiredSemantics() would have no effect if called.
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

    public void anotherTransactionalMethodWithRequiredSemantics(){
        transactionalMethodWithRequiredSemantics();
    }

    public void requiredTransactionalCallingRequiredTransactionalTransitively() {
        privateCallingTransactional();
    }

    @Transactional(value = Transactional.TxType.NEVER)
    public void neverTransactionalCallingRequiredTransactionalTransitively() {
        privateCallingTransactional();
    }

    public void transactionalMethodCallingMethodWithAdditionalConfiguration() {
        transactionalMethodWithAdditionalConfiguration();
    }

    public void transactionalMethodCallingMethodWithSupportsSemanticsAndAdditionalConfiguration() {
        transactionalMethodWithSupportsSemanticsAndAdditionalConfiguration();
    }

    public void transactionalMethodCallingMethodWithSupportsSemantics() {
        transactionalMethodWithSupportsSemantics();
    }

}
