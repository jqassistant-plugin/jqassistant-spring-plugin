package org.jqassistant.plugin.spring.test.set.transaction;

import jakarta.transaction.Transactional;

@Transactional
public class JtaJakartaTransactionalClass {

    public void transactionalMethod(){
    }

    private void callingTransactional() {
        transactionalMethod();
    }

    private void privateMethod() {
    }

    private void callingPrivateMethod() {
        privateMethod(); // Private methods are not transactional and may be called.
    }

}
