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

    // This method always runs without a transaction. The REQUIRED semantics of transactionalMethodWithRequiredSemantics() would have no effect if called.
    @Transactional(value = Transactional.TxType.NEVER)
    public void transactionalMethodWithNeverSemantics(){
        transactionalMethodWithRequiredSemantics();
    }

    @Transactional
    public void anotherTransactionalMethodWithRequiredSemantics(){
        transactionalMethodWithRequiredSemantics();
    }
}
