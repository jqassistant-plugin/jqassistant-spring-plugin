package org.jqassistant.plugin.spring.test.set.injectables;

import java.lang.annotation.Annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JakartaConstraintValidator implements ConstraintValidator<Annotation, Void> {
    @Override
    public boolean isValid(Void unused, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
