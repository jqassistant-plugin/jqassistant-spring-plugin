package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OverridingSubClassOfGenericClassWithTransactionalMethod extends GenericClassWithTransactionalMethod<Long> {

    @Override
    public void method(Long l) {
        super.method(l);
    }

    // This method always runs without a transaction. The REQUIRED semantic of oneMoreMethod() would have no effect if called.
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void oneMoreMethod(Long l) {
        super.oneMoreMethod(l);
    }

}
