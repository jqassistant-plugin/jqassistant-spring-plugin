package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.stereotype.Service;

@Service
public class SpringTransactionalImplementingClass implements SpringTransactionalInterface {

    public static void staticMethod() {
    }

    public void transactionalImplementingClassMethod() {
    }

    @Override
    public void transactionalMethod() {

    }
}
