package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CallingSubClassOfGenericClassWithTransactionalMethod extends GenericClassWithTransactionalMethod<Long> {

    public void anotherMethodWithRequiredSemantics() {
        methodWithRequiredSemantics(1L);
    }

    // This method always runs without a transaction. The REQUIRED semantic of methodWithOverriddenSemantics() would have no effect if called.
    @Transactional(propagation = Propagation.NEVER)
    public void callingMethodWithNeverSemantics() {
        methodWithOverriddenSemantics(1L);
    }

}
