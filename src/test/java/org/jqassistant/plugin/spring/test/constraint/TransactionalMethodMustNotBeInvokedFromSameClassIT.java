package org.jqassistant.plugin.spring.test.constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;

import org.jqassistant.plugin.spring.test.set.transaction.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.FAILURE;
import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

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



}
