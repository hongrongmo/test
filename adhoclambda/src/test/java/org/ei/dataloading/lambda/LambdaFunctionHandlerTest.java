package org.ei.dataloading.lambda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ei.dataloading.adhoclambda.DlAdhocLambdaHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3BucketEntity;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.event.S3EventNotification.S3ObjectEntity;
import com.amazonaws.services.s3.model.GetObjectRequest;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
@RunWith(MockitoJUnitRunner.class)
public class LambdaFunctionHandlerTest {

    private S3Event event;

    @Captor
    private ArgumentCaptor<GetObjectRequest> getObjectRequest;

    @Before
    public void setUp() throws IOException {

    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    public void testLambdaFunctionHandler() {
        DlAdhocLambdaHandler handler = new DlAdhocLambdaHandler();
        S3ObjectEntity objEntity = new S3ObjectEntity("123.zip", 1L, null, null, null);
        S3BucketEntity bucketEntity = new S3BucketEntity("ev-data", null, "arn:aws:s3:::ev-data");
        S3Entity entity = new S3Entity(null, bucketEntity, objEntity, null); 
        S3EventNotificationRecord rec = new S3EventNotificationRecord(null, "test", null,
        	      "1970-01-01T00:00:00.000Z", null, null, null, entity, null, null);
        List<S3EventNotificationRecord> records = new ArrayList<S3EventNotificationRecord>();
        records.add(rec);
        event = new S3Event(records);
        Context ctx = createContext();
        String output = handler.handleRequest(event, ctx);

        Assert.assertNotNull(output);

    }
}
