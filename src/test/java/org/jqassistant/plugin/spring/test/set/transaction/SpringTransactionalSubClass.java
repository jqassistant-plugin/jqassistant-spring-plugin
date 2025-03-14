package org.jqassistant.plugin.spring.test.set.transaction;

public class SpringTransactionalSubClass extends SpringTransactionalClass {

    public void transactionalSubClassMethod() {
    }

    private void callingTransactionalSubClassMethod() {
        transactionalSubClassMethod();
    }

    private void privateSubClassMethod() {
    }

}
