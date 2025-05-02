package org.jqassistant.plugin.spring.test.concept;

import java.util.List;
import java.util.stream.Collectors;

import com.buschmais.jqassistant.core.report.api.model.Column;
import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Concept;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;

import org.jqassistant.plugin.spring.test.set.test.AssertExample;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

public class TestIT extends AbstractSpringIT {

    @Test
    void resultActionsAndReturnMethod() throws Exception {
        scanClasses(AssertExample.class);

        final Result<Concept> conceptResult = applyConcept("spring-test:ResultActionsAssertMethod");
        assertThat(conceptResult.getStatus()).isEqualTo(SUCCESS);

        store.beginTransaction();

        assertThat(conceptResult.getRows().size()).isEqualTo(1);
        assertThat(conceptResult.getRows()
                .get(0)
                .getColumns()
                .get("assertMethod")
                .getValue()).asInstanceOf(type(MethodDescriptor.class))
                .is(methodDescriptor(ResultActions.class, "andReturn"));

        verifyAssertMethodResultGraph();

        store.commitTransaction();
    }

    @Test
    void providedConceptAssertMethod() throws Exception {
        scanClasses(AssertExample.class);

        final Result<Concept> conceptResult = applyConcept("java:AssertMethod");
        assertThat(conceptResult.getStatus()).isEqualTo(SUCCESS);

        store.beginTransaction();

        final List<TypeDescriptor> declaringTypes = conceptResult.getRows().stream()
            .map(Row::getColumns)
            .map(columns -> columns.get("DeclaringType"))
            .map(Column::getValue)
            .map(TypeDescriptor.class::cast)
            .collect(Collectors.toList());
        assertThat(declaringTypes).haveExactly(1, typeDescriptor(ResultActions.class));

        verifyAssertMethodResultGraph();

        store.commitTransaction();
    }

    // Expects an open transaction
    private void verifyAssertMethodResultGraph() throws NoSuchMethodException {
        final TestResult methodQueryResult = query(
            "MATCH (testMethod:Method)-[:INVOKES]->(assertMethod:Method) "
                + "WHERE assertMethod:Spring:Assert "
                + "RETURN testMethod, assertMethod");
        assertThat(methodQueryResult.getRows().size()).isEqualTo(1);
        assertThat(methodQueryResult.<MethodDescriptor>getColumn("testMethod"))
            .haveExactly(1, methodDescriptor(AssertExample.class, "resultActionsAndReturnExampleMethod"));
        assertThat(methodQueryResult.<MethodDescriptor>getColumn("assertMethod"))
            .haveExactly(1, methodDescriptor(ResultActions.class, "andReturn"));
    }
}
