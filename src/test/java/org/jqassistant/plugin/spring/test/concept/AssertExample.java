package org.jqassistant.plugin.spring.test.concept;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.lang.NonNull;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;

/**
 * Example class used by integration tests.
 */
public class AssertExample {

    void resultActionsAndReturnExampleMethod() {
         final ResultActions resultActions = new ExampleResultActions();
         resultActions.andReturn();
    }

    void mockWebServiceServerVerifyExampleMethod() {
        MockWebServiceServer.createServer(new WebServiceTemplate()).verify();
    }

    static class ExampleResultActions implements ResultActions {
        @Override
        @NonNull
        public ResultActions andExpect(@NonNull ResultMatcher matcher) throws Exception {
            throw new NotImplementedException("Not implemented");
        }

        @Override
        @NonNull
        public ResultActions andDo(@NonNull ResultHandler handler) throws Exception {
            throw new NotImplementedException("Not implemented");
        }

        @Override
        @NonNull
        public MvcResult andReturn() {
            throw new NotImplementedException("Not implemented");
        }
    }
}
