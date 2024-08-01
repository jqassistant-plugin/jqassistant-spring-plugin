package org.jqassistant.plugin.spring.test.concept;

import java.util.List;

import org.jqassistant.plugin.spring.test.set.transaction.*;
import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.plugin.java.test.matcher.MethodDescriptorMatcher.methodDescriptor;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class TransactionalMethodIT extends AbstractSpringIT {

    @Test
    void transactionalMethod() throws Exception {
        scanClasses(SpringTransactionalMethod.class, JtaTransactionalMethod.class, JtaJakartaTransactionalMethod.class,
            SpringTransactionalClass.class, SpringTransactionalSubClass.class, SpringTransactionalInterface.class,
            SpringTransactionalImplementingClass.class, JtaTransactionalClass.class,
            JtaJakartaTransactionalClass.class);
        assertThat(applyConcept("spring-transaction:TransactionalMethod").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> methods = query("MATCH (m:Spring:Method:Transactional) RETURN m").getColumn("m");
        assertThat(methods.size(), equalTo(10));
        // method level annotations
        assertThat(methods, hasItem(methodDescriptor(SpringTransactionalMethod.class, "transactionalMethod")));
        assertThat(methods, not(hasItem(methodDescriptor(SpringTransactionalMethod.class, "nonTransactionalMethod"))));
        assertThat(methods, hasItem(methodDescriptor(JtaTransactionalMethod.class, "transactionalMethod")));
        assertThat(methods, hasItem(methodDescriptor(JtaJakartaTransactionalMethod.class, "transactionalMethod")));
        // class level annotations
        assertThat(methods, hasItem(methodDescriptor(SpringTransactionalClass.class, "transactionalMethod")));
        assertThat(methods,
            hasItem(methodDescriptor(SpringTransactionalSubClass.class, "transactionalSubClassMethod")));
        assertThat(methods, hasItem(methodDescriptor(SpringTransactionalInterface.class, "transactionalMethod")));
        assertThat(methods,
            hasItem(methodDescriptor(SpringTransactionalImplementingClass.class, "transactionalMethod")));
        assertThat(methods, hasItem(
            methodDescriptor(SpringTransactionalImplementingClass.class, "transactionalImplementingClassMethod")));
        assertThat(methods, hasItem(methodDescriptor(JtaTransactionalClass.class, "transactionalMethod")));
        assertThat(methods, hasItem(methodDescriptor(JtaJakartaTransactionalClass.class, "transactionalMethod")));
        store.commitTransaction();
    }
}
