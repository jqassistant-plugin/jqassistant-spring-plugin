package org.jqassistant.plugin.spring.test.concept;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.rule.api.model.Concept;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import org.jqassistant.plugin.spring.test.set.feign.FeignClientConfiguration;
import org.jqassistant.plugin.spring.test.set.feign.TestFeignClient;
import org.junit.jupiter.api.Test;

import static com.buschmais.jqassistant.plugin.java.test.assertj.TypeDescriptorCondition.typeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;

public class FeignIT extends AbstractSpringIT {

    @Test
    void feignClient() throws RuleException {
        scanClasses(FeignClientConfiguration.class, TestFeignClient.class);
        Result<Concept> conceptResult = applyConcept("spring-feign:FeignClient");
        store.beginTransaction();
        assertThat(conceptResult.getStatus()).isEqualTo(Result.Status.SUCCESS);
        assertThat(conceptResult.getRows().size()).isEqualTo(1);
        assertThat((TypeDescriptor) conceptResult.getRows().get(0).getColumns().get("FeignClient").getValue()).is(typeDescriptor(TestFeignClient.class));
        store.commitTransaction();
    }

    @Test
    void feignClientProvidesInjectable() throws RuleException {
        scanClasses(FeignClientConfiguration.class, TestFeignClient.class);
        applyConcept("spring-injection:Injectable");
        store.beginTransaction();
        TestResult result = query("MATCH (feignClient:Spring:Injectable:Feign:Client) RETURN feignClient");
        assertThat(result.getRows().size()).isEqualTo(1);
        assertThat(result.<TypeDescriptor>getColumn("feignClient")).haveExactly(1, typeDescriptor(TestFeignClient.class));
        store.commitTransaction();
    }

    @Test
    void feignClientConfiguration() throws RuleException {
        scanClasses(FeignClientConfiguration.class, TestFeignClient.class);
        Result<Concept> conceptResult = applyConcept("spring-feign:FeignConfiguration");
        store.beginTransaction();
        assertThat(conceptResult.getStatus()).isEqualTo(Result.Status.SUCCESS);
        assertThat(conceptResult.getRows().size()).isEqualTo(1);
        assertThat((TypeDescriptor) conceptResult.getRows().get(0).getColumns().get("FeignConfiguration").getValue()).is(typeDescriptor(FeignClientConfiguration.class));
        store.commitTransaction();
    }

    @Test
    void feignClientConfigurationProvidesInjectable() throws RuleException {
        scanClasses(FeignClientConfiguration.class, TestFeignClient.class);
        applyConcept("spring-injection:Injectable");
        store.beginTransaction();
        TestResult result = query("MATCH (feignConfig:Spring:Injectable) RETURN feignConfig");
        assertThat(result.<TypeDescriptor>getColumn("feignConfig")).haveExactly(1, typeDescriptor(FeignClientConfiguration.class));
        store.commitTransaction();
    }

    @Test
    void feignClientConfigurationProvidesConfiguration() throws RuleException {
        scanClasses(FeignClientConfiguration.class, TestFeignClient.class);
        applyConcept("spring-component:Configuration");
        store.beginTransaction();
        TestResult result = query("MATCH (feignConfig:Spring:Configuration) RETURN feignConfig");
        assertThat(result.getRows().size()).isEqualTo(1);
        assertThat(result.<TypeDescriptor>getColumn("feignConfig")).haveExactly(1, typeDescriptor(FeignClientConfiguration.class));
        store.commitTransaction();
    }

    @Test
    void feignClientConfigurationProvidesComponent() throws RuleException {
        scanClasses(FeignClientConfiguration.class, TestFeignClient.class);
        applyConcept("spring-component:Component");
        store.beginTransaction();
        TestResult result = query("MATCH (feignConfig:Spring:Component) RETURN feignConfig");
        assertThat(result.getRows().size()).isEqualTo(1);
        assertThat(result.<TypeDescriptor>getColumn("feignConfig")).haveExactly(1, typeDescriptor(FeignClientConfiguration.class));
        store.commitTransaction();
    }
}
