package org.jqassistant.plugin.spring.test.constraint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.plugin.java.api.model.FieldDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.WritesDescriptor;
import com.buschmais.jqassistant.plugin.java.test.AbstractJavaPluginIT;

import org.jqassistant.plugin.spring.test.set.constructors.Repository;
import org.jqassistant.plugin.spring.test.set.constructors.RepositoryImpl;
import org.jqassistant.plugin.spring.test.set.constructors.SecurityProperties;
import org.jqassistant.plugin.spring.test.set.constructors.ServiceImpl;
import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.FAILURE;
import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.plugin.java.test.matcher.FieldDescriptorMatcher.fieldDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.matcher.TypeDescriptorMatcher.typeDescriptor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

class FieldsOfInjectablesMustNotBeManipulatedIT extends AbstractJavaPluginIT {

    @Test
    void constructorFieldsMustNotBeManipulated() throws Exception {
        // given
        scanClasses(ServiceImpl.class, Repository.class, RepositoryImpl.class);
        // when
        Result<Constraint> result = validateConstraint("spring-injection:FieldsOfInjectablesMustNotBeManipulated");
        // then
        assertThat(result.getStatus(), equalTo(FAILURE));
        store.beginTransaction();
        List<Row> rows = result.getRows();
        assertThat(rows.size(), equalTo(1));
        Row row = rows.get(0);
        WritesDescriptor writeToInjectableField = (WritesDescriptor) row.getColumns()
            .get("WriteToInjectableField")
            .getValue();
        TypeDescriptor injectable = (TypeDescriptor) row.getColumns()
            .get("Injectable")
            .getValue();
        FieldDescriptor field = (FieldDescriptor) row.getColumns()
            .get("Field")
            .getValue();
        assertThat(writeToInjectableField.getLineNumber(), equalTo(55));
        assertThat(injectable, typeDescriptor(ServiceImpl.class));
        assertThat(field, fieldDescriptor(ServiceImpl.class, "repository"));

        store.commitTransaction();
    }

    @Test
    void configurationProperties() throws Exception {
        // given
        scanClasses(SecurityProperties.class);
        // when
        Result<Constraint> result = validateConstraint("spring-injection:FieldsOfInjectablesMustNotBeManipulated");
        // then
        assertThat(result.getStatus(), equalTo(SUCCESS));
    }

    @Test
    void syntheticFields() throws Exception {
        // given
        scanClasses(ServiceImpl.class, RepositoryImpl.class);
        store.beginTransaction();
        Map<String, Object> params = new HashMap<>();
        params.put("service", ServiceImpl.class.getName());
        List<FieldDescriptor> fields = query("MATCH (:Type{fqn:$service})-[:DECLARES]->(field:Field) SET field.synthetic=true RETURN field", params)
                .getColumn("field");
        assertThat(fields, hasItems(fieldDescriptor(ServiceImpl.class, "repository")));
        store.commitTransaction();
        // when
        Result.Status status = validateConstraint("spring-injection:FieldsOfInjectablesMustNotBeManipulated").getStatus();
        // then
        assertThat(status, equalTo(SUCCESS));
    }
}
