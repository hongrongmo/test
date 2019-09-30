package org.ei.dataloading.cafe_download_lambda;

import static org.junit.Assert.*;

import java.io.IOException;

import org.ei.dataloading.cafe_correction_lambda.CafeCorrectionLambdaHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class CafeCorrectionLambdaHandlerTest {

	
	 private SNSEvent input;

	    @Before
	    public void createInput() throws IOException {
	        // TODO: set up your sample input object here.
	        input = TestUtils.parse("/sns-event.json", SNSEvent.class);
	    }

	    private Context createContext() {
	        TestContext ctx = new TestContext();

	        // TODO: customize your context here if needed.
	        ctx.setFunctionName("CafeCorrectionLambdaHandler");

	        return ctx;
	    }
	    
	@Test
	public void test() {


		CafeCorrectionLambdaHandler handler = new CafeCorrectionLambdaHandler();
	        Context ctx = createContext();

	        String output = handler.handleRequest(input, ctx);
	        Assert.assertEquals("Hello from SNS!", output);
	        
	}

}
