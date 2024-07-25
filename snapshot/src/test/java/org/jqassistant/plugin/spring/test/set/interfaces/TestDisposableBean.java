package org.jqassistant.plugin.spring.test.set.interfaces;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class TestDisposableBean implements DisposableBean {

    @Override
    public void destroy() throws Exception {
    }
}
