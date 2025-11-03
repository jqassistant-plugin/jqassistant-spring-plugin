package org.jqassistant.plugin.spring.test.set.transaction.inheritance;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OverridingSubClassOfSimpleTransactionalClass extends SimpleTransactionalClass{

    @Override
    public void method() {
        super.method();
    }

}
