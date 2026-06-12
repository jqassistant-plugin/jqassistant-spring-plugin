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

    // The rollback configuration is ignored if the method is called within the same bean.
    @Transactional(readOnly = true)
    public void transactionalSubClassMethodWithAdditionalConfiguration() {}

    // This method always runs without a transaction. The REQUIRED semantics of transactionalSubClassMethodWithRequiredSemantics() would have no effect if called.
    @Transactional(propagation = Propagation.NEVER)
    public void transactionalSubClassMethodWithNeverSemantics(){
        transactionalSubClassMethodWithRequiredSemantics();
    }

    public void anotherTransactionalSubClassMethodWithRequiredSemantics(){
        transactionalSubClassMethodWithRequiredSemantics();
    }

    @Transactional
    public void transactionalMethodCallingMethodWithAdditionalConfiguration() {
        transactionalSubClassMethodWithAdditionalConfiguration();
    }

}
