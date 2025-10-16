package org.jqassistant.plugin.spring.test.set.transactionpropagation;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public class SpringTransactionalTypeMethodPropagation {
    public void transactionalMethodMandatory() {}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transactionalMethodRequiresNew() {}
}
