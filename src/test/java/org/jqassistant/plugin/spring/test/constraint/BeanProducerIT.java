package org.jqassistant.plugin.spring.test.constraint;

import java.util.List;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.plugin.java.api.model.InvokesDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;

import org.jqassistant.plugin.spring.test.set.injectables.ConfigurationBeanA;
import org.jqassistant.plugin.spring.test.set.injectables.ConfigurationWithBeanProducer;
import org.jqassistant.plugin.spring.test.set.injectables.ConfigurationWithBeanProducerAndPrivateMethod;
import org.jqassistant.plugin.spring.test.set.injectables.ServiceInvokingBeanProducer;
import org.jqassistant.plugin.spring.test.set.injectables.ServiceWithBeanProducer;
import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.FAILURE;
import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.plugin.java.test.matcher.MethodDescriptorMatcher.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.matcher.TypeDescriptorMatcher.typeDescriptor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

class BeanProducerIT extends AbstractJavaPluginIT {

    @Test
    public void beanProducerInConfigurationComponent() throws Exception {
        scanClasses(ConfigurationWithBeanProducer.class, ConfigurationWithBeanProducer.NestedConfigurationWithBeanProducer.class,
            ConfigurationWithBeanProducerAndPrivateMethod.class);
        assertThat(
            validateConstraint("spring-injection:BeanProducerMustBeDeclaredInConfigurationComponent").getStatus(),
            equalTo(SUCCESS));
    }

    @Test
    void beanProducerInServiceComponent() throws Exception {
        scanClasses(ServiceWithBeanProducer.class);
        Result<Constraint> result = validateConstraint(
            "spring-injection:BeanProducerMustBeDeclaredInConfigurationComponent");
        store.beginTransaction();
        assertThat(result.getStatus(), equalTo(FAILURE));
        List<Row> rows = result.getRows();
        assertThat(rows.size(), equalTo(1));
        Row row = rows.get(0);
        assertThat((MethodDescriptor) row.getColumns()
            .get("BeanProducer")
            .getValue(), methodDescriptor(ServiceWithBeanProducer.class, "getBean"));
        assertThat((TypeDescriptor) row.getColumns()
            .get("Injectable")
            .getValue(), typeDescriptor(ConfigurationBeanA.class));
        store.commitTransaction();
    }

    @Test
    void beanProducerMustNotBeInvokedDirectly() throws Exception {
        scanClasses(ConfigurationWithBeanProducer.class, ServiceInvokingBeanProducer.class);
        Result<Constraint> result = validateConstraint("spring-injection:BeanProducerMustNotBeInvokedDirectly");
        store.beginTransaction();
        assertThat(result.getStatus(), equalTo(FAILURE));
        List<Row> rows = result.getRows();
        assertThat(rows.size(), equalTo(1));
        Row row = rows.get(0);
        assertThat((TypeDescriptor) row.getColumns()
            .get("Type")
            .getValue(), typeDescriptor(ServiceInvokingBeanProducer.class));
        Object invokes = row.getColumns().get("Invocation").getValue();
        assertThat(invokes, instanceOf(InvokesDescriptor.class));
        assertThat(((InvokesDescriptor) invokes).getInvokingMethod(),
            methodDescriptor(ServiceInvokingBeanProducer.class, "doSomething"));
        assertThat((TypeDescriptor) row.getColumns()
            .get("BeanProducerType")
            .getValue(), typeDescriptor(ConfigurationWithBeanProducer.class));
        assertThat((MethodDescriptor) row.getColumns()
            .get("BeanProducer")
            .getValue(), methodDescriptor(ConfigurationWithBeanProducer.class, "getConfiguration"));
        store.commitTransaction();
    }
}
