package org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OverridingSubClassOfSimpleClassWithTransactionalMethod extends SimpleClassWithTransactionalMethod{

    @Override
    public void method() {
        super.method();
    }

}
