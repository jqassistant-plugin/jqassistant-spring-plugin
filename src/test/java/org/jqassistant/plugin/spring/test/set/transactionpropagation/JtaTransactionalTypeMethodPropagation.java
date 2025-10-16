package org.jqassistant.plugin.spring.test.set.transactionpropagation;

import javax.transaction.Transactional;

@Transactional(value = Transactional.TxType.MANDATORY)
public class JtaTransactionalTypeMethodPropagation {
    public void transactionalMethodMandatory() {}

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void transactionalMethodRequiresNew() {}
}
