package org.jqassistant.plugin.spring.test.set.injectables;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class JavaxConstraintValidator implements ConstraintValidator<Annotation, Void> {
    @Override
    public boolean isValid(Void unused, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
