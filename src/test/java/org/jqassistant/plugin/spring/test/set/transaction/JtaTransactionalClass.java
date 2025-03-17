package org.jqassistant.plugin.spring.test.set.transaction;

import javax.transaction.Transactional;

@Transactional
public class JtaTransactionalClass {

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
