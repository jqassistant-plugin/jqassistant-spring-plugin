package org.jqassistant.plugin.spring.test.concept;

import java.util.List;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Concept;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import org.jqassistant.plugin.spring.test.set.transaction.*;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic.CallingSubClassOfGenericClassWithTransactionalMethod;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic.CallingSubClassOfGenericTransactionalClass;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic.GenericClassWithTransactionalMethod;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic.GenericTransactionalClass;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple.CallingSubClassOfSimpleClassWithTransactionalMethod;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple.CallingSubClassOfSimpleTransactionalClass;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple.SimpleClassWithTransactionalMethod;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple.SimpleTransactionalClass;
import org.jqassistant.plugin.spring.test.set.transactionpropagation.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
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

    @ParameterizedTest
    @ValueSource(classes = {JavaTransactionalApiTypeAndMethodLevelPropagation.class, SpringTransactionalTypeAndMethodLevelPropagation.class,
        JakartaTransactionalApiTypeAndMethodLevelPropagation.class})
    void propagationOnTypeAndMethodLevel(Class<?> testClass) throws RuleException, NoSuchMethodException {
        scanClasses(testClass);

        final Result<Concept> conceptResult = applyConcept("spring-transaction:TransactionalMethod");
        store.beginTransaction();

        assertThat(conceptResult.getStatus()).isEqualTo(Result.Status.SUCCESS);
        assertThat(conceptResult.getRows().size()).isEqualTo(3);

        Row row1 = conceptResult.getRows().get(0);
        assertThat((TypeDescriptor) row1.getColumns().get("Type").getValue())
            .is(typeDescriptor(testClass));
        assertThat((MethodDescriptor) row1.getColumns().get("TransactionalMethod").getValue())
            .is(methodDescriptor(testClass, "transactionalMethodMandatory"));
        assertThat(row1.getColumns().get("TransactionPropagation").getLabel()).isEqualTo("MANDATORY");

        Row row2 = conceptResult.getRows().get(1);
        assertThat((TypeDescriptor) row2.getColumns().get("Type").getValue())
            .is(typeDescriptor(testClass));
        assertThat((MethodDescriptor) row2.getColumns().get("TransactionalMethod").getValue())
            .is(methodDescriptor(testClass, "transactionalMethodRequired"));
        assertThat(row2.getColumns().get("TransactionPropagation").getLabel()).isEqualTo("REQUIRED");

        Row row3 = conceptResult.getRows().get(2);
        assertThat((TypeDescriptor) row3.getColumns().get("Type").getValue())
            .is(typeDescriptor(testClass));
        assertThat((MethodDescriptor) row3.getColumns().get("TransactionalMethod").getValue())
            .is(methodDescriptor(testClass, "transactionalMethodRequiresNew"));
        assertThat(row3.getColumns().get("TransactionPropagation").getLabel()).isEqualTo("REQUIRES_NEW");

        store.commitTransaction();
    }

    @ParameterizedTest
    @ValueSource(classes = {JavaTransactionalApiMethodLevelPropagation.class, SpringTransactionalMethodLevelPropagation.class,
        JakartaTransactionalApiMethodLevelPropagation.class})
    void propagationOnMethodLevel(Class<?> testClass) throws RuleException, NoSuchMethodException {
        scanClasses(testClass);

        final Result<Concept> conceptResult = applyConcept("spring-transaction:TransactionalMethod");
        store.beginTransaction();

        assertThat(conceptResult.getStatus()).isEqualTo(Result.Status.SUCCESS);
        assertThat(conceptResult.getRows().size()).isEqualTo(2);

        Row row1 = conceptResult.getRows().get(0);
        assertThat((TypeDescriptor) row1.getColumns().get("Type").getValue())
            .is(typeDescriptor(testClass));
        assertThat((MethodDescriptor) row1.getColumns().get("TransactionalMethod").getValue())
            .is(methodDescriptor(testClass, "transactionalMethodRequired"));
        assertThat(row1.getColumns().get("TransactionPropagation").getLabel()).isEqualTo("REQUIRED");

        Row row2 = conceptResult.getRows().get(1);
        assertThat((TypeDescriptor) row2.getColumns().get("Type").getValue())
            .is(typeDescriptor(testClass));
        assertThat((MethodDescriptor) row2.getColumns().get("TransactionalMethod").getValue())
            .is(methodDescriptor(testClass, "transactionalMethodRequiresNew"));
        assertThat(row2.getColumns().get("TransactionPropagation").getLabel()).isEqualTo("REQUIRES_NEW");

        store.commitTransaction();
    }
}
