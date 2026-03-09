package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringTransactionalClassWithNestedClass {

    class InnerClass {
        void callingTransactional() {
            SpringTransactionalClassWithNestedClass.this.transactionalMethodWithRequiredSemantics();
        }

        void callingPrivateMethod() {
            SpringTransactionalClassWithNestedClass.this.privateMethod();
        }

        // This method always runs without a transaction. The REQUIRED semantics of transactionalMethodWithRequiredSemantics() would have no effect if called.
        @Transactional(propagation = Propagation.NEVER)
        void transactionalMethodWithNeverSemantics() {
            SpringTransactionalClassWithNestedClass.this.transactionalMethodWithRequiredSemantics();
        }
    }

    void transactionalMethodWithRequiredSemantics(){
    }

    private void privateMethod() {
    }

}
