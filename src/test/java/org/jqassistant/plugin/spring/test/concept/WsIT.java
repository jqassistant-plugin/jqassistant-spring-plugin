package org.jqassistant.plugin.spring.test.concept;

import java.util.List;
import java.util.stream.Collectors;

import com.buschmais.jqassistant.core.report.api.model.Column;
import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Concept;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition;

import org.assertj.core.api.Assertions;
import org.jqassistant.plugin.spring.test.set.components.Endpoint;
import org.jqassistant.plugin.spring.test.set.components.RestController;
import org.junit.jupiter.api.Test;
import org.springframework.ws.test.client.MockWebServiceServer;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.core.report.api.model.Result.Status.WARNING;
import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.matcher.TypeDescriptorMatcher.typeDescriptor;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class WsIT extends AbstractSpringIT {
    @Test
    void endpoint() throws Exception {
        verify(Endpoint.class, "spring-ws:Endpoint",
            ":Spring:Endpoint:Component:Injectable");
    }

    private void verify(Class<?> componentType, String conceptId, String expectedLabels) throws RuleException {
        scanClasses(RestController.class);
        assertThat(applyConcept(conceptId).getStatus(), equalTo(WARNING));
        clearConcepts();
        scanClasses(componentType);
        assertThat(applyConcept(conceptId).getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        assertThat(query("MATCH (c" + expectedLabels + ") RETURN c").getColumn("c"),
            hasItem(typeDescriptor(componentType)));
        store.commitTransaction();
    }

    @Test
    void mockWebServiceServerVerifyMethod() throws Exception {
        scanClasses(AssertExample.class, AssertExample.ExampleResultActions.class);

        final Result<Concept> conceptResult = applyConcept("spring-ws:MockWebServiceServerVerifyMethod");
        Assertions.assertThat(conceptResult.getStatus()).isEqualTo(SUCCESS);

        store.beginTransaction();

        Assertions.assertThat(conceptResult.getRows().size()).isEqualTo(1);
        Assertions.assertThat(conceptResult.getRows()
                .get(0)
                .getColumns()
                .get("assertMethod")
                .getValue()).asInstanceOf(type(MethodDescriptor.class))
                .is(methodDescriptor(MockWebServiceServer.class, "verify"));

        verifyAssertMethodResultGraph();

        store.commitTransaction();
    }

    @Test
    void providedConceptAssertMethod() throws Exception {
        scanClasses(AssertExample.class, AssertExample.ExampleResultActions.class);

        final Result<Concept> conceptResult = applyConcept("java:AssertMethod");
        Assertions.assertThat(conceptResult.getStatus()).isEqualTo(SUCCESS);

        store.beginTransaction();

        final List<TypeDescriptor> declaringTypes = conceptResult.getRows().stream()
            .map(Row::getColumns)
            .map(columns -> columns.get("DeclaringType"))
            .map(Column::getValue)
            .map(TypeDescriptor.class::cast)
            .collect(Collectors.toList());
        Assertions.assertThat(declaringTypes).haveExactly(1,
            TypeDescriptorCondition.typeDescriptor(MockWebServiceServer.class));

        verifyAssertMethodResultGraph();

        store.commitTransaction();
    }

    // Expects an open transaction
    private void verifyAssertMethodResultGraph() throws NoSuchMethodException {
        final TestResult methodQueryResult = query(
            "MATCH (testMethod:Method)-[:INVOKES]->(assertMethod:Method) "
                + "WHERE assertMethod:MockWebServiceServer:Assert "
                + "RETURN testMethod, assertMethod");
        Assertions.assertThat(methodQueryResult.getRows().size()).isEqualTo(1);
        Assertions.assertThat(methodQueryResult.<MethodDescriptor>getColumn("testMethod"))
            .haveExactly(1, methodDescriptor(AssertExample.class, "mockWebServiceServerVerifyExampleMethod"));
        Assertions.assertThat(methodQueryResult.<MethodDescriptor>getColumn("assertMethod"))
            .haveExactly(1, methodDescriptor(MockWebServiceServer.class, "verify"));
    }
}
