package org.jqassistant.plugin.spring.test.set.transactionpropagation;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
public class SpringTransactionalTypeAndMethodLevelPropagation {
    public void transactionalMethodMandatoryWithAdditionalConfig() {}

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = RuntimeException.class)
    public void transactionalMethodRequiresNewWithAdditionalConfig() {}

    @Transactional(readOnly = true)
    public void transactionalMethodRequiredWithOverriddenConfig() {}

    @Transactional(propagation = Propagation.NEVER)
    public void transactionalMethodNeverWithoutAdditionalConfig() {}

    // method-level annotations without specified propagation semantics override type-level annotations with the default.
    @Transactional
    public void transactionalMethodRequiredWithoutAdditionalConfig() {}
}
