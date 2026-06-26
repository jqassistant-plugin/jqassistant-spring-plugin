package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringTransactionalClass {

    public void transactionalMethodWithRequiredSemantics(){
    }

    private void privateCallingTransactional() {
        transactionalMethodWithRequiredSemantics();
    }

    public static void staticMethod() {
    }

    private void privateMethod() {
    }

    private void callingPrivateMethod() {
        privateMethod(); // Private methods are not transactional and may be called.
    }

    // The rollback configuration is ignored if the method is called within the same bean.
    @Transactional(readOnly = true)
    public void transactionalMethodWithAdditionalConfiguration() {}

    // This method always runs without a transaction. The REQUIRED semantics of transactionalMethodWithRequiredSemantics() would have no effect if called.
    @Transactional(propagation = Propagation.NEVER)
    public void transactionalMethodWithNeverSemantics(){
        transactionalMethodWithRequiredSemantics();
    }


    // This transaction semantics is always compatible with the caller.
    // Calling it within the same bean should lead to finding anyway as it contains an additional configuration attribute.
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public void transactionalMethodWithSupportsSemanticsAndAdditionalConfiguration() {}

    // This transaction semantics is always compatible with the caller.
    @Transactional(propagation = Propagation.SUPPORTS)
    public void transactionalMethodWithSupportsSemantics() {}

    public void anotherTransactionalMethodWithRequiredSemantics(){
        transactionalMethodWithRequiredSemantics();
    }

    public void requiredTransactionalCallingRequiredTransactionalTransitively() {
        privateCallingTransactional();
    }

    @Transactional(propagation = Propagation.NEVER)
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
