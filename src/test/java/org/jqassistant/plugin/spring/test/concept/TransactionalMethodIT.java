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
        assertThat(methods).hasSize(22);

        // method level annotations
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalMethod.class, "transactionalMethod"));
        assertThat(methods).doNotHave(
            methodDescriptor(SpringTransactionalMethod.class, "nonTransactionalMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaTransactionalMethod.class, "transactionalMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaJakartaTransactionalMethod.class, "transactionalMethod"));
        assertThat(methods).doNotHave(
            methodDescriptor(NonTransactionalSubClassOfSpringTransactionalMethod.class, "transactionalMethod"));

        // class level annotations
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalClass.class, "transactionalMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalSubClass.class, "transactionalSubClassMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalInterface.class, "transactionalMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalImplementingClass.class, "transactionalMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalImplementingClass.class, "transactionalImplementingClassMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaTransactionalClass.class, "transactionalMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaJakartaTransactionalClass.class, "transactionalMethod"));

        // Simple inheritance
        assertThat(methods).haveExactly(1,
            methodDescriptor(SimpleTransactionalClass.class, "method"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfSimpleTransactionalClass.class, "anotherMethod"));
        assertThat(methods).haveExactly(1,
            simpleMethodDescriptor(CallingSubClassOfSimpleTransactionalClass.class, "void method()"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(SimpleClassWithTransactionalMethod.class, "method"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfSimpleClassWithTransactionalMethod.class, "anotherMethod"));
        assertThat(methods).haveExactly(1,
            simpleMethodDescriptor(CallingSubClassOfSimpleClassWithTransactionalMethod.class, "void method()"));

        // Generic inheritance
        assertThat(methods).haveExactly(1,
            methodDescriptor(GenericTransactionalClass.class, "method", Object.class));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfGenericTransactionalClass.class, "anotherMethod"));
        assertThat(methods).haveExactly(1,
            simpleMethodDescriptor(CallingSubClassOfGenericTransactionalClass.class, "void method(java.lang.Object)"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(GenericClassWithTransactionalMethod.class, "method", Object.class));
        assertThat(methods).haveExactly(1,
            methodDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class, "anotherMethod"));
        assertThat(methods).haveExactly(1,
            simpleMethodDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class, "void method(java.lang.Object)"));

        store.commitTransaction();
    }
}
