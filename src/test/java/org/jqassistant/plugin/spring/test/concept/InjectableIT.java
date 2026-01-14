package org.jqassistant.plugin.spring.test.concept;

import com.buschmais.jqassistant.plugin.java.api.model.ConstructorDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
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

import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(applyConcept("spring-injection:BeanProducer").getStatus()).isEqualTo(SUCCESS);
        store.beginTransaction();
        List<TypeDescriptor> injectables = query("MATCH (i:Type:Spring:Injectable) RETURN i").getColumn("i");
        assertThat(injectables.size()).isEqualTo(1);
        assertThat(injectables).haveExactly(1, typeDescriptor(beanType));
        List<MethodDescriptor> methods = query("MATCH (m:Spring:BeanProducer:Method) RETURN m").getColumn("m");
        assertThat(methods.size()).isEqualTo(1);
        assertThat(methods).haveExactly(1, methodDescriptor(configuration, "getConfiguration"));
        store.commitTransaction();
    }

    @Test
    void injectionPoint() throws Exception {
        scanClasses(ServiceWithConstructorInjection.class, ServiceWithFieldInjection.class);
        assertThat(applyConcept("spring-injection:InjectionPoint").getStatus()).isEqualTo(SUCCESS);
        store.beginTransaction();
        List<ConstructorDescriptor> constructors = query("MATCH (c:Constructor:Spring:InjectionPoint) RETURN c").getColumn("c");
        assertThat(constructors.size()).isEqualTo(1);
        // assertThat(constructors, (Matcher<? super List<ConstructorDescriptor>>)
        // hasItem(constructorDescriptor(ServiceWithConstructorInjection.class,
        // Repository.class)));
        List<ConstructorDescriptor> fields = query("MATCH (f:Field:Spring:InjectionPoint) RETURN f").getColumn("f");
        assertThat(fields.size()).isEqualTo(1);
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
            TypeWithAutoConfigurationAnnotation.class, TypeWithConfigurationPropertiesAnnotation.class,
            JavaxConstraintValidator.class, JakartaConstraintValidator.class
        );
        assertThat(applyConcept("spring-injection:Injectable").getStatus()).isEqualTo(SUCCESS);
        store.beginTransaction();
        List<TypeDescriptor> injectables = query("MATCH (i:Type:Spring:Injectable) RETURN i").getColumn("i");
        assertThat(injectables.size()).isEqualTo(28);
        assertThat(injectables).haveExactly(1, typeDescriptor(Application.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ConfigurationBeanA.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ConfigurationBeanB.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ConfigurationBeanC.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ConfigurationWithBeanProducer.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ConfigurationWithBeanProducerAndPrivateMethod.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ConfigurationWithBeanProducer.NestedConfigurationWithBeanProducer.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(AnnotatedRepository.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ImplementedRepository.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(Controller.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(RestController.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(Service.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ComponentWithCustomAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ComponentWithTransitiveCustomAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ControllerWithCustomAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ControllerWithTransitiveCustomAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ServiceWithCustomAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ServiceWithTransitiveCustomAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(RepositoryWithCustomAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(RepositoryWithTransitiveCustomAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ConfigurationWithCustomAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(ConfigurationWithTransitiveCustomAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(TypeWithAutoConfigurationAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(TypeWithConfigurationPropertiesAnnotation.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(javax.validation.ConstraintValidator.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(jakarta.validation.ConstraintValidator.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(JavaxConstraintValidator.class));
        assertThat(injectables).haveExactly(1, typeDescriptor(JakartaConstraintValidator.class));
        store.commitTransaction();
    }

    private static Stream<Arguments> constraintValidatorParams() {
        return Stream.of(
            Arguments.of(JavaxConstraintValidator.class, javax.validation.ConstraintValidator.class),
            Arguments.of(JakartaConstraintValidator.class, jakarta.validation.ConstraintValidator.class)
        );
    }

    @ParameterizedTest
    @MethodSource("constraintValidatorParams")
    void constraintValidator(Class<?> validatorClass, Class<?> validatorInterface) throws Exception {
        // The validator interface itself is part of the framework. Hence, it is not explicitly scanned but identified
        // anyway when the implementing class is scanned.
        scanClasses(validatorClass);
        assertThat(applyConcept("spring-injection:ConstraintValidator").getStatus()).isEqualTo(SUCCESS);
        store.beginTransaction();
        List<TypeDescriptor> injectables = query("MATCH (i:Type:Spring:Injectable) RETURN i").getColumn("i");
        assertThat(injectables.size()).isEqualTo(2);
        assertThat(injectables).haveExactly(1, typeDescriptor(validatorClass));
        assertThat(injectables).haveExactly(1, typeDescriptor(validatorInterface));
        store.commitTransaction();
    }

}
