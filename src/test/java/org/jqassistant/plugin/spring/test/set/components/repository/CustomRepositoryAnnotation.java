package org.jqassistant.plugin.spring.test.set.components.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@org.springframework.stereotype.Repository
public @interface CustomRepositoryAnnotation {
}
