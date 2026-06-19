package org.jqassistant.plugin.spring.test.set.transactionpropagation;

import javax.transaction.Transactional;

@Transactional(value = Transactional.TxType.MANDATORY, rollbackOn = Exception.class)
public class JavaTransactionalApiTypeAndMethodLevelPropagation {
    public void transactionalMethodMandatoryWithAdditionalConfig() {}

    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = RuntimeException.class)
    public void transactionalMethodRequiresNewWithAdditionalConfig() {}

    @Transactional(rollbackOn = RuntimeException.class)
    public void transactionalMethodRequiredWithOverriddenConfig() {}

    @Transactional(value = Transactional.TxType.NEVER)
    public void transactionalMethodNeverWithoutAdditionalConfig() {}

    // method-level annotations without specified propagation semantics override type-level annotations with the default.
    @Transactional
    public void transactionalMethodRequiredWithoutAdditionalConfig() {}
}
