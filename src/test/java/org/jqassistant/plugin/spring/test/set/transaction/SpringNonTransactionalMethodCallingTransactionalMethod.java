package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SpringNonTransactionalMethodCallingTransactionalMethod {

    @Transactional
    public void transactionalMethodWithRequiredSemantics(){}

    @Transactional(propagation = Propagation.NEVER)
    public void transactionalMethodWithNeverSemantics(){}

    // should be reported as transaction handling is required by the target method
    public void nonTransactionalMethodCallingTransactionalRequiredMethodDirectly(){
        transactionalMethodWithRequiredSemantics();
    }

    // should be reported as transaction handling is required by the target method
    public void nonTransactionalMethodCallingTransactionalRequiredMethodTransitively(){
        privateMethodCallingTransactionalRequiredMethod();
    }

    // should not be reported as no transaction handling is required by the target method
    public void nonTransactionalMethodCallingTransactionalNeverMethodDirectly(){
        transactionalMethodWithNeverSemantics();
    }

    // should not be reported as no transaction handling is required by the target method
    public void nonTransactionalMethodCallingTransactionalNeverMethodTransitively(){
        privateMethodCallingTransactionalNeverMethod();
    }

    // should not be reported as it is private
    private void privateMethodCallingTransactionalRequiredMethod() {
        transactionalMethodWithRequiredSemantics();
    }

    // should not be reported as it is private
    private void privateMethodCallingTransactionalNeverMethod() {
        transactionalMethodWithNeverSemantics();
    }
}
