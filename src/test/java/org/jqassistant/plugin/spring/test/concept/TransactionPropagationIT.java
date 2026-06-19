package org.jqassistant.plugin.spring.test.concept;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Concept;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import org.jqassistant.plugin.spring.test.set.transactionpropagation.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionPropagationIT extends AbstractSpringIT {

    @ParameterizedTest
    @ValueSource(classes = {JavaTransactionalApiTypeAndMethodLevelPropagation.class, SpringTransactionalTypeAndMethodLevelPropagation.class,
        JakartaTransactionalApiTypeAndMethodLevelPropagation.class})
    void propagationOnTypeAndMethodLevel(Class<?> testClass) throws RuleException, NoSuchMethodException {
        scanClasses(testClass);

        final Result<Concept> conceptResult = applyConcept("spring-transaction:TransactionalMethod");
        store.beginTransaction();

        assertThat(conceptResult.getStatus()).isEqualTo(Result.Status.SUCCESS);
        assertThat(conceptResult.getRows().size()).isEqualTo(5);

        Row row1 = conceptResult.getRows().get(0);
        assertThat((TypeDescriptor) row1.getColumns().get("Type").getValue())
            .is(typeDescriptor(testClass));
        assertThat((MethodDescriptor) row1.getColumns().get("TransactionalMethod").getValue())
            .is(methodDescriptor(testClass, "transactionalMethodMandatoryWithAdditionalConfig"));
        assertThat(row1.getColumns().get("TransactionPropagation").getLabel()).isEqualTo("MANDATORY");
        assertThat(row1.getColumns().get("AdditionalTransactionConfiguration").getLabel()).isEqualTo("true");

        Row row2 = conceptResult.getRows().get(1);
        assertThat((TypeDescriptor) row2.getColumns().get("Type").getValue())
            .is(typeDescriptor(testClass));
        assertThat((MethodDescriptor) row2.getColumns().get("TransactionalMethod").getValue())
            .is(methodDescriptor(testClass, "transactionalMethodNeverWithoutAdditionalConfig"));
        assertThat(row2.getColumns().get("TransactionPropagation").getLabel()).isEqualTo("NEVER");
        assertThat(row2.getColumns().get("AdditionalTransactionConfiguration").getLabel()).isEqualTo("false");

        Row row3 = conceptResult.getRows().get(2);
        assertThat((TypeDescriptor) row3.getColumns().get("Type").getValue())
            .is(typeDescriptor(testClass));
        assertThat((MethodDescriptor) row3.getColumns().get("TransactionalMethod").getValue())
            .is(methodDescriptor(testClass, "transactionalMethodRequiredWithOverriddenConfig"));
        assertThat(row3.getColumns().get("TransactionPropagation").getLabel()).isEqualTo("REQUIRED");
        assertThat(row3.getColumns().get("AdditionalTransactionConfiguration").getLabel()).isEqualTo("true");

        Row row4 = conceptResult.getRows().get(3);
        assertThat((TypeDescriptor) row4.getColumns().get("Type").getValue())
            .is(typeDescriptor(testClass));
        assertThat((MethodDescriptor) row4.getColumns().get("TransactionalMethod").getValue())
            .is(methodDescriptor(testClass, "transactionalMethodRequiredWithoutAdditionalConfig"));
        assertThat(row4.getColumns().get("TransactionPropagation").getLabel()).isEqualTo("REQUIRED");
        assertThat(row4.getColumns().get("AdditionalTransactionConfiguration").getLabel()).isEqualTo("false");

        Row row5 = conceptResult.getRows().get(4);
        assertThat((TypeDescriptor) row5.getColumns().get("Type").getValue())
            .is(typeDescriptor(testClass));
        assertThat((MethodDescriptor) row5.getColumns().get("TransactionalMethod").getValue())
            .is(methodDescriptor(testClass, "transactionalMethodRequiresNewWithAdditionalConfig"));
        assertThat(row5.getColumns().get("TransactionPropagation").getLabel()).isEqualTo("REQUIRES_NEW");
        assertThat(row5.getColumns().get("AdditionalTransactionConfiguration").getLabel()).isEqualTo("true");

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
        assertThat(row1.getColumns().get("AdditionalTransactionConfiguration").getLabel()).isEqualTo("false");

        Row row2 = conceptResult.getRows().get(1);
        assertThat((TypeDescriptor) row2.getColumns().get("Type").getValue())
            .is(typeDescriptor(testClass));
        assertThat((MethodDescriptor) row2.getColumns().get("TransactionalMethod").getValue())
            .is(methodDescriptor(testClass, "transactionalMethodRequiresNew"));
        assertThat(row2.getColumns().get("TransactionPropagation").getLabel()).isEqualTo("REQUIRES_NEW");
        assertThat(row2.getColumns().get("AdditionalTransactionConfiguration").getLabel()).isEqualTo("true");

        store.commitTransaction();
    }
}
