package org.jqassistant.plugin.spring.test.set.transaction;

import javax.transaction.Transactional;

public class JtaTransactionalMethod {

    @Transactional
    public void transactionalMethod() {

    }

    private void callingTransactional() {
        transactionalMethod();
    }

    @Transactional // Illegal combination of private and @Transactional is subject to tests
    private void privateMethod() {
    }

    private void callingPrivateMethod() {
        privateMethod(); // Private methods are not transactional and may be called.
    }

    // This method always runs without a transaction. The REQUIRED semantics of transactionalMethod() would have no effect if called.
    @Transactional(value = Transactional.TxType.NEVER)
    public void transactionalMethodWithNeverSemantics(){
        transactionalMethod();
    }

    @Transactional
    public void transactionalMethodWithRequiredSemantics(){
        transactionalMethod();
    }


}
