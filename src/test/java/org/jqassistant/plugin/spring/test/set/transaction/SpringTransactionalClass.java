package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringTransactionalClass {

    public void transactionalMethod(){
    }

    private void callingTransactional() {
        transactionalMethod();
    }

    public static void staticMethod() {
    }

    private void privateMethod() {
    }

    private void callingPrivateMethod() {
        privateMethod(); // Private methods are not transactional and may be called.
    }

    // This method always runs without a transaction. The REQUIRED semantics of transactionalMethod() would have no effect if called.
    @Transactional(propagation = Propagation.NEVER)
    public void transactionalMethodWithNeverSemantics(){
        transactionalMethod();
    }

    public void transactionalMethodWithRequiredSemantics(){
        transactionalMethod();
    }

}
