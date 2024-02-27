package org.jqassistant.plugin.spring.test.concept;

import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import org.jqassistant.plugin.spring.test.set.components.Endpoint;
import org.jqassistant.plugin.spring.test.set.components.RestController;
import org.jqassistant.plugin.spring.test.set.components.Service;
import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.core.report.api.model.Result.Status.WARNING;
import static com.buschmais.jqassistant.plugin.java.test.matcher.TypeDescriptorMatcher.typeDescriptor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebserviceIT extends AbstractSpringIT {
    @Test
    void endpoint() throws Exception {
        verify(Endpoint.class, "spring-webservice:Endpoint",
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
}
