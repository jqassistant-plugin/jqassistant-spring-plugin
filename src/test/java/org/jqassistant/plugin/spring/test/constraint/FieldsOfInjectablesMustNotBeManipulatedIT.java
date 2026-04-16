package org.jqassistant.plugin.spring.test.constraint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.plugin.java.api.model.FieldDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
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
import static com.buschmais.jqassistant.plugin.java.test.assertj.FieldDescriptorCondition.fieldDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;

class FieldsOfInjectablesMustNotBeManipulatedIT extends AbstractJavaPluginIT {

    @Test
    void constructorFieldsMustNotBeManipulated() throws Exception {
        // given
        scanClasses(ServiceImpl.class, Repository.class, RepositoryImpl.class);
        // when
        Result<Constraint> result = validateConstraint("spring-injection:FieldsOfInjectablesMustNotBeManipulated");
        // then
        assertThat(result.getStatus()).isEqualTo(FAILURE);
        store.beginTransaction();
        List<Row> rows = result.getRows();
        assertThat(rows).hasSize(3);

        Row row1 = rows.get(0);
        WritesDescriptor writeToInjectableFieldViaPostConstruct = (WritesDescriptor) row1.getColumns()
            .get("WriteToInjectableField")
            .getValue();
        TypeDescriptor injectableWrittenViaPostConstruct = (TypeDescriptor) row1.getColumns()
            .get("Injectable")
            .getValue();
        MethodDescriptor postConstructMethod = (MethodDescriptor) row1.getColumns()
            .get("Method")
            .getValue();
        FieldDescriptor fieldWrittenViaPostConstruct = (FieldDescriptor) row1.getColumns()
            .get("Field")
            .getValue();
        assertThat(writeToInjectableFieldViaPostConstruct.getLineNumber()).isEqualTo(29);
        assertThat(injectableWrittenViaPostConstruct).is(typeDescriptor(ServiceImpl.class));
        assertThat(postConstructMethod).is(methodDescriptor(ServiceImpl.class, "postConstruct"));
        assertThat(fieldWrittenViaPostConstruct).is(fieldDescriptor(ServiceImpl.class, "cache"));

        Row row2 = rows.get(1);
        WritesDescriptor writeToInjectableFieldViaPreDestroy = (WritesDescriptor) row2.getColumns()
            .get("WriteToInjectableField")
            .getValue();
        TypeDescriptor injectableWrittenViaPreDestroy = (TypeDescriptor) row2.getColumns()
            .get("Injectable")
            .getValue();
        MethodDescriptor preDestroyMethod = (MethodDescriptor) row2.getColumns()
            .get("Method")
            .getValue();
        FieldDescriptor fieldWrittenViaPreDestroy = (FieldDescriptor) row2.getColumns()
            .get("Field")
            .getValue();
        assertThat(writeToInjectableFieldViaPreDestroy.getLineNumber()).isEqualTo(34);
        assertThat(injectableWrittenViaPreDestroy).is(typeDescriptor(ServiceImpl.class));
        assertThat(preDestroyMethod).is(methodDescriptor(ServiceImpl.class, "preDestroy"));
        assertThat(fieldWrittenViaPreDestroy).is(fieldDescriptor(ServiceImpl.class, "cache"));

        Row row3 = rows.get(2);
        WritesDescriptor writeToInjectableField = (WritesDescriptor) row3.getColumns()
            .get("WriteToInjectableField")
            .getValue();
        TypeDescriptor injectable = (TypeDescriptor) row3.getColumns()
            .get("Injectable")
            .getValue();
        MethodDescriptor setterMethod = (MethodDescriptor) row3.getColumns()
            .get("Method")
            .getValue();
        FieldDescriptor field = (FieldDescriptor) row3.getColumns()
            .get("Field")
            .getValue();
        assertThat(writeToInjectableField.getLineNumber()).isEqualTo(55);
        assertThat(injectable).is(typeDescriptor(ServiceImpl.class));
        assertThat(setterMethod).is(methodDescriptor(ServiceImpl.class, "setRepository", Repository.class));
        assertThat(field).is(fieldDescriptor(ServiceImpl.class, "repository"));

        store.commitTransaction();
    }

    @Test
    void configurationProperties() throws Exception {
        // given
        scanClasses(SecurityProperties.class);
        // when
        Result<Constraint> result = validateConstraint("spring-injection:FieldsOfInjectablesMustNotBeManipulated");
        // then
        assertThat(result.getStatus()).isEqualTo(SUCCESS);
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
        assertThat(fields).haveExactly(1, fieldDescriptor(ServiceImpl.class, "repository"));
        store.commitTransaction();
        // when
        Result.Status status = validateConstraint("spring-injection:FieldsOfInjectablesMustNotBeManipulated").getStatus();
        // then
        assertThat(status).isEqualTo(SUCCESS);
    }
}
