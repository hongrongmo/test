package org.ei.dataloading.lambda;

import java.io.IOException;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class CafeLambdaFunctionHandlerTest {

    private SNSEvent input;

    @Before
    public void createInput() throws IOException {
        // TODO: set up your sample input object here.
        input = TestUtils.parse("/sns-event.json", SNSEvent.class);
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("CafeLambdaFunctionHandler");

        return ctx;
    }

    @Test
    public void testCafeLambdaFunctionHandler() {
        CafeLambdaFunctionHandler handler = new CafeLambdaFunctionHandler();
        Context ctx = createContext();

        String output = handler.handleRequest(input, ctx);
        Assert.assertEquals("Hello from SNS!", output);
    }
}
