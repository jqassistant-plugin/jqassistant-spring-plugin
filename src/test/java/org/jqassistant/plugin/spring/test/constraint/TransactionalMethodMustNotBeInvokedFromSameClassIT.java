package org.jqassistant.plugin.spring.test.constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;

import org.jqassistant.plugin.spring.test.set.transaction.*;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.generic.*;
import org.jqassistant.plugin.spring.test.set.transaction.inheritance.simple.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.FAILURE;
import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.jqassistant.plugin.spring.test.SimpleMethodDescriptorCondition.simpleMethodDescriptor;

class TransactionalMethodMustNotBeInvokedFromSameClassIT extends AbstractJavaPluginIT {

    @ParameterizedTest
    @ValueSource(classes = {JtaJakartaTransactionalClass.class, JtaTransactionalMethod.class,
        JtaTransactionalClass.class, JtaTransactionalMethod.class, SpringTransactionalClass.class,
        SpringTransactionalImplementingClass.class, SpringTransactionalMethod.class})
    void transactionMethodsMustNotBeCalledDirectlyWithViolations(Class<?> clazz) throws Exception {
        scanClasses(SpringTransactionalInterface.class, clazz);
        assertThat(validateConstraint("spring-transaction:TransactionalMethodMustNotBeInvokedFromSameClass")
            .getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        List<Result<Constraint>> constraintViolations = new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);
        Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRule().getId())
            .isEqualTo("spring-transaction:TransactionalMethodMustNotBeInvokedFromSameClass");
        assertThat(result.getRows()).hasSize(1);
        final Row row = result.getRows().get(0);
        assertThat(row.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(clazz));
        assertThat(row.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(clazz, "callingTransactional"));
        assertThat(row.getColumns().get("TransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(clazz, "transactionalMethod"));
        store.commitTransaction();
    }

    @Test
    void transactionMethodsMustNotBeCalledDirectlyWithViolationsInSubClass() throws Exception {
        scanClasses(SpringTransactionalClass.class, SpringTransactionalSubClass.class);
        assertThat(validateConstraint("spring-transaction:TransactionalMethodMustNotBeInvokedFromSameClass")
            .getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        List<Result<Constraint>> constraintViolations = new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);
        Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRule().getId())
            .isEqualTo("spring-transaction:TransactionalMethodMustNotBeInvokedFromSameClass");
        assertThat(result.getRows()).hasSize(2);
        Map<String, Row> resultMap = result.getRows().stream().collect(Collectors
            .toMap(
                row -> ((TypeDescriptor) row.getColumns().get("Type").getValue()).getName(),
                row -> row)
        );

        final Row subClassRow = resultMap.get(SpringTransactionalSubClass.class.getSimpleName());
        assertThat(subClassRow.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(SpringTransactionalSubClass.class));
        assertThat(subClassRow.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SpringTransactionalSubClass.class, "callingTransactionalSubClassMethod"));
        assertThat(subClassRow.getColumns().get("TransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SpringTransactionalSubClass.class, "transactionalSubClassMethod"));

        final Row superClassRow = resultMap.get(SpringTransactionalClass.class.getSimpleName());
        assertThat(superClassRow.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(SpringTransactionalClass.class));
        assertThat(superClassRow.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SpringTransactionalClass.class, "callingTransactional"));
        assertThat(superClassRow.getColumns().get("TransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SpringTransactionalClass.class, "transactionalMethod"));

        store.commitTransaction();
    }

    @Test
    void transactionalMethodMustNotBeInvokedFromSameClassSimpleInheritance() throws Exception {
        scanClasses(SimpleNonTransactionalClass.class, SimpleTransactionalClass.class, SimpleClassWithTransactionalMethod.class,
            CallingSubClassOfSimpleNonTransactionalClass.class, CallingSubClassOfSimpleTransactionalClass.class, CallingSubClassOfSimpleClassWithTransactionalMethod.class,
            OverridingSubClassOfSimpleNonTransactionalClass.class, OverridingSubClassOfSimpleTransactionalClass.class, OverridingSubClassOfSimpleClassWithTransactionalMethod.class);
        assertThat(validateConstraint("spring-transaction:TransactionalMethodMustNotBeInvokedFromSameClass").getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        final List<Result<Constraint>> constraintViolations = new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);
        final Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRule().getId()).isEqualTo("spring-transaction:TransactionalMethodMustNotBeInvokedFromSameClass");
        assertThat(result.getRows()).hasSize(4);
        final Map<String, Row> resultMap = result.getRows().stream().collect(Collectors.toMap(row -> (row.getColumns().get("Type").getLabel()), Function.identity()));

        final Row callingSubClassOfSimpleTransactionalClassResult = resultMap.get(CallingSubClassOfSimpleTransactionalClass.class.getName());
        assertThat(callingSubClassOfSimpleTransactionalClassResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(CallingSubClassOfSimpleTransactionalClass.class));
        assertThat(callingSubClassOfSimpleTransactionalClassResult.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(CallingSubClassOfSimpleTransactionalClass.class, "anotherMethod"));
        assertThat(callingSubClassOfSimpleTransactionalClassResult.getColumns().get("TransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(simpleMethodDescriptor(CallingSubClassOfSimpleTransactionalClass.class, "void method()"));

        final Row callingSubClassOfSimpleClassWithTransactionalMethodResult = resultMap.get(CallingSubClassOfSimpleClassWithTransactionalMethod.class.getName());
        assertThat(callingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(CallingSubClassOfSimpleClassWithTransactionalMethod.class));
        assertThat(callingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(CallingSubClassOfSimpleClassWithTransactionalMethod.class, "anotherMethod"));
        assertThat(callingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("TransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(simpleMethodDescriptor(CallingSubClassOfSimpleClassWithTransactionalMethod.class, "void method()"));

        final Row overridingSubClassOfSimpleTransactionalClassResult = resultMap.get(OverridingSubClassOfSimpleTransactionalClass.class.getName());
        assertThat(overridingSubClassOfSimpleTransactionalClassResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(OverridingSubClassOfSimpleTransactionalClass.class));
        assertThat(overridingSubClassOfSimpleTransactionalClassResult.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(OverridingSubClassOfSimpleTransactionalClass.class, "method"));
        assertThat(overridingSubClassOfSimpleTransactionalClassResult.getColumns().get("TransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SimpleTransactionalClass.class, "method"));

        final Row overridingSubClassOfSimpleClassWithTransactionalMethodResult = resultMap.get(OverridingSubClassOfSimpleClassWithTransactionalMethod.class.getName());
        assertThat(overridingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(OverridingSubClassOfSimpleClassWithTransactionalMethod.class));
        assertThat(overridingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(OverridingSubClassOfSimpleClassWithTransactionalMethod.class, "method"));
        assertThat(overridingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("TransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SimpleClassWithTransactionalMethod.class, "method"));

        store.commitTransaction();
    }

    @Test
    void transactionalMethodMustNotBeInvokedFromSameClassGenericInheritance() throws Exception {
        scanClasses(GenericNonTransactionalClass.class, GenericTransactionalClass.class, GenericClassWithTransactionalMethod.class,
            CallingSubClassOfGenericNonTransactionalClass.class, CallingSubClassOfGenericTransactionalClass.class, CallingSubClassOfGenericClassWithTransactionalMethod.class,
            OverridingSubClassOfGenericNonTransactionalClass.class, OverridingSubClassOfGenericTransactionalClass.class, OverridingSubClassOfGenericClassWithTransactionalMethod.class);
        assertThat(validateConstraint("spring-transaction:TransactionalMethodMustNotBeInvokedFromSameClass").getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        final List<Result<Constraint>> constraintViolations = new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);
        final Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRule().getId()).isEqualTo("spring-transaction:TransactionalMethodMustNotBeInvokedFromSameClass");
        assertThat(result.getRows()).hasSize(4);
        final Map<String, Row> resultMap = result.getRows().stream().collect(Collectors.toMap(row -> (row.getColumns().get("Type").getLabel()), Function.identity()));

        final Row callingSubClassOfGenericClassWithTransactionalMethodResult = resultMap.get(CallingSubClassOfGenericClassWithTransactionalMethod.class.getName());
        assertThat(callingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class));
        assertThat(callingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class, "anotherMethod"));
        assertThat(callingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("TransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(simpleMethodDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class, "void method(java.lang.Object)"));

        final Row callingSubClassOfGenericTransactionalClassResult = resultMap.get(CallingSubClassOfGenericTransactionalClass.class.getName());
        assertThat(callingSubClassOfGenericTransactionalClassResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(CallingSubClassOfGenericTransactionalClass.class));
        assertThat(callingSubClassOfGenericTransactionalClassResult.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(CallingSubClassOfGenericTransactionalClass.class, "anotherMethod"));
        assertThat(callingSubClassOfGenericTransactionalClassResult.getColumns().get("TransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(simpleMethodDescriptor(CallingSubClassOfGenericTransactionalClass.class, "void method(java.lang.Object)"));

        final Row overridingSubClassOfGenericTransactionalClassResult = resultMap.get(OverridingSubClassOfGenericTransactionalClass.class.getName());
        assertThat(overridingSubClassOfGenericTransactionalClassResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(OverridingSubClassOfGenericTransactionalClass.class));
        assertThat(overridingSubClassOfGenericTransactionalClassResult.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(OverridingSubClassOfGenericTransactionalClass.class, "method", Long.class));
        assertThat(overridingSubClassOfGenericTransactionalClassResult.getColumns().get("TransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(GenericTransactionalClass.class, "method", Object.class));

        final Row overridingSubClassOfGenericClassWithTransactionalMethodResult = resultMap.get(OverridingSubClassOfGenericClassWithTransactionalMethod.class.getName());
        assertThat(overridingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(OverridingSubClassOfGenericClassWithTransactionalMethod.class));
        assertThat(overridingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(OverridingSubClassOfGenericClassWithTransactionalMethod.class, "method", Long.class));
        assertThat(overridingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("TransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(GenericClassWithTransactionalMethod.class, "method", Object.class));

        store.commitTransaction();
    }

}
