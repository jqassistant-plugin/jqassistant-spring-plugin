package org.jqassistant.plugin.spring.test.concept;

import com.buschmais.jqassistant.core.report.api.model.Column;
import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Row;
import com.buschmais.jqassistant.core.rule.api.model.Concept;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.plugin.java.api.model.MethodDescriptor;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import org.jqassistant.plugin.spring.test.set.test.AssertExample;
import org.jqassistant.plugin.spring.test.set.test.spring_batch.CustomSpringBatchTestAnnotation;
import org.jqassistant.plugin.spring.test.set.test.spring_batch.SpringBatchTestClassFromInheritedTestClass;
import org.jqassistant.plugin.spring.test.set.test.spring_batch.SpringBatchTestClassWithCustomAnnotationAndTestMethod;
import org.jqassistant.plugin.spring.test.set.test.spring_batch.SpringBatchTestClassWithNestedTestsAndTestMethod;
import org.jqassistant.plugin.spring.test.set.test.spring_batch.SpringBatchTestClassWithNestedTestsAndWithoutTestMethod;
import org.jqassistant.plugin.spring.test.set.test.spring_batch.SpringBatchTestClassWithoutNestedTestsAndWithTestMethod;
import org.jqassistant.plugin.spring.test.set.test.spring_batch.SpringBatchTestClassWithoutNestedTestsAndWithoutTestMethod;
import org.jqassistant.plugin.spring.test.set.test.spring_boot.CustomSpringBootTestAnnotation;
import org.jqassistant.plugin.spring.test.set.test.spring_boot.SpringBootTestClassFromInheritedTestClass;
import org.jqassistant.plugin.spring.test.set.test.spring_boot.SpringBootTestClassWithCustomAnnotationAndTestMethod;
import org.jqassistant.plugin.spring.test.set.test.spring_boot.SpringBootTestClassWithNestedTestsAndTestMethod;
import org.jqassistant.plugin.spring.test.set.test.spring_boot.SpringBootTestClassWithNestedTestsAndWithoutTestMethod;
import org.jqassistant.plugin.spring.test.set.test.spring_boot.SpringBootTestClassWithoutNestedTestsAndWithTestMethod;
import org.jqassistant.plugin.spring.test.set.test.spring_boot.SpringBootTestClassWithoutNestedTestsAndWithoutTestMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.buschmais.jqassistant.core.report.api.model.Result.Status.SUCCESS;
import static com.buschmais.jqassistant.core.report.api.model.Result.Status.WARNING;
import static com.buschmais.jqassistant.plugin.java.test.assertj.MethodDescriptorCondition.methodDescriptor;
import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

public class TestIT extends AbstractSpringIT {

    @Test
    void resultActionsAndReturnMethod() throws Exception {
        scanClasses(AssertExample.class);

        final Result<Concept> conceptResult = applyConcept("spring-test:ResultActionsAssertMethod");
        assertThat(conceptResult.getStatus()).isEqualTo(SUCCESS);

        store.beginTransaction();

        assertThat(conceptResult.getRows().size()).isEqualTo(1);
        assertThat(conceptResult.getRows()
                .get(0)
                .getColumns()
                .get("assertMethod")
                .getValue()).asInstanceOf(type(MethodDescriptor.class))
                .is(methodDescriptor(ResultActions.class, "andReturn"));

        verifyResultGraph("Servlet", "resultActionsAndReturnExampleMethod", ResultActions.class, "andReturn");

        store.commitTransaction();
    }

    @Test
    void providedConceptAssertMethod() throws Exception {
        scanClasses(AssertExample.class);

        final Result<Concept> conceptResult = applyConcept("java:AssertMethod");
        assertThat(conceptResult.getStatus()).isEqualTo(SUCCESS);

        store.beginTransaction();

        final List<TypeDescriptor> declaringTypes = conceptResult.getRows().stream()
            .map(Row::getColumns)
            .map(columns -> columns.get("DeclaringType"))
            .map(Column::getValue)
            .map(TypeDescriptor.class::cast)
            .collect(Collectors.toList());
        assertThat(declaringTypes).haveExactly(1, typeDescriptor(ResultActions.class));

        verifyResultGraph("Servlet", "resultActionsAndReturnExampleMethod", ResultActions.class, "andReturn");

        store.commitTransaction();
    }

    private static Stream<Arguments> springTestWithNestedTestsAndWithoutTestMethodParams() {
        return Stream.of(
            Arguments.of(SpringBootTestClassWithNestedTestsAndWithoutTestMethod.class, SpringBootTestClassWithNestedTestsAndWithoutTestMethod.NestedTestWithTestcase.class, SpringBootTestClassWithNestedTestsAndWithoutTestMethod.NestedTestWithoutTestcase.class),
            Arguments.of(SpringBatchTestClassWithNestedTestsAndWithoutTestMethod.class, SpringBatchTestClassWithNestedTestsAndWithoutTestMethod.NestedTestWithTestcase.class, SpringBatchTestClassWithNestedTestsAndWithoutTestMethod.NestedTestWithoutTestcase.class)
        );
    }

