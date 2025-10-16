package org.jqassistant.plugin.spring.test.set.transactionpropagation;

import jakarta.transaction.Transactional;

@Transactional(value = Transactional.TxType.MANDATORY)
public class JtaJakartaTransactionalTypeMethodPropagation {
    public void transactionalMethodMandatory() {}

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void transactionalMethodRequiresNew() {}
}
