package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringTransactionalClassWithNestedClass {

    class InnerClass {
        void nonTransactionalMethod() {
            SpringTransactionalClassWithNestedClass.this.transactionalMethodWithRequiredSemantics();
        }

        @Transactional
        void transactionalMethodWithRequiredSemantics() {
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

        @Transactional
        void transactionalMethodCallingMethodWithAdditionalConfiguration() {
            SpringTransactionalClassWithNestedClass.this.transactionalMethodWithAdditionalConfiguration();
        }
    }

    void transactionalMethodWithRequiredSemantics(){
    }

    // The rollback configuration is ignored if the method is called within the same bean.
    @Transactional(readOnly = true)
    void transactionalMethodWithAdditionalConfiguration() {}

    private void privateMethod() {
    }

}
