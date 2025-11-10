package org.jqassistant.plugin.spring.test.concept;

import org.jqassistant.plugin.spring.test.set.components.component.ComponentWithCustomAnnotation;
import org.jqassistant.plugin.spring.test.set.components.component.ComponentWithCustomMetaAnnotation;
import org.jqassistant.plugin.spring.test.set.components.component.CustomComponentAnnotation;
import org.jqassistant.plugin.spring.test.set.components.component.CustomComponentMetaAnnotation;
import org.jqassistant.plugin.spring.test.set.components.controller.*;
import org.jqassistant.plugin.spring.test.set.components.dependencies.direct.*;
import org.jqassistant.plugin.spring.test.set.components.repository.*;
import org.jqassistant.plugin.spring.test.set.components.security.*;
import org.jqassistant.plugin.spring.test.set.components.service.*;
import org.jqassistant.plugin.spring.test.set.components.service.Service;
import org.jqassistant.plugin.spring.test.set.injectables.*;
import org.junit.jupiter.api.Test;

import java.util.List;

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
            EnableWebSecurityComponent.class, ConfigurationWithCustomAnnotation.class,
            ConfigurationWithCustomMetaAnnotation.class};
        scanClasses(configurationComponents);
        scanClasses(CustomConfigurationAnnotation.class);
        scanClasses(CustomConfigurationMetaAnnotation.class);
        assertThat(applyConcept("spring-component:Configuration").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> configurations = query("MATCH (c:Spring:Configuration) RETURN c").getColumn("c");
        assertThat(configurations.size(), equalTo(9));
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
        scanClasses(Controller.class, ControllerWithCustomAnnotation.class, CustomControllerAnnotation.class,
            ControllerWithCustomMetaAnnotation.class, CustomControllerMetaAnnotation.class);
        assertThat(applyConcept("spring-component:Controller").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> controller = query("MATCH (c:Spring:Controller) RETURN c").getColumn("c");
        assertThat(controller.size(), equalTo(3));
        assertThat(controller, hasItem(typeDescriptor(Controller.class)));
        assertThat(controller, hasItem(typeDescriptor(ControllerWithCustomAnnotation.class)));
        assertThat(controller, hasItem(typeDescriptor(ControllerWithCustomMetaAnnotation.class)));
        store.commitTransaction();
    }

    @Test
    void service() throws Exception {
        scanClasses(AnnotatedRepository.class);
        assertThat(applyConcept("spring-component:Service").getStatus(), equalTo(WARNING));
        clearConcepts();
        scanClasses(Service.class, ServiceWithCustomAnnotation.class, CustomServiceAnnotation.class,
            ServiceWithCustomMetaAnnotation.class, CustomServiceMetaAnnotation.class);
        assertThat(applyConcept("spring-component:Service").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> services = query("MATCH (s:Spring:Service) RETURN s").getColumn("s");
        assertThat(services.size(), equalTo(3));
        assertThat(services, hasItem(typeDescriptor(Service.class)));
        assertThat(services, hasItem(typeDescriptor(ServiceWithCustomAnnotation.class)));
        assertThat(services, hasItem(typeDescriptor(ServiceWithCustomMetaAnnotation.class)));
        store.commitTransaction();
    }

    @Test
    void repository() throws Exception {
        scanClasses(AnnotatedRepository.class, ImplementedRepository.class, RepositoryWithCustomAnnotation.class,
            CustomRepositoryAnnotation.class, RepositoryWithCustomMetaAnnotation.class, CustomRepositoryMetaAnnotation.class);
        assertThat(applyConcept("spring-component:Repository").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> repositories = query("MATCH (r:Spring:Repository) RETURN r").getColumn("r");
        assertThat(repositories.size(), equalTo(4));
        assertThat(repositories, hasItem(typeDescriptor(AnnotatedRepository.class)));
        assertThat(repositories, hasItem(typeDescriptor(ImplementedRepository.class)));
        assertThat(repositories,  hasItem(typeDescriptor(RepositoryWithCustomAnnotation.class)));
        assertThat(repositories, hasItem(typeDescriptor(RepositoryWithCustomMetaAnnotation.class)));
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

    @Test
    void component() throws Exception {
        scanClasses(ComponentWithCustomMetaAnnotation.class, CustomComponentMetaAnnotation.class, CustomComponentAnnotation.class, ComponentWithCustomAnnotation.class);
        assertThat(applyConcept("spring-component:Component").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> metaComponents = query("MATCH (c:Spring:Component) RETURN c").getColumn("c");
        assertThat(metaComponents.size(), equalTo(2));
        assertThat(metaComponents, hasItem(typeDescriptor(ComponentWithCustomMetaAnnotation.class)));
        assertThat(metaComponents, hasItem(typeDescriptor(ComponentWithCustomAnnotation.class)));
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
