package org.jqassistant.plugin.spring.test.concept;

import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;

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

}
