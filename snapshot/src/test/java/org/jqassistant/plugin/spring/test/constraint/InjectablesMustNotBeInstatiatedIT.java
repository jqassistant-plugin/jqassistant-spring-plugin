package org.jqassistant.plugin.spring.test.constraint;

import java.util.List;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.plugin.common.api.model.DependsOnDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.TestDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.JavaClassesDirectoryDescriptor;
import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;

import org.hamcrest.Matcher;
import org.jqassistant.plugin.spring.test.set.injectables.ConfigurationWithBeanProducer;
import org.jqassistant.plugin.spring.test.set.injectables.ControllerInstantiatingService;
import org.jqassistant.plugin.spring.test.set.injectables.NonInjectableInstantiatingService;
import org.jqassistant.plugin.spring.test.set.injectables.Service;
import org.jqassistant.plugin.spring.test.set.injectables.subclass.AbstractConfigurationBean;
import org.jqassistant.plugin.spring.test.set.injectables.subclass.ConfigurationBean;
import org.jqassistant.plugin.spring.test.set.injectables.subclass.SubClassOfInjectable;
import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.FAILURE;
import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.core.test.matcher.ConstraintMatcher.constraint;
import static com.buschmais.jqassistant.core.test.matcher.ResultMatcher.result;
import static com.buschmais.jqassistant.plugin.java.test.matcher.MethodDescriptorMatcher.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.matcher.TypeDescriptorMatcher.typeDescriptor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class InjectablesMustNotBeInstatiatedIT extends AbstractJavaPluginIT {

    @Test
    void injectableCreatesInstance() throws Exception {
        scanClasses("a", ControllerInstantiatingService.class, Service.class, ConfigurationWithBeanProducer.class, ConfigurationBean.class);
        Result<Constraint> result = validateConstraint("spring-injection:InjectablesMustNotBeInstantiated");
        assertThat(result.getStatus(), equalTo(FAILURE));
        store.beginTransaction();
        assertThat(result, result(constraint("spring-injection:InjectablesMustNotBeInstantiated")));
        List<Row> rows = result.getRows();
        assertThat(rows.size(), equalTo(1));
        Row row = rows.get(0);
        assertThat(row.getColumns()
            .get("Type")
            .getValue(), (Matcher<? super Object>) typeDescriptor(ControllerInstantiatingService.class));
        assertThat(row.getColumns()
                .get("Method")
                .getValue(),
            (Matcher<? super Object>) methodDescriptor(ControllerInstantiatingService.class, "instantiateService"));
        assertThat(row.getColumns()
            .get("Injectable")
            .getValue(), (Matcher<? super Object>) typeDescriptor(Service.class));
        store.commitTransaction();
    }

    @Test
    void nonInjectableCreatesInstance() throws Exception {
        scanClasses("a", NonInjectableInstantiatingService.class, Service.class, ConfigurationWithBeanProducer.class, ConfigurationBean.class);
        Result<Constraint> result = validateConstraint("spring-injection:InjectablesMustNotBeInstantiated");
        assertThat(result.getStatus(), equalTo(SUCCESS));
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
        assertThat(validateConstraint("spring-injection:InjectablesMustNotBeInstantiated").getStatus(), equalTo(SUCCESS));
    }

    @Test
    void configInstantiatesSubClassOfInjectable() throws Exception {
        scanClasses("a", AbstractConfigurationBean.class, ConfigurationBean.class, SubClassOfInjectable.class);
        Result<Constraint> result = validateConstraint("spring-injection:InjectablesMustNotBeInstantiated");
        assertThat(result.getStatus(), equalTo(SUCCESS));
    }
}
