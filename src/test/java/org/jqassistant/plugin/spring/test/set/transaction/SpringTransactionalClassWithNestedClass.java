package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.transaction.annotation.Propagation;
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

        // This method always runs without a transaction. The REQUIRED semantics of transactionalMethod() would have no effect if called.
        @Transactional(propagation = Propagation.NEVER)
        void transactionalMethodWithNeverSemantics() {
            SpringTransactionalClassWithNestedClass.this.transactionalMethod();
        }
    }

    void transactionalMethod(){
    }

    private void privateMethod() {
    }

}
