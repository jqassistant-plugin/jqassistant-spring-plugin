package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SpringTransactionalImplementingClass implements SpringTransactionalInterface {

    public static void staticMethod() {
        // intentionally left blank
    }

    public void transactionalImplementingClassMethod() {
        // intentionally left blank
    }

    @Override
    public void transactionalMethodWithRequiredSemantics() {
        // intentionally left blank
    }

    private void privateCallingTransactional() {
        transactionalMethodWithRequiredSemantics();
    }

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
}
