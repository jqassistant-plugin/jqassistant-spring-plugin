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

import org.jqassistant.plugin.spring.test.set.components.controller.Endpoint;
import org.jqassistant.plugin.spring.test.set.components.controller.RestController;
import org.jqassistant.plugin.spring.test.set.test.AssertExample;
import org.junit.jupiter.api.Test;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.ws.test.server.ResponseActions;
import org.springframework.ws.test.server.ResponseMatcher;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.core.report.api.model.Result.Status.WARNING;
import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

public class WsIT extends AbstractSpringIT {
    @Test
    void endpoint() throws Exception {
        verify(Endpoint.class, "spring-ws:Endpoint",
            ":Spring:Endpoint:Component:Injectable");
    }

    private void verify(Class<?> componentType, String conceptId, String expectedLabels) throws RuleException {
        scanClasses(RestController.class);
        assertThat(applyConcept(conceptId).getStatus()).isEqualTo(WARNING);
        clearConcepts();
        scanClasses(componentType);
        assertThat(applyConcept(conceptId).getStatus()).isEqualTo(SUCCESS);
        store.beginTransaction();
        final List<TypeDescriptor> types = query("MATCH (c" + expectedLabels + ") RETURN c").getColumn("c");
        assertThat(types).haveExactly(1, typeDescriptor(componentType));
        store.commitTransaction();
    }

    @Test
    void mockWebServiceServerVerifyMethod() throws Exception {
        scanClasses(AssertExample.class, AssertExample.ExampleResultActions.class);

        final Result<Concept> conceptResult = applyConcept("spring-ws:MockWebServiceServerVerifyMethod");
        assertThat(conceptResult.getStatus()).isEqualTo(SUCCESS);

        store.beginTransaction();

        assertThat(conceptResult.getRows().size()).isEqualTo(1);
        assertThat(conceptResult.getRows()
                .get(0)
                .getColumns()
                .get("assertMethod")
                .getValue()).asInstanceOf(type(MethodDescriptor.class))
                .is(methodDescriptor(MockWebServiceServer.class, "verify"));

        verifyResultGraph("MockWebServiceServer", "mockWebServiceServerVerifyExampleMethod", MockWebServiceServer.class, "verify");

        store.commitTransaction();
    }

    @Test
    void providedConceptMockWebServiceAssertMethod() throws Exception {
        scanClasses(AssertExample.class, AssertExample.ExampleResultActions.class);

        final Result<Concept> conceptResult = applyConcept("java:AssertMethod");
        assertThat(conceptResult.getStatus()).isEqualTo(SUCCESS);

        store.beginTransaction();

        final List<TypeDescriptor> declaringTypes = conceptResult.getRows().stream()
            .map(Row::getColumns)
            .map(columns -> columns.get("DeclaringType"))
            .map(Column::getValue)
            .map(TypeDescriptor.class::cast)
            .collect(Collectors.toList());
        assertThat(declaringTypes).haveExactly(1,
            typeDescriptor(MockWebServiceServer.class));

        verifyResultGraph("MockWebServiceServer", "mockWebServiceServerVerifyExampleMethod", MockWebServiceServer.class, "verify");

        store.commitTransaction();
    }

    @Test
    void webServiceAssertMethod() throws Exception {
        scanClasses(AssertExample.class);

        final Result<Concept> conceptResult = applyConcept("spring-ws:ServerResponseAssertion");
        assertThat(conceptResult.getStatus()).isEqualTo(SUCCESS);

        store.beginTransaction();

        assertThat(conceptResult.getRows().size()).isEqualTo(1);
        assertThat(conceptResult.getRows()
            .get(0)
            .getColumns()
            .get("assertMethod")
            .getValue()).asInstanceOf(type(MethodDescriptor.class))
            .is(methodDescriptor(ResponseActions.class, "andExpect", ResponseMatcher.class));

        verifyResultGraph("ServerResponse", "springWebServiceAssertExampleMethod", ResponseActions.class, "andExpect", ResponseMatcher.class);

        store.commitTransaction();
    }

    @Test
    void providedConceptWebServiceAssertMethod() throws Exception {
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
        assertThat(declaringTypes).haveExactly(1, typeDescriptor(ResponseActions.class));

        verifyResultGraph("ServerResponse", "springWebServiceAssertExampleMethod", ResponseActions.class, "andExpect", ResponseMatcher.class);

        store.commitTransaction();
    }
}
