package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.stereotype.Service;

@Service
public class SpringTransactionalImplementingClass implements SpringTransactionalInterface {

    public static void staticMethod() {
        // intentionally left blank
    }

    public void transactionalImplementingClassMethod() {
        // intentionally left blank
    }

    @Override
    public void transactionalMethod() {
        // intentionally left blank
    }
}
