package org.jqassistant.plugin.spring.test.concept;

import org.jqassistant.plugin.spring.test.set.components.repository.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.core.report.api.model.Result.Status.WARNING;
import static com.buschmais.jqassistant.plugin.java.test.matcher.TypeDescriptorMatcher.typeDescriptor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

class RepositoryIT extends AbstractSpringIT {

    @Test
    void annotatedRepository() throws Exception {
        scanClasses(ImplementedRepository.class);
        assertThat(applyConcept("spring-data:AnnotatedRepository").getStatus(), equalTo(WARNING));
        clearConcepts();
        scanClasses(AnnotatedRepository.class, RepositoryWithCustomAnnotation.class,
            RepositoryWithTransitiveCustomAnnotation.class, CustomRepositoryAnnotation.class,
            TransitiveCustomRepositoryAnnotation.class);
        assertThat(applyConcept("spring-data:AnnotatedRepository").getStatus(), equalTo(SUCCESS));

        store.beginTransaction();
        List<Object> repositories = query("MATCH (r:Spring:Repository) RETURN r").getColumn("r");
        assertThat(repositories.size(), equalTo(3));
        assertThat(repositories, hasItem(typeDescriptor(AnnotatedRepository.class)));
        assertThat(repositories, hasItem(typeDescriptor(RepositoryWithCustomAnnotation.class)));
        assertThat(repositories, hasItem(typeDescriptor(RepositoryWithTransitiveCustomAnnotation.class)));
        store.commitTransaction();
    }

    @Test
    void implementedRepository() throws Exception {
        scanClasses(AnnotatedRepository.class);
        assertThat(applyConcept("spring-data:ImplementedRepository").getStatus(), equalTo(WARNING));
        clearConcepts();
        scanClasses(ImplementedRepository.class);
        assertThat(applyConcept("spring-data:ImplementedRepository").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        assertThat(query("MATCH (r:Spring:Repository) RETURN r").getColumn("r"), hasItem(typeDescriptor(ImplementedRepository.class)));
        store.commitTransaction();
    }
}
