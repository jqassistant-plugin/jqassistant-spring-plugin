package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OverridingSubClassOfGenericTransactionalClass extends GenericTransactionalClass<Long> {

    @Override
    public void method(Long l) {
        super.method(l);
    }

}
