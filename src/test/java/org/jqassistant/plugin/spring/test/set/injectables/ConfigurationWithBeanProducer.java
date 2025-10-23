package org.jqassistant.plugin.spring.test.set.injectables;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationWithBeanProducer {

    @Bean
    public ConfigurationBeanA getConfiguration() {
        return new ConfigurationBeanA();
    }

    @Configuration
    public static class NestedConfigurationWithBeanProducer {
        @Bean
        public ConfigurationBeanB getConfiguration() {
            return new ConfigurationBeanB();
        }
    }

}
