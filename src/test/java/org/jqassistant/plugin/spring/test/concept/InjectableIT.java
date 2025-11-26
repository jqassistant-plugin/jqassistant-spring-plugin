package org.jqassistant.plugin.spring.test.concept;

import com.buschmais.jqassistant.plugin.java.api.model.ConstructorDescriptor;
import org.jqassistant.plugin.spring.test.set.application.valid.Application;
import org.jqassistant.plugin.spring.test.set.components.component.ComponentWithCustomAnnotation;
import org.jqassistant.plugin.spring.test.set.components.component.ComponentWithTransitiveCustomAnnotation;
import org.jqassistant.plugin.spring.test.set.components.component.CustomComponentAnnotation;
import org.jqassistant.plugin.spring.test.set.components.component.TransitiveCustomComponentAnnotation;
import org.jqassistant.plugin.spring.test.set.components.controller.*;
import org.jqassistant.plugin.spring.test.set.components.repository.*;
import org.jqassistant.plugin.spring.test.set.components.service.*;
import org.jqassistant.plugin.spring.test.set.components.service.Service;
import org.jqassistant.plugin.spring.test.set.fieldinjection.ServiceWithConstructorInjection;
import org.jqassistant.plugin.spring.test.set.fieldinjection.ServiceWithFieldInjection;
import org.jqassistant.plugin.spring.test.set.injectables.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

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
            Arguments.of(ConfigurationWithBeanProducerAndPrivateMethod.class, ConfigurationBeanC.class),
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
            AnnotatedRepository.class, ImplementedRepository.class, Controller.class,
            RestController.class, Service.class, ConfigurationWithBeanProducerAndPrivateMethod.class,
            ComponentWithCustomAnnotation.class, ComponentWithTransitiveCustomAnnotation.class,
            CustomComponentAnnotation.class, TransitiveCustomComponentAnnotation.class,
            ControllerWithCustomAnnotation.class, ControllerWithTransitiveCustomAnnotation.class,
            CustomControllerAnnotation.class, TransitiveCustomControllerAnnotation.class,
            ServiceWithCustomAnnotation.class, ServiceWithTransitiveCustomAnnotation.class,
            CustomServiceAnnotation.class, TransitiveCustomServiceAnnotation.class,
            RepositoryWithCustomAnnotation.class, RepositoryWithTransitiveCustomAnnotation.class,
            CustomRepositoryAnnotation.class, TransitiveCustomRepositoryAnnotation.class,
            ConfigurationWithCustomAnnotation.class, ConfigurationWithTransitiveCustomAnnotation.class,
            CustomConfigurationAnnotation.class, TransitiveCustomConfigurationAnnotation.class,
            TypeWithAutoConfigurationAnnotation.class, TypeWithConfigurationPropertiesAnnotation.class
        );
        assertThat(applyConcept("spring-injection:Injectable").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> injectables = query("MATCH (i:Type:Spring:Injectable) RETURN i").getColumn("i");
        assertThat(injectables.size(), equalTo(22));
        assertThat(injectables, hasItem(typeDescriptor(Application.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationBeanA.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationBeanB.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationBeanC.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationWithBeanProducer.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationWithBeanProducerAndPrivateMethod.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationWithBeanProducer.NestedConfigurationWithBeanProducer.class)));
        assertThat(injectables, hasItem(typeDescriptor(AnnotatedRepository.class)));
        assertThat(injectables, hasItem(typeDescriptor(ImplementedRepository.class)));
        assertThat(injectables, hasItem(typeDescriptor(Controller.class)));
        assertThat(injectables, hasItem(typeDescriptor(RestController.class)));
        assertThat(injectables, hasItem(typeDescriptor(Service.class)));
        assertThat(injectables, hasItem(typeDescriptor(ComponentWithCustomAnnotation.class)));
        assertThat(injectables, hasItem(typeDescriptor(ComponentWithTransitiveCustomAnnotation.class)));
        assertThat(injectables, hasItem(typeDescriptor(ControllerWithCustomAnnotation.class)));
        assertThat(injectables, hasItem(typeDescriptor(ControllerWithTransitiveCustomAnnotation.class)));
        assertThat(injectables, hasItem(typeDescriptor(ServiceWithCustomAnnotation.class)));
        assertThat(injectables, hasItem(typeDescriptor(ServiceWithTransitiveCustomAnnotation.class)));
        assertThat(injectables, hasItem(typeDescriptor(RepositoryWithCustomAnnotation.class)));
        assertThat(injectables, hasItem(typeDescriptor(RepositoryWithTransitiveCustomAnnotation.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationWithCustomAnnotation.class)));
        assertThat(injectables, hasItem(typeDescriptor(ConfigurationWithTransitiveCustomAnnotation.class)));
        assertThat(injectables, hasItem(typeDescriptor(TypeWithAutoConfigurationAnnotation.class)));
        assertThat(injectables, hasItem(typeDescriptor(TypeWithConfigurationPropertiesAnnotation.class)));
        store.commitTransaction();
    }

}
