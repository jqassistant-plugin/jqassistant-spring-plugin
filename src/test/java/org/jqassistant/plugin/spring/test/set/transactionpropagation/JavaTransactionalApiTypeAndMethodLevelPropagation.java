package org.jqassistant.plugin.spring.test.set.transactionpropagation;

import javax.transaction.Transactional;

@Transactional(value = Transactional.TxType.MANDATORY)
public class JavaTransactionalApiTypeAndMethodLevelPropagation {
    public void transactionalMethodMandatory() {}

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void transactionalMethodRequiresNew() {}

    // method-level annotations without specified propagation semantics override type-level annotations with the default.
    @Transactional
    public void transactionalMethodRequired() {}
}
