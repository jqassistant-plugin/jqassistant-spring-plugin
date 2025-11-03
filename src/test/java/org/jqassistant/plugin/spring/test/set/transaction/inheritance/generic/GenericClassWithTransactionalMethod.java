package org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic;

import org.springframework.transaction.annotation.Transactional;

public class GenericClassWithTransactionalMethod<T> {

    @Transactional
    public void method(T parameter) {

    }

}
