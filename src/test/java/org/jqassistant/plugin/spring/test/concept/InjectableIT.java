package org.jqassistant.plugin.spring.test.concept;

import java.util.List;
import java.util.stream.Stream;

import com.buschmais.jqassistant.plugin.java.api.model.ConstructorDescriptor;

import org.jqassistant.plugin.spring.test.set.application.valid.Application;
import org.jqassistant.plugin.spring.test.set.components.*;
import org.jqassistant.plugin.spring.test.set.fieldinjection.ServiceWithConstructorInjection;
import org.jqassistant.plugin.spring.test.set.fieldinjection.ServiceWithFieldInjection;
import org.jqassistant.plugin.spring.test.set.injectables.ConfigurationBeanA;
import org.jqassistant.plugin.spring.test.set.injectables.ConfigurationBeanB;
import org.jqassistant.plugin.spring.test.set.injectables.ConfigurationWithBeanProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.plugin.java.test.matcher.MethodDescriptorMatcher.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.matcher.TypeDescriptorMatcher.typeDescriptor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

class InjectableIT extends AbstractSpringIT {

    private static Stream<Arguments> beanMethodParams() {
        return Stream.of(
            Arguments.of(ConfigurationWithBeanProducer.class, ConfigurationBeanA.class),
            Arguments.of(ConfigurationWithBeanProducer.NestedConfigurationWithBeanProducer.class, ConfigurationBeanB.class)
        );
    }

    @ParameterizedTest
    @MethodSource("beanMethodParams")
    void beanMethod(Class<?> configuration, Class<?> beanType) throws Exception {
        scanClasses(configuration);
        assertThat(applyConcept("spring-injection:BeanProducer").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> injectables = query("MATCH (i:Type:Spring:Injectable) RETURN i").getColumn("i");
        assertThat(injectables.size(), equalTo(1));
        assertThat(injectables, hasItem(typeDescriptor(beanType)));
        List<Object> methods = query("MATCH (m:Spring:BeanProducer:Method) RETURN m").getColumn("m");
        assertThat(methods.size(), equalTo(1));
        assertThat(methods, hasItem(methodDescriptor(configuration, "getConfiguration")));
        store.commitTransaction();
    }

    @Test
    void injectionPoint() throws Exception {
        scanClasses(ServiceWithConstructorInjection.class, ServiceWithFieldInjection.class);
        assertThat(applyConcept("spring-injection:InjectionPoint").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<ConstructorDescriptor> constructors = query("MATCH (c:Constructor:Spring:InjectionPoint) RETURN c").getColumn("c");
        assertThat(constructors.size(), equalTo(1));
        // assertThat(constructors, (Matcher<? super List<ConstructorDescriptor>>)
        // hasItem(constructorDescriptor(ServiceWithConstructorInjection.class,
        // Repository.class)));
        List<ConstructorDescriptor> fields = query("MATCH (f:Field:Spring:InjectionPoint) RETURN f").getColumn("f");
        assertThat(fields.size(), equalTo(1));
        // assertThat(fields, (Matcher<? super List<ConstructorDescriptor>>)
        // hasItem(fieldDescriptor(ServiceWithFieldInjection.class, "repository")));
        store.commitTransaction();
    }

    @Test
    void injectable() throws Exception {
        scanClasses(Application.class, ConfigurationWithBeanProducer.class, ConfigurationWithBeanProducer.NestedConfigurationWithBeanProducer.class,
            AnnotatedRepository.class, ImplementedRepository.class, Controller.class, RestController.class, Service.class);
        assertThat(applyConcept("spring-injection:Injectable").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> injectables = query("MATCH (i:Type:Spring:Injectable) RETURN i").getColumn("i");
        assertThat(injectables.size(), equalTo(10));
        assertThat(injectables, hasItem(typeDescriptor(Application.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationBeanA.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationBeanB.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationWithBeanProducer.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationWithBeanProducer.NestedConfigurationWithBeanProducer.class)));
        assertThat(injectables, hasItem(typeDescriptor(AnnotatedRepository.class)));
        assertThat(injectables, hasItem(typeDescriptor(ImplementedRepository.class)));
        assertThat(injectables, hasItem(typeDescriptor(Controller.class)));
        assertThat(injectables, hasItem(typeDescriptor(RestController.class)));
        assertThat(injectables, hasItem(typeDescriptor(Service.class)));
        store.commitTransaction();
    }

}
