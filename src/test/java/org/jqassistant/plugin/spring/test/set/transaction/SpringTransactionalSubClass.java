package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SpringTransactionalSubClass extends SpringTransactionalClass {

    public void transactionalSubClassMethodWithRequiredSemantics() {
    }

    private void privateCallingTransactionalSubClassMethod() {
        transactionalSubClassMethodWithRequiredSemantics();
    }

    private void privateSubClassMethod() {
    }

    // This method always runs without a transaction. The REQUIRED semantics of transactionalSubClassMethodWithRequiredSemantics() would have no effect if called.
    @Transactional(propagation = Propagation.NEVER)
    public void transactionalSubClassMethodWithNeverSemantics(){
        transactionalSubClassMethodWithRequiredSemantics();
    }

    public void anotherTransactionalSubClassMethodWithRequiredSemantics(){
        transactionalSubClassMethodWithRequiredSemantics();
    }

}
