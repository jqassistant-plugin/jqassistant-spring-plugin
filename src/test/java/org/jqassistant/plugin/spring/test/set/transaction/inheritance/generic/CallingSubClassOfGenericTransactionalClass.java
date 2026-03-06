package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CallingSubClassOfGenericTransactionalClass extends GenericTransactionalClass<Long> {

    public void anotherMethod() {
        method(1L);
    }

    // This method always runs without a transaction. The REQUIRED semantic of oneMoreMethod() would have no effect if called.
    @Transactional(propagation = Propagation.NEVER)
    public void callingOneMoreMethod() {
        oneMoreMethod(1L);
    }

}
