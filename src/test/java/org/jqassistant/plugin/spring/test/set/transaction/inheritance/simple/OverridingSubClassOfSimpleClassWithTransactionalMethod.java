package org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OverridingSubClassOfSimpleClassWithTransactionalMethod extends SimpleClassWithTransactionalMethod{

    @Override
    public void method() {
        super.method();
    }

    // This method always runs without a transaction. The REQUIRED semantics of oneMoreMethod() would have no effect if called.
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void oneMoreMethod() {
        super.oneMoreMethod();
    }

}
