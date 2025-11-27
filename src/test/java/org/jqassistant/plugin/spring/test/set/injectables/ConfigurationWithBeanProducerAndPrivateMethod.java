package org.jqassistant.plugin.spring.test.set.injectables;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationWithBeanProducerAndPrivateMethod {

    @Bean
    public ConfigurationBeanC getConfiguration() {
        return instantiate();
    }

    public ConfigurationBeanC nonBeanProducerInstantiatingBean() {
        return instantiate();
    }

    private ConfigurationBeanC instantiate() {
        return new ConfigurationBeanC();
    }

}
