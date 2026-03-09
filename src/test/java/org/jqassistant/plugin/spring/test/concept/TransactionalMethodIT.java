package org.jqassistant.plugin.spring.test.concept;

import java.util.List;

import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import org.jqassistant.plugin.spring.test.set.transaction.*;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic.CallingSubClassOfGenericClassWithTransactionalMethod;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic.CallingSubClassOfGenericTransactionalClass;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic.GenericClassWithTransactionalMethod;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic.GenericTransactionalClass;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple.CallingSubClassOfSimpleClassWithTransactionalMethod;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple.CallingSubClassOfSimpleTransactionalClass;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple.SimpleClassWithTransactionalMethod;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple.SimpleTransactionalClass;
import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jqassistant.plugin.spring.test.SimpleMethodDescriptorCondition.simpleMethodDescriptor;

class TransactionalMethodIT extends AbstractSpringIT {

    @Test
    void transactionalMethod() throws Exception {
        scanClasses(SpringTransactionalMethod.class, JtaTransactionalMethod.class, JtaJakartaTransactionalMethod.class,
            SpringTransactionalClass.class, SpringTransactionalSubClass.class, SpringTransactionalInterface.class,
            SpringTransactionalImplementingClass.class, JtaTransactionalClass.class, NonTransactionalSubClassOfSpringTransactionalMethod.class,
            JtaJakartaTransactionalClass.class,
            SimpleTransactionalClass.class, CallingSubClassOfSimpleTransactionalClass.class,
            SimpleClassWithTransactionalMethod.class, CallingSubClassOfSimpleClassWithTransactionalMethod.class,
            GenericTransactionalClass.class, CallingSubClassOfGenericTransactionalClass.class,
            GenericClassWithTransactionalMethod.class, CallingSubClassOfGenericClassWithTransactionalMethod.class);
        assertThat(applyConcept("spring-transaction:TransactionalMethod").getStatus()).isEqualTo(SUCCESS);
        store.beginTransaction();
        final List<MethodDescriptor> methods = query("MATCH (m:Spring:Method:Transactional) RETURN m").getColumn("m");
        assertThat(methods).hasSize(46);

        // method level annotations
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalMethod.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).doNotHave(
            methodDescriptor(SpringTransactionalMethod.class, "nonTransactionalMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalMethod.class, "transactionalMethodWithNeverSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalMethod.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaTransactionalMethod.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaTransactionalMethod.class, "transactionalMethodWithNeverSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaTransactionalMethod.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaJakartaTransactionalMethod.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).doNotHave(
            methodDescriptor(NonTransactionalSubClassOfSpringTransactionalMethod.class, "transactionalMethodWithRequiredSemantics"));

        // class level annotations
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalClass.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalClass.class, "transactionalMethodWithNeverSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalClass.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalSubClass.class, "transactionalSubClassMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalSubClass.class, "transactionalSubClassMethodWithNeverSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalSubClass.class, "transactionalSubClassMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalInterface.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalImplementingClass.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalImplementingClass.class, "transactionalImplementingClassMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalImplementingClass.class, "transactionalMethodWithNeverSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalImplementingClass.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaTransactionalClass.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaTransactionalClass.class, "transactionalMethodWithNeverSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaTransactionalClass.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaJakartaTransactionalClass.class, "transactionalMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaJakartaTransactionalClass.class, "transactionalMethodWithNeverSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaJakartaTransactionalClass.class, "transactionalMethodWithRequiredSemantics"));

        // Simple inheritance
        assertThat(methods).haveExactly(1,
            methodDescriptor(SimpleTransactionalClass.class, "methodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SimpleTransactionalClass.class, "oneMoreMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfSimpleTransactionalClass.class, "anotherMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfSimpleTransactionalClass.class, "transactionalMethodWithNeverSemantics"));
        assertThat(methods).haveExactly(1,
            simpleMethodDescriptor(CallingSubClassOfSimpleTransactionalClass.class, "void methodWithRequiredSemantics()"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SimpleClassWithTransactionalMethod.class, "methodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SimpleClassWithTransactionalMethod.class, "oneMoreMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfSimpleClassWithTransactionalMethod.class, "anotherMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfSimpleClassWithTransactionalMethod.class, "transactionalMethodWithNeverSemantics"));
        assertThat(methods).haveExactly(1,
            simpleMethodDescriptor(CallingSubClassOfSimpleClassWithTransactionalMethod.class, "void methodWithRequiredSemantics()"));

        // Generic inheritance
        assertThat(methods).haveExactly(1,
            methodDescriptor(GenericTransactionalClass.class, "methodWithRequiredSemantics", Object.class));
        assertThat(methods).haveExactly(1,
            methodDescriptor(GenericTransactionalClass.class, "oneMoreMethodWithRequiredSemantics", Object.class));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfGenericTransactionalClass.class, "anotherMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            simpleMethodDescriptor(CallingSubClassOfGenericTransactionalClass.class, "void methodWithRequiredSemantics(java.lang.Object)"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfGenericTransactionalClass.class, "callingMethodWithNeverSemantics"));
        assertThat(methods).haveExactly(1,
            simpleMethodDescriptor(CallingSubClassOfGenericTransactionalClass.class, "void oneMoreMethodWithRequiredSemantics(java.lang.Object)"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(GenericClassWithTransactionalMethod.class, "methodWithRequiredSemantics", Object.class));
        assertThat(methods).haveExactly(1,
            methodDescriptor(GenericClassWithTransactionalMethod.class, "oneMoreMethodWithRequiredSemantics", Object.class));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class, "anotherMethodWithRequiredSemantics"));
        assertThat(methods).haveExactly(1,
            simpleMethodDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class, "void methodWithRequiredSemantics(java.lang.Object)"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class, "callingMethodWithNeverSemantics"));
        assertThat(methods).haveExactly(1,
            simpleMethodDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class, "void oneMoreMethodWithRequiredSemantics(java.lang.Object)"));

        store.commitTransaction();
    }
}
