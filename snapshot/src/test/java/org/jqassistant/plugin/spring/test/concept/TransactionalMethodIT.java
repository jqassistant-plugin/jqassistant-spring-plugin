package org.jqassistant.plugin.spring.test.concept;

import java.util.List;

import org.jqassistant.plugin.spring.test.set.transaction.*;
import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.plugin.java.test.matcher.MethodDescriptorMatcher.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.matcher.TypeDescriptorMatcher.typeDescriptor;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class TransactionalMethodIT extends AbstractSpringIT {

    @Test
    void transactionalMethod() throws Exception {
        scanClasses(SpringTransactionalMethod.class, JtaTransactionalMethod.class, JtaJakartaTransactionalMethod.class);
        assertThat(applyConcept("spring-transaction:TransactionalMethod").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> methods = query("MATCH (m:Spring:Method:Transactional) RETURN m").getColumn("m");
        assertThat(methods.size(), equalTo(3));
        assertThat(methods, hasItem(methodDescriptor(SpringTransactionalMethod.class, "transactionalMethod")));
        assertThat(methods, not(hasItem(methodDescriptor(SpringTransactionalMethod.class, "nonTransactionalMethod"))));
        assertThat(methods, hasItem(methodDescriptor(JtaTransactionalMethod.class, "transactionalMethod")));
        assertThat(methods, hasItem(methodDescriptor(JtaJakartaTransactionalMethod.class, "transactionalMethod")));
        store.commitTransaction();
    }

    @Test
    void transactionalClass() throws Exception {
        scanClasses(SpringTransactionalClass.class, SpringTransactionalSubClass.class,
            SpringTransactionalInterface.class, SpringTransactionalImplementingClass.class,
            JtaTransactionalClass.class, JtaJakartaTransactionalClass.class);
        assertThat(applyConcept("spring-transaction:TransactionalClass").getStatus(), equalTo(SUCCESS));
        store.beginTransaction();
        List<Object> classes = query("MATCH (c:Spring:Type:Transactional) RETURN c").getColumn("c");
        assertThat(classes.size(), equalTo(6));
        assertThat(classes, hasItem(typeDescriptor(SpringTransactionalClass.class)));
        assertThat(classes, hasItem(typeDescriptor(SpringTransactionalSubClass.class)));
        assertThat(classes, hasItem(typeDescriptor(SpringTransactionalInterface.class)));
        assertThat(classes,
            hasItem(typeDescriptor(SpringTransactionalImplementingClass.class)));
        assertThat(classes, hasItem(typeDescriptor(JtaTransactionalClass.class)));
        assertThat(classes, hasItem(typeDescriptor(JtaJakartaTransactionalClass.class)));
        List<Object> methods = query("MATCH (m:Spring:Method:Transactional) RETURN m").getColumn("m");
        assertThat(methods.size(), equalTo(7));
        assertThat(methods, hasItem(methodDescriptor(SpringTransactionalClass.class, "transactionalMethod")));
        assertThat(methods,
            hasItem(methodDescriptor(SpringTransactionalSubClass.class, "transactionalSubClassMethod")));
        assertThat(methods, hasItem(methodDescriptor(SpringTransactionalInterface.class, "transactionalMethod")));
        assertThat(methods,
            hasItem(methodDescriptor(SpringTransactionalImplementingClass.class, "transactionalMethod")));
        assertThat(methods,
            hasItem(methodDescriptor(SpringTransactionalImplementingClass.class, "transactionalImplementingClassMethod")));
        assertThat(methods, hasItem(methodDescriptor(JtaTransactionalClass.class, "transactionalMethod")));
        assertThat(methods, hasItem(methodDescriptor(JtaJakartaTransactionalClass.class, "transactionalMethod")));
        store.commitTransaction();
    }
}
