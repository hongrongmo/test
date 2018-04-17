package lambdaFunctions;

import java.net.URLDecoder;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;

/*
 * @author: HT
 * @Date: 12/15/2017
 * @Description: Invokes ELT (NEW that now run as corr), (Update) and (Delete)script
 * whenever related ELT files are sync'ed to s3 bucket
 * 
 * reason for this is to automate our weekly routine dataloading
 * this is the First dataset to satrt with
 */
public class EltCorrectionLambdaFunction implements RequestHandler <S3Event, String>
{

	public String handleRequest(S3Event s3event, Context context) 
	{
		try
		{
			S3EventNotificationRecord record = s3event.getRecords().get(0);
			String bucket = record.getS3().getBucket().getName();

			//Object Key may have unicode non-ASCII characters, so need to decode 
			String key = record.getS3().getObject().getKey().replace("+", " ");
			key = URLDecoder.decode(key, "UTF-8");
			
			System.out.println("BucketName: " + bucket);
			System.out.println("Key: " + key);
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

}
