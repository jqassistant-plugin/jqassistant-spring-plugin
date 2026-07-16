package org.jqassistant.plugin.spring.test.concept;

import com.buschmais.jqassistant.core.test.plugin.AbstractPluginIT;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;
import org.jqassistant.plugin.spring.test.set.test.AssertExample;

import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Abstract base class for Spring ITs.
 */
public abstract class AbstractSpringIT extends AbstractJavaPluginIT {

    /**
     * Clear the marker nodes for all applied concepts.
     */
    protected void clearConcepts() {
        store.beginTransaction();
        query("MATCH (c:Concept) DETACH DELETE c");
        store.commitTransaction();
    }

    // Expects an open transaction
    protected void verifyResultGraph(String assertMethodLabel, String testMethodName, Class<?> assertClass, String assertMethodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        final AbstractPluginIT.TestResult methodQueryResult = query(
            "MATCH (testMethod:Method)-[:INVOKES]->(assertMethod:Method) "
                + "WHERE assertMethod:Spring:" + assertMethodLabel + ":Assert "
                + "RETURN testMethod, assertMethod");
        assertThat(methodQueryResult.getRows().size()).isEqualTo(1);
        assertThat(methodQueryResult.<MethodDescriptor>getColumn("testMethod"))
            .haveExactly(1, methodDescriptor(AssertExample.class, testMethodName));
        assertThat(methodQueryResult.<MethodDescriptor>getColumn("assertMethod"))
            .haveExactly(1, methodDescriptor(assertClass, assertMethodName, parameterTypes));
    }

}
