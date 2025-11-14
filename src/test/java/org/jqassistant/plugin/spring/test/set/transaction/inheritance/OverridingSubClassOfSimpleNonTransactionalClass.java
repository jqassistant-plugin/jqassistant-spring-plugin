package org.jqassistant.plugin.spring.test.set.transaction.inheritance;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OverridingSubClassOfSimpleNonTransactionalClass extends SimpleNonTransactionalClass{

    @Override
    public void method() {
        super.method();
    }

}
