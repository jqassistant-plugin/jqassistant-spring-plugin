package org.jqassistant.plugin.spring.test.constraint;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.plugin.common.api.model.DependsOnDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.TestDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.JavaClassesDirectoryDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;
import org.jqassistant.plugin.spring.test.set.injectables.ConfigurationBeanC;
import org.jqassistant.plugin.spring.test.set.injectables.ConfigurationWithBeanProducer;
import org.jqassistant.plugin.spring.test.set.injectables.ConfigurationWithBeanProducerAndPrivateMethod;
import org.jqassistant.plugin.spring.test.set.injectables.ControllerInstantiatingService;
import org.jqassistant.plugin.spring.test.set.injectables.NonInjectableInstantiatingService;
import org.jqassistant.plugin.spring.test.set.injectables.Service;
import org.jqassistant.plugin.spring.test.set.injectables.subclass.AbstractConfigurationBean;
import org.jqassistant.plugin.spring.test.set.injectables.subclass.ConfigurationBean;
import org.jqassistant.plugin.spring.test.set.injectables.subclass.SubClassOfInjectable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.FAILURE;
import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;


class InjectablesMustNotBeInstatiatedIT extends AbstractJavaPluginIT {

    @Test
    void injectableCreatesInstance() throws Exception {
        scanClasses("a", ControllerInstantiatingService.class, Service.class,
            ConfigurationWithBeanProducer.class, ConfigurationBean.class,
            ConfigurationWithBeanProducerAndPrivateMethod.class, ConfigurationBeanC.class);
        Result<Constraint> result = validateConstraint("spring-injection:InjectablesMustNotBeInstantiated");
        assertThat(result.getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        List<Row> rows = result.getRows();
        assertThat(rows.size()).isEqualTo(2);
        Row row = rows.get(0);

        assertThat((TypeDescriptor) row.getColumns()
            .get("Type")
            .getValue()).is(typeDescriptor(ConfigurationWithBeanProducerAndPrivateMethod.class));
        assertThat((MethodDescriptor) row.getColumns()
            .get("Method")
            .getValue()).is(methodDescriptor(ConfigurationWithBeanProducerAndPrivateMethod.class, "nonBeanProducerInstantiatingBean"));
        assertThat((TypeDescriptor) row.getColumns()
            .get("Injectable")
            .getValue()).is(typeDescriptor(ConfigurationBeanC.class));

        Row row1 = rows.get(1);
        assertThat((TypeDescriptor) row1.getColumns()
            .get("Type")
            .getValue()).is(typeDescriptor(ControllerInstantiatingService.class));
        assertThat((MethodDescriptor) row1.getColumns()
                .get("Method")
                .getValue()).is(methodDescriptor(ControllerInstantiatingService.class, "instantiateService"));
        assertThat((TypeDescriptor) row1.getColumns()
                .get("Injectable")
                .getValue()).is(typeDescriptor(Service.class));
        store.commitTransaction();
    }

    @Test
    void nonInjectableCreatesInstance() throws Exception {
        scanClasses("a", NonInjectableInstantiatingService.class, Service.class, ConfigurationWithBeanProducer.class,
            ConfigurationBean.class, ConfigurationWithBeanProducerAndPrivateMethod.class, ConfigurationBeanC.class);
        Result<Constraint> result = validateConstraint("spring-injection:InjectablesMustNotBeInstantiated");
        assertThat(result.getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        List<Row> rows = result.getRows();
        Row row = rows.get(0);
        assertThat((TypeDescriptor) row.getColumns()
            .get("Type")
            .getValue()).is(typeDescriptor(ConfigurationWithBeanProducerAndPrivateMethod.class));
        assertThat((MethodDescriptor) row.getColumns()
            .get("Method")
            .getValue()).is(methodDescriptor(ConfigurationWithBeanProducerAndPrivateMethod.class, "nonBeanProducerInstantiatingBean"));
        assertThat((TypeDescriptor) row.getColumns()
            .get("Injectable")
            .getValue()).is(typeDescriptor(ConfigurationBeanC.class));
        store.commitTransaction();
    }

    @Test
    void testClasses() throws Exception {
        store.beginTransaction();
        JavaClassesDirectoryDescriptor jar = getArtifactDescriptor("a");
        jar.setType("jar");
        JavaClassesDirectoryDescriptor testJar = getArtifactDescriptor("b");
        store.create(testJar, DependsOnDescriptor.class, jar);
        store.addDescriptorType(testJar, TestDescriptor.class);
        store.commitTransaction();
        scanClasses("a", Service.class);
        scanClasses("b", ControllerInstantiatingService.class);
        assertThat(validateConstraint("spring-injection:InjectablesMustNotBeInstantiated").getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    void configInstantiatesSubClassOfInjectable() throws Exception {
        scanClasses("a", AbstractConfigurationBean.class, ConfigurationBean.class, SubClassOfInjectable.class);
        Result<Constraint> result = validateConstraint("spring-injection:InjectablesMustNotBeInstantiated");
        assertThat(result.getStatus()).isEqualTo(SUCCESS);
    }
}
