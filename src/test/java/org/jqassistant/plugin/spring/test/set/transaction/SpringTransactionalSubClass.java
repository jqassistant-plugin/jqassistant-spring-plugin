package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SpringTransactionalSubClass extends SpringTransactionalClass {

    public void transactionalSubClassMethod() {
    }

    private void callingTransactionalSubClassMethod() {
        transactionalSubClassMethod();
    }

    private void privateSubClassMethod() {
    }

    // This method always runs without a transaction. The REQUIRED semantics of transactionalSubClassMethod() would have no effect if called.
    @Transactional(propagation = Propagation.NEVER)
    public void transactionalSubClassMethodWithNeverSemantics(){
        transactionalSubClassMethod();
    }

    public void transactionalSubClassMethodWithRequiredSemantics(){
        transactionalSubClassMethod();
    }

}
