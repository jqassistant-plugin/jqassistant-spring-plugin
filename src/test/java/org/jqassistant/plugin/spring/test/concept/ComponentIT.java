package org.jqassistant.plugin.spring.test.concept;

import java.util.List;

import org.jqassistant.plugin.spring.test.set.components.AnnotatedRepository;
import org.jqassistant.plugin.spring.test.set.components.Controller;
import org.jqassistant.plugin.spring.test.set.components.ImplementedRepository;
import org.jqassistant.plugin.spring.test.set.components.Service;
import org.jqassistant.plugin.spring.test.set.components.dependencies.direct.*;
import org.jqassistant.plugin.spring.test.set.components.security.*;
import org.jqassistant.plugin.spring.test.set.injectables.ConfigurationWithBeanProducer;
import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.core.report.api.model.Result.Status.WARNING;
import static com.buschmais.jqassistant.plugin.java.test.matcher.TypeDescriptorMatcher.typeDescriptor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

class ComponentIT extends AbstractSpringIT {

    @Test
    void configuration() throws Exception {
        Class<?>[] configurationComponents = { ConfigurationWithBeanProducer.class, EnableGlobalAuthenticationComponent.class,
            EnableGlobalMethodSecurityComponent.class, EnableReactiveMethodSecurityComponent.class,
            EnableWebFluxSecurityComponent.class, EnableWebMvcSecurityComponent.class,
            EnableWebSecurityComponent.class };
        scanClasses(configurationComponents);
        assertThat(applyConcept("spring-component:Configuration").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> configurations = query("MATCH (c:Spring:Configuration) RETURN c").getColumn("c");
        for (Class<?> configurationComponent : configurationComponents) {
            assertThat(configurations, hasItem(typeDescriptor(configurationComponent)));
        }
        store.commitTransaction();
    }

    @Test
    void controller() throws Exception {
        scanClasses(Service.class);
        assertThat(applyConcept("spring-component:Controller").getStatus(), equalTo(WARNING));
        clearConcepts();
        scanClasses(Controller.class);
        assertThat(applyConcept("spring-component:Controller").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        assertThat(query("MATCH (c:Spring:Controller) RETURN c").getColumn("c"), hasItem(typeDescriptor(Controller.class)));
        store.commitTransaction();
    }

    @Test
    void service() throws Exception {
        scanClasses(AnnotatedRepository.class);
        assertThat(applyConcept("spring-component:Service").getStatus(), equalTo(WARNING));
        clearConcepts();
        scanClasses(Service.class);
        assertThat(applyConcept("spring-component:Service").getStatus(), equalTo(SUCCESS));

        store.beginTransaction();
        assertThat(query("MATCH (s:Spring:Service) RETURN s").getColumn("s"), hasItem(typeDescriptor(Service.class)));
        store.commitTransaction();
    }

    @Test
    void repository() throws Exception {
        scanClasses(AnnotatedRepository.class, ImplementedRepository.class);
        assertThat(applyConcept("spring-component:Repository").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> repositories = query("MATCH (r:Spring:Repository) RETURN r").getColumn("r");
        assertThat(repositories, hasItem(typeDescriptor(AnnotatedRepository.class)));
        assertThat(repositories, hasItem(typeDescriptor(ImplementedRepository.class)));
        store.commitTransaction();
    }

    @Test
    void directComponentDependencies() throws Exception {
        scanClasses(TestController1.class, TestService1.class, TestService2.class, TestRepository1.class, TestRepository2.class, TestComponent.class);
        assertThat(applyConcept("spring-component:Controller").getStatus(), equalTo(SUCCESS));
        assertThat(applyConcept("spring-component:Service").getStatus(), equalTo(SUCCESS));
        assertThat(applyConcept("spring-component:Repository").getStatus(), equalTo(SUCCESS));
        assertThat(applyConcept("spring-component:Component").getStatus(), equalTo(SUCCESS));
        verifyComponentDependencies("MATCH (:Spring:Controller{name:'TestController1'})-[:DEPENDS_ON]->(c:Spring:Component) RETURN c", TestService1.class, TestRepository1.class, TestComponent.class);
        verifyComponentDependencies("MATCH (:Spring:Service{name:'TestService1'})-[:DEPENDS_ON]->(c:Spring:Component) RETURN c", TestService2.class, TestRepository1.class, TestComponent.class);
        verifyComponentDependencies("MATCH (:Spring:Repository{name:'TestRepository1'})-[:DEPENDS_ON]->(c:Spring:Component) RETURN c", TestRepository2.class, TestComponent.class);
    }

    private void verifyComponentDependencies(String query, Class<?>... dependencies) {
        store.beginTransaction();
        TestResult result = query(query);
        assertThat(result.getRows().size(), equalTo(dependencies.length));
        List<Object> components = result.getColumn("c");
        for (Class<?> dependency : dependencies) {
            assertThat(components, hasItem(typeDescriptor(dependency)));
        }
        store.commitTransaction();
    }
}
