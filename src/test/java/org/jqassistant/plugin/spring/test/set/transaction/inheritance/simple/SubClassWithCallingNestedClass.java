package org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SubClassWithCallingNestedClass extends SimpleTransactionalClass {

    class InnerClass {
        void callingTransactional() {
            SubClassWithCallingNestedClass.super.methodWithRequiredSemantics();
        }

        // This method always runs without a transaction. The REQUIRED semantics of oneMoreMethodWithRequiredSemantics() would have no effect if called.
        @Transactional(propagation = Propagation.NEVER)
        void transactionalMethodWithNeverSemantics() {
            SubClassWithCallingNestedClass.super.oneMoreMethodWithRequiredSemantics();
        }
    }

}
