package org.jqassistant.plugin.spring.test.set.transactionpropagation;

import jakarta.transaction.Transactional;

@Transactional
public class JakartaTransactionalApiMethodLevelPropagation {
    public void transactionalMethodRequired() {}

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void transactionalMethodRequiresNew() {}
}
