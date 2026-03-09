package org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CallingSubClassOfSimpleTransactionalClass extends SimpleTransactionalClass {

    public void anotherMethodWithRequiredSemantics() {
        methodWithRequiredSemantics();
    }

    // This method always runs without a transaction. The REQUIRED semantic of methodWithRequiredSemantics() would have no effect if called.
    @Transactional(propagation = Propagation.NEVER)
    public void transactionalMethodWithNeverSemantics() {
        methodWithRequiredSemantics();
    }

}
