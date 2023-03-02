package org.jqassistant.plugin.spring.test.set.injectables.subclass;

import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;

public class SubClassOfInjectable extends CustomizableTraceInterceptor {

    SubClassOfInjectable(boolean useDynamicLogger) {
        setEnterMessage("> [ENTRY] " + PLACEHOLDER_METHOD_NAME + "(" + PLACEHOLDER_ARGUMENTS + ")");
        setExitMessage("< [EXIT] " + PLACEHOLDER_METHOD_NAME + " @ " + PLACEHOLDER_INVOCATION_TIME + " ms: " + PLACEHOLDER_RETURN_VALUE);
        setExceptionMessage("< [THROW] " + PLACEHOLDER_METHOD_NAME + " : " + PLACEHOLDER_EXCEPTION);
        setUseDynamicLogger(useDynamicLogger);
    }

    SubClassOfInjectable() {
        // This constructor is allowed to call the other constructor of the injectable
        this(true);
    }

    @Override
    protected boolean isLogEnabled(Log logger) {
        return logger.isDebugEnabled();
    }

    @Override
    protected void writeToLog(Log logger, String message, Throwable ex) {
        if (ex != null) {
            logger.warn(message, ex);
        } else {
            logger.debug(message);
        }
    }

}
