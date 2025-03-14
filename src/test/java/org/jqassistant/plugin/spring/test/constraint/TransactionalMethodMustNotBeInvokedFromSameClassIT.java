package org.jqassistant.plugin.spring.test.constraint;

import java.util.ArrayList;
import java.util.List;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;

import org.jqassistant.plugin.spring.test.set.transaction.SpringTransactionalMethod;
import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.FAILURE;
import static org.assertj.core.api.Assertions.assertThat;

class TransactionalMethodMustNotBeInvokedFromSameClassIT extends AbstractJavaPluginIT {

    @Test
    void transactionMethodsMustNotBeCalledDirectly() throws Exception {
        scanClasses(SpringTransactionalMethod.class);
        assertThat(validateConstraint("spring-transaction:TransactionalMethodMustNotBeInvokedFromSameClass")
            .getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        List<Result<Constraint>> constraintViolations = new ArrayList<>(reportPlugin.getConstraintResults().values());
        assertThat(constraintViolations).hasSize(1);
        Result<Constraint> result = constraintViolations.get(0);
        assertThat(result.getRule().getId())
            .isEqualTo("spring-transaction:TransactionalMethodMustNotBeInvokedFromSameClass");
        List<Row> rows = result.getRows();
        assertThat(rows).hasSize(1);
        store.commitTransaction();
    }

}
