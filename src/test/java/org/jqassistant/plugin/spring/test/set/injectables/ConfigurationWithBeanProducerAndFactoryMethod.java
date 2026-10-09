package org.jqassistant.plugin.spring.test.set.injectables;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationWithBeanProducerAndFactoryMethod {

    @Bean
    public ConfigurationBeanC getConfiguration() {
        return instantiate();
    }

    public ConfigurationBeanC nonBeanProducerInstantiatingBean() {
        return instantiate();
    }

    public ConfigurationBeanC instantiate() {
        return new ConfigurationBeanC();
    }

}
