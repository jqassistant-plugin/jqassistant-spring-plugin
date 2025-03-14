package org.jqassistant.plugin.spring.test.constraint;

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

import java.util.ArrayList;
import java.util.List;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.FAILURE;
import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

class PrivateMethodMustNotBeAnnotatedWithTransactionalIT extends AbstractJavaPluginIT {

    @ParameterizedTest
    @ValueSource(classes = {JtaTransactionalMethod.class, JtaTransactionalMethod.class,
        SpringTransactionalMethod.class})
    void privateMethodMustNotBeAnnotatedWithTransactional(Class<?> clazz) throws Exception {
        scanClasses(clazz);
        assertThat(validateConstraint("spring-transaction:PrivateMethodMustNotBeAnnotatedWithTransactional").getStatus()).isEqualTo(FAILURE);

        store.beginTransaction();

        final List<Result<Constraint>> constraintViolations = new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);

        assertThat(constraintViolations.get(0).getRule().getId())
            .isEqualTo("spring-transaction:PrivateMethodMustNotBeAnnotatedWithTransactional");

        final Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRows()).hasSize(1);
        final Row row = result.getRows().get(0);
        assertThat(row.getColumns().get("Type").getValue()).asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(clazz));
        assertThat(row.getColumns().get("Method").getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(clazz, "privateMethod"));

        store.commitTransaction();
    }

    @Test
    void privateMethodMustNotBeAnnotatedWithTransactionalNoViolations() throws Exception {
        scanClasses(SpringTransactionalInterface.class, JtaJakartaTransactionalClass.class, JtaTransactionalClass.class,
            SpringTransactionalClass.class, SpringTransactionalImplementingClass.class,
            SpringTransactionalSubClass.class);
        assertThat(validateConstraint("spring-transaction:PrivateMethodMustNotBeAnnotatedWithTransactional")
            .getStatus()).isEqualTo(SUCCESS);

        store.beginTransaction();

        final List<Result<Constraint>> constraintViolations = new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);

        assertThat(constraintViolations.get(0).getRule().getId())
            .isEqualTo("spring-transaction:PrivateMethodMustNotBeAnnotatedWithTransactional");

        final Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRows()).isEmpty();

        store.commitTransaction();
    }

}
