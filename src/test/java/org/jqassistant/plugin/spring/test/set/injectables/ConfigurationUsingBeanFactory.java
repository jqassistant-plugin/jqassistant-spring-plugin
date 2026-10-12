package org.jqassistant.plugin.spring.test.set.injectables;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ConfigurationUsingBeanFactory {

    private final ConfigurationWithBeanProducerAndFactoryMethod factory;

    @Bean
    public ConfigurationBeanC beanProducerUsingFactoryMethod() {
        return factory.instantiate();
    }

    public ConfigurationBeanC someOtherMethodUsingFactoryMethod() {
        return factory.instantiate();
    }

}
