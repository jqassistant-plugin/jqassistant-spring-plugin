package org.jqassistant.plugin.spring.test.set.transactionpropagation;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringTransactionalMethodLevelPropagation {
    public void transactionalMethodRequired() {}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transactionalMethodRequiresNew() {}
}
