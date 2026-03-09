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

class TransactionalMethodMustNotBeInvokedFromSameOrSubclassIT extends AbstractJavaPluginIT {

    @ParameterizedTest
    @ValueSource(classes = {JtaJakartaTransactionalClass.class, JtaTransactionalMethod.class,
        JtaTransactionalClass.class, JtaTransactionalMethod.class, SpringTransactionalClass.class,
        SpringTransactionalImplementingClass.class, SpringTransactionalMethod.class})
    void transactionMethodsMustNotBeCalledDirectlyWithViolations(Class<?> clazz) throws Exception {
        scanClasses(SpringTransactionalInterface.class, clazz);
        assertThat(validateConstraint("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass")
            .getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        List<Result<Constraint>> constraintViolations = new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);
        Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRule().getId())
            .isEqualTo("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass");
        assertThat(result.getRows()).hasSize(2);

        final Row row1 = result.getRows().get(0);
        assertThat(row1.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(clazz));
        assertThat(row1.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(clazz, "neverTransactionalCallingRequiredTransactionalTransitively"));
        assertThat(row1.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(clazz, "transactionalMethodWithRequiredSemantics"));

        final Row row2 = result.getRows().get(1);
        assertThat(row2.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(clazz));
        assertThat(row2.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(clazz, "transactionalMethodWithNeverSemantics"));
        assertThat(row2.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(clazz, "transactionalMethodWithRequiredSemantics"));
        store.commitTransaction();
    }

    @Test
    void transactionMethodsMustNotBeCalledDirectlyWithViolationsInSubClass() throws Exception {
        scanClasses(SpringTransactionalClass.class, SpringTransactionalSubClass.class);
        assertThat(validateConstraint("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass")
            .getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        List<Result<Constraint>> constraintViolations = new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);
        Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRule().getId())
            .isEqualTo("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass");
        assertThat(result.getRows()).hasSize(3);

        final Row superClassRow1 = result.getRows().get(0);
        assertThat(superClassRow1.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(SpringTransactionalClass.class));
        assertThat(superClassRow1.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SpringTransactionalClass.class, "neverTransactionalCallingRequiredTransactionalTransitively"));
        assertThat(superClassRow1.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SpringTransactionalClass.class, "transactionalMethodWithRequiredSemantics"));

        final Row superClassRow2 = result.getRows().get(1);
        assertThat(superClassRow2.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(SpringTransactionalClass.class));
        assertThat(superClassRow2.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SpringTransactionalClass.class, "transactionalMethodWithNeverSemantics"));
        assertThat(superClassRow2.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SpringTransactionalClass.class, "transactionalMethodWithRequiredSemantics"));

        final Row subClassRow = result.getRows().get(2);
        assertThat(subClassRow.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(SpringTransactionalSubClass.class));
        assertThat(subClassRow.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SpringTransactionalSubClass.class, "transactionalSubClassMethodWithNeverSemantics"));
        assertThat(subClassRow.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SpringTransactionalSubClass.class, "transactionalSubClassMethodWithRequiredSemantics"));

        store.commitTransaction();
    }

    @Test
    void transactionalMethodMustNotBeInvokedFromSameClassSimpleInheritance() throws Exception {
        scanClasses(SimpleNonTransactionalClass.class, SimpleTransactionalClass.class, SimpleClassWithTransactionalMethod.class,
            CallingSubClassOfSimpleNonTransactionalClass.class, CallingSubClassOfSimpleTransactionalClass.class, CallingSubClassOfSimpleClassWithTransactionalMethod.class,
            OverridingSubClassOfSimpleNonTransactionalClass.class, OverridingSubClassOfSimpleTransactionalClass.class, OverridingSubClassOfSimpleClassWithTransactionalMethod.class);
        assertThat(validateConstraint("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass").getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        final List<Result<Constraint>> constraintViolations = new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);
        final Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRule().getId()).isEqualTo("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass");
        assertThat(result.getRows()).hasSize(4);
        final Map<String, Row> resultMap = result.getRows().stream().collect(Collectors.toMap(row -> (row.getColumns().get("Type").getLabel()), Function.identity()));

        final Row callingSubClassOfSimpleTransactionalClassResult = resultMap.get(CallingSubClassOfSimpleTransactionalClass.class.getName());
        assertThat(callingSubClassOfSimpleTransactionalClassResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(CallingSubClassOfSimpleTransactionalClass.class));
        assertThat(callingSubClassOfSimpleTransactionalClassResult.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(CallingSubClassOfSimpleTransactionalClass.class, "transactionalMethodWithNeverSemantics"));
        assertThat(callingSubClassOfSimpleTransactionalClassResult.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(simpleMethodDescriptor(CallingSubClassOfSimpleTransactionalClass.class, "void methodWithRequiredSemantics()"));

        final Row callingSubClassOfSimpleClassWithTransactionalMethodResult = resultMap.get(CallingSubClassOfSimpleClassWithTransactionalMethod.class.getName());
        assertThat(callingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(CallingSubClassOfSimpleClassWithTransactionalMethod.class));
        assertThat(callingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(CallingSubClassOfSimpleClassWithTransactionalMethod.class, "transactionalMethodWithNeverSemantics"));
        assertThat(callingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(simpleMethodDescriptor(CallingSubClassOfSimpleClassWithTransactionalMethod.class, "void methodWithRequiredSemantics()"));

        final Row overridingSubClassOfSimpleTransactionalClassResult = resultMap.get(OverridingSubClassOfSimpleTransactionalClass.class.getName());
        assertThat(overridingSubClassOfSimpleTransactionalClassResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(OverridingSubClassOfSimpleTransactionalClass.class));
        assertThat(overridingSubClassOfSimpleTransactionalClassResult.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(OverridingSubClassOfSimpleTransactionalClass.class, "oneMoreMethodWithRequiredSemantics"));
        assertThat(overridingSubClassOfSimpleTransactionalClassResult.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SimpleTransactionalClass.class, "oneMoreMethodWithRequiredSemantics"));

        final Row overridingSubClassOfSimpleClassWithTransactionalMethodResult = resultMap.get(OverridingSubClassOfSimpleClassWithTransactionalMethod.class.getName());
        assertThat(overridingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(OverridingSubClassOfSimpleClassWithTransactionalMethod.class));
        assertThat(overridingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(OverridingSubClassOfSimpleClassWithTransactionalMethod.class, "oneMoreMethodWithRequiredSemantics"));
        assertThat(overridingSubClassOfSimpleClassWithTransactionalMethodResult.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(SimpleClassWithTransactionalMethod.class, "oneMoreMethodWithRequiredSemantics"));

        store.commitTransaction();
    }

    @Test
    void transactionalMethodMustNotBeInvokedFromSameClassGenericInheritance() throws Exception {
        scanClasses(GenericNonTransactionalClass.class, GenericTransactionalClass.class, GenericClassWithTransactionalMethod.class,
            CallingSubClassOfGenericNonTransactionalClass.class, CallingSubClassOfGenericTransactionalClass.class, CallingSubClassOfGenericClassWithTransactionalMethod.class,
            OverridingSubClassOfGenericNonTransactionalClass.class, OverridingSubClassOfGenericTransactionalClass.class, OverridingSubClassOfGenericClassWithTransactionalMethod.class);
        assertThat(validateConstraint("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass").getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        final List<Result<Constraint>> constraintViolations = new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);
        final Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRule().getId()).isEqualTo("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass");
        assertThat(result.getRows()).hasSize(4);
        final Map<String, Row> resultMap = result.getRows().stream().collect(Collectors.toMap(row -> (row.getColumns().get("Type").getLabel()), Function.identity()));

        final Row callingSubClassOfGenericClassWithTransactionalMethodResult = resultMap.get(CallingSubClassOfGenericClassWithTransactionalMethod.class.getName());
        assertThat(callingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class));
        assertThat(callingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class, "callingMethodWithNeverSemantics"));
        assertThat(callingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(simpleMethodDescriptor(CallingSubClassOfGenericClassWithTransactionalMethod.class, "void oneMoreMethodWithRequiredSemantics(java.lang.Object)"));

        final Row callingSubClassOfGenericTransactionalClassResult = resultMap.get(CallingSubClassOfGenericTransactionalClass.class.getName());
        assertThat(callingSubClassOfGenericTransactionalClassResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(CallingSubClassOfGenericTransactionalClass.class));
        assertThat(callingSubClassOfGenericTransactionalClassResult.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(CallingSubClassOfGenericTransactionalClass.class, "callingMethodWithNeverSemantics"));
        assertThat(callingSubClassOfGenericTransactionalClassResult.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(simpleMethodDescriptor(CallingSubClassOfGenericTransactionalClass.class, "void oneMoreMethodWithRequiredSemantics(java.lang.Object)"));

        final Row overridingSubClassOfGenericTransactionalClassResult = resultMap.get(OverridingSubClassOfGenericTransactionalClass.class.getName());
        assertThat(overridingSubClassOfGenericTransactionalClassResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(OverridingSubClassOfGenericTransactionalClass.class));
        assertThat(overridingSubClassOfGenericTransactionalClassResult.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(OverridingSubClassOfGenericTransactionalClass.class, "oneMoreMethodWithRequiredSemantics", Long.class));
        assertThat(overridingSubClassOfGenericTransactionalClassResult.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(GenericTransactionalClass.class, "oneMoreMethodWithRequiredSemantics", Object.class));

        final Row overridingSubClassOfGenericClassWithTransactionalMethodResult = resultMap.get(OverridingSubClassOfGenericClassWithTransactionalMethod.class.getName());
        assertThat(overridingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(OverridingSubClassOfGenericClassWithTransactionalMethod.class));
        assertThat(overridingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(OverridingSubClassOfGenericClassWithTransactionalMethod.class, "oneMoreMethodWithRequiredSemantics", Long.class));
        assertThat(overridingSubClassOfGenericClassWithTransactionalMethodResult.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(GenericClassWithTransactionalMethod.class, "oneMoreMethodWithRequiredSemantics", Object.class));

        store.commitTransaction();
    }

    @Test
    void transactionalMethodMustNotBeInvokedFromNestedClass() throws Exception {
        final Class<?> clazz = SpringTransactionalClassWithNestedClass.class;
        final Class<?> nestedClass = clazz.getDeclaredClasses()[0];
        scanClasses(clazz, nestedClass);
        assertThat(validateConstraint("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass")
            .getStatus()).isEqualTo(FAILURE);

        store.beginTransaction();

        final List<Result<Constraint>> constraintViolations =
            new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);

        assertThat(constraintViolations.get(0).getRule().getId())
            .isEqualTo("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass");

        final Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRows()).hasSize(1);
        final Row row = result.getRows().get(0);
        assertThat(row.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(nestedClass));
        assertThat(row.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(nestedClass, "transactionalMethodWithNeverSemantics"));
        assertThat(row.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(clazz, "transactionalMethodWithRequiredSemantics"));

        store.commitTransaction();
    }

    @Test
    void transactionalMethodMustNotBeInvokedFromNestedClassInSubClass() throws Exception {
        final Class<?> clazz = SubClassWithCallingNestedClass.class;
        final Class<?> nestedClass = clazz.getDeclaredClasses()[0];
        final Class<?> superClass = clazz.getSuperclass();
        scanClasses(clazz, nestedClass, superClass);
        assertThat(validateConstraint("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass")
            .getStatus()).isEqualTo(FAILURE);

        store.beginTransaction();

        final List<Result<Constraint>> constraintViolations =
            new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);

        assertThat(constraintViolations.get(0).getRule().getId())
            .isEqualTo("spring-transaction:TransactionChangingMethodMustNotBeInvokedFromSameClassOrSubclass");

        final Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRows()).hasSize(1);
        final Row row = result.getRows().get(0);
        assertThat(row.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(nestedClass));
        assertThat(row.getColumns().get("SourceTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(nestedClass, "transactionalMethodWithNeverSemantics"));
        assertThat(row.getColumns().get("TargetTransactionalMethod").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(superClass, "oneMoreMethodWithRequiredSemantics"));

        store.commitTransaction();
    }

}
