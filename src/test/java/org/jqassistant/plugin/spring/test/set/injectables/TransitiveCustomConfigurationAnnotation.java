package org.jqassistant.plugin.spring.test.set.injectables;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@CustomConfigurationAnnotation
public @interface TransitiveCustomConfigurationAnnotation {
}
