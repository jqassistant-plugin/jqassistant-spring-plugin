package org.jqassistant.plugin.spring.test.set.transactionpropagation;

import jakarta.transaction.Transactional;

@Transactional
public class JtaJakartaTransactionalMethodPropagation {
    public void transactionalMethodRequired() {}

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void transactionalMethodRequiresNew() {}
}
