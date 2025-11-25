package org.jqassistant.plugin.spring.test.set.transaction;

public class NonTransactionalSubClassOfSpringTransactionalMethod extends SpringTransactionalMethod{

    // Method is not actually transactional, since class-level annotations are not inherited.
    @Override
    public void transactionalMethod() {
    }

}
