package org.jqassistant.plugin.spring.test.set.transactionpropagation;

import javax.transaction.Transactional;

@Transactional
public class JavaTransactionalApiMethodLevelPropagation {
    public void transactionalMethodRequired() {}

    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = Exception.class)
    public void transactionalMethodRequiresNew() {}
}
