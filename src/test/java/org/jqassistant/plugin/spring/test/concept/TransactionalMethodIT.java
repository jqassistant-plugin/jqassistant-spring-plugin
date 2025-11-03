package org.jqassistant.plugin.spring.test.concept;

import java.util.List;

import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import org.jqassistant.plugin.spring.test.set.transaction.*;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.CallingSubClassOfSimpleClassWithTransactionalMethod;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.CallingSubClassOfSimpleTransactionalClass;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.SimpleClassWithTransactionalMethod;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.SimpleTransactionalClass;
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
            SpringTransactionalImplementingClass.class, JtaTransactionalClass.class,
            JtaJakartaTransactionalClass.class,
            SimpleTransactionalClass.class, CallingSubClassOfSimpleTransactionalClass.class,
            SimpleClassWithTransactionalMethod.class, CallingSubClassOfSimpleClassWithTransactionalMethod.class);
        assertThat(applyConcept("spring-transaction:TransactionalMethod").getStatus()).isEqualTo(SUCCESS);
        store.beginTransaction();
        final List<MethodDescriptor> methods = query("MATCH (m:Spring:Method:Transactional) RETURN m").getColumn("m");
        assertThat(methods).hasSize(16);
        // method level annotations
        assertThat(methods).haveExactly(1,
            methodDescriptor(SpringTransactionalMethod.class, "transactionalMethod"));
        assertThat(methods).doNotHave(
            methodDescriptor(SpringTransactionalMethod.class, "nonTransactionalMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaTransactionalMethod.class, "transactionalMethod"));
        assertThat(methods).haveExactly(1,
            methodDescriptor(JtaJakartaTransactionalMethod.class, "transactionalMethod"));
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
        store.commitTransaction();
    }
}
