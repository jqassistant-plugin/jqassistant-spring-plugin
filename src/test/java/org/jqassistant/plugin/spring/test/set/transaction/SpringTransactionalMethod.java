package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SpringTransactionalMethod {

    @Transactional
    public void transactionalMethodWithRequiredSemantics(){

    }

    public void nonTransactionalMethod() {
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

    // This method always runs without a transaction. The REQUIRED semantics of transactionalMethod() would have no effect if called.
    @Transactional(propagation = Propagation.NEVER)
    public void transactionalMethodWithNeverSemantics(){
        transactionalMethodWithRequiredSemantics();
    }

    @Transactional
    public void anotherTransactionalMethodWithRequiredSemantics(){
        transactionalMethodWithRequiredSemantics();
    }

    @Transactional
    public void requiredTransactionalCallingRequiredTransactionalTransitively() {
        privateCallingTransactional();
    }

    @Transactional(propagation = Propagation.NEVER)
    public void neverTransactionalCallingRequiredTransactionalTransitively() {
        privateCallingTransactional();
    }

}
