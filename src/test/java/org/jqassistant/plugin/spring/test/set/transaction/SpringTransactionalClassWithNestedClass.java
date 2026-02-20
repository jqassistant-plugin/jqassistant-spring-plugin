package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringTransactionalClassWithNestedClass {

    class InnerClass {
        void callingTransactional() {
            SpringTransactionalClassWithNestedClass.this.transactionalMethod();
        }

        void callingPrivateMethod() {
            SpringTransactionalClassWithNestedClass.this.privateMethod();
        }
    }

    void transactionalMethod(){
    }

    private void privateMethod() {
    }

}
