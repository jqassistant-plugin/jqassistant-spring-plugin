package org.jqassistant.plugin.spring.test.set.transaction;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SpringTransactionalInterface {

    void transactionalMethod();

}
