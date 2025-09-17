package org.jqassistant.plugin.spring.test.constraint;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Result.Status;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Clock;

import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

/**
 * Integration tests for rules rejecting static references to injectables.
 *
 * @author Stephan Pirnbaum
 */
class JdkClassesMustNotBeInjectablesIT extends AbstractJavaPluginIT {

    @Test
    void reportsInjectableJdkClasses() throws Exception {

        scanClasses(MyComponent.class);

        Result<Constraint> result = validateConstraint("spring-injection:JdkClassesMustNotBeInjectables");

        store.beginTransaction();

        assertThat(result.getStatus()).isEqualTo(Status.FAILURE);
        assertThat(result.getRows()).hasSize(1);

        Row row = result.getRows().get(0);

        assertThat(row.getColumns().get("Injectable").getValue())
            .asInstanceOf(type(TypeDescriptor.class))
            .is(typeDescriptor(Object.class));

        store.rollbackTransaction();
    }

    @Component
    static class MyComponent {

        @Bean
        public Object someMethod() {
            return null;
        }

        @Bean
        public Clock produceClock() {
            return Clock.systemUTC();
        }

    }

}
