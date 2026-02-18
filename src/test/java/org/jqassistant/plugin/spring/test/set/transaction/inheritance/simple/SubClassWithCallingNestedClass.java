package org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple;

public class SubClassWithCallingNestedClass extends SimpleTransactionalClass {

    class InnerClass {
        void callingTransactional() {
            SubClassWithCallingNestedClass.super.method();
        }
    }

}