    @ParameterizedTest
    @MethodSource("springTestWithNestedTestsAndWithoutTestMethodParams")
    void springBootTestWithNestedTestsAndWithoutTestMethod(Class<?> outerClass, Class<?> firstInnerClass, Class<?> secondInnerClass) throws RuleException {
        scanClasses(outerClass, firstInnerClass, secondInnerClass);
        Result<Concept> result = applyConcept("spring-test:TestClass");
        store.beginTransaction();
        assertThat(result.getStatus()).isEqualTo(SUCCESS);
        assertThat(result.getRows().size()).isEqualTo(1);
        assertThat((TypeDescriptor) result.getRows().get(0).getColumns().get("SpringTest").getValue()).is(typeDescriptor(outerClass));
        store.commitTransaction();
    }

    private static Stream<Arguments> springTestWithNestedTestsAndTestMethodParams() {
        return Stream.of(
            Arguments.of(SpringBootTestClassWithNestedTestsAndTestMethod.class, SpringBootTestClassWithNestedTestsAndTestMethod.NestedTest.class),
            Arguments.of(SpringBatchTestClassWithNestedTestsAndTestMethod.class, SpringBatchTestClassWithNestedTestsAndTestMethod.NestedTest.class)
        );
    }

    @ParameterizedTest
    @MethodSource("springTestWithNestedTestsAndTestMethodParams")
    void springBootTestWithNestedTestsAndTestMethod(Class<?> outerClass, Class<?> innerClass) throws RuleException {
        scanClasses(outerClass, innerClass);
        Result<Concept> result = applyConcept("spring-test:TestClass");
        store.beginTransaction();
        assertThat(result.getStatus()).isEqualTo(SUCCESS);
        assertThat(result.getRows().size()).isEqualTo(1);
        assertThat((TypeDescriptor) result.getRows().get(0).getColumns().get("SpringTest").getValue()).is(typeDescriptor(outerClass));
        store.commitTransaction();
    }

    @ParameterizedTest
    @ValueSource(classes = {SpringBootTestClassWithoutNestedTestsAndWithoutTestMethod.class, SpringBatchTestClassWithoutNestedTestsAndWithoutTestMethod.class})
    void springBootTestWithoutNestedTestsAndWithoutTestMethod(Class<?> annotatedClass) throws RuleException {
        scanClasses(annotatedClass);
        Result<Concept> result = applyConcept("spring-test:TestClass");
        store.beginTransaction();
        assertThat(result.getStatus()).isEqualTo(WARNING);
        store.commitTransaction();
    }

    @ParameterizedTest
    @ValueSource(classes = {SpringBootTestClassWithoutNestedTestsAndWithTestMethod.class, SpringBatchTestClassWithoutNestedTestsAndWithTestMethod.class})
    void springBootTestWithoutNestedTestsAndWithTestMethod(Class<?> testClass) throws RuleException {
        scanClasses(testClass);
        Result<Concept> result = applyConcept("spring-test:TestClass");
        store.beginTransaction();
        assertThat(result.getStatus()).isEqualTo(SUCCESS);
        assertThat(result.getRows().size()).isEqualTo(1);
        assertThat((TypeDescriptor) result.getRows().get(0).getColumns().get("SpringTest").getValue()).is(typeDescriptor(testClass));
        store.commitTransaction();
    }

    private static Stream<Arguments> inheritedSpringTestParams() {
        return Stream.of(
            Arguments.of(SpringBootTestClassFromInheritedTestClass.class, SpringBootTestClassWithoutNestedTestsAndWithoutTestMethod.class),
            Arguments.of(SpringBatchTestClassFromInheritedTestClass.class, SpringBatchTestClassWithoutNestedTestsAndWithoutTestMethod.class)
        );
    }

    @ParameterizedTest
    @MethodSource("inheritedSpringTestParams")
    void inheritedSpringBootTest(Class<?> subclass, Class<?> parentClass) throws RuleException {
        scanClasses(subclass, parentClass);
        Result<Concept> result = applyConcept("spring-test:TestClass");
        store.beginTransaction();
        assertThat(result.getStatus()).isEqualTo(SUCCESS);
        assertThat(result.getRows().size()).isEqualTo(1);
        assertThat((TypeDescriptor) result.getRows().get(0).getColumns().get("SpringTest").getValue()).is(typeDescriptor(subclass));
        store.commitTransaction();
    }

    private static Stream<Arguments> springTestWithCustomAnnoatationParams() {
        return Stream.of(
            Arguments.of(CustomSpringBootTestAnnotation.class, SpringBootTestClassWithCustomAnnotationAndTestMethod.class),
            Arguments.of(CustomSpringBatchTestAnnotation.class, SpringBatchTestClassWithCustomAnnotationAndTestMethod.class)
        );
    }

    @ParameterizedTest
    @MethodSource("springTestWithCustomAnnoatationParams")
    void springBootTestWithCustomAnnotation(Class<?> annotation, Class<?> annotatedClass) throws RuleException {
        scanClasses(annotation, annotatedClass);
        Result<Concept> result = applyConcept("spring-test:TestClass");
        store.beginTransaction();
        assertThat(result.getStatus()).isEqualTo(SUCCESS);
        assertThat(result.getRows().size()).isEqualTo(1);
        assertThat((TypeDescriptor) result.getRows().get(0).getColumns().get("SpringTest").getValue()).is(typeDescriptor(annotatedClass));
        store.commitTransaction();
    }

}
