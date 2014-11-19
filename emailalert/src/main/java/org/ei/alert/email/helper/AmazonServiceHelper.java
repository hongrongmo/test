package org.ei.alert.email.helper;

import org.apache.log4j.Logger;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

/**
 * Helper class to get Amazon Service references
 */
public final class AmazonServiceHelper {

	private final static Logger log4j = Logger.getLogger(AmazonServiceHelper.class);

	private static AmazonServiceHelper instance;
	
    private AmazonSimpleEmailServiceClient sesclient;


	private AmazonServiceHelper() {};

	public static AmazonServiceHelper getInstance() {
	    if (instance == null) {
	        instance = new AmazonServiceHelper();
            Region usEast1 = Region.getRegion(Regions.US_EAST_1);
            log4j.info("Creating Amazon SES client...");
	        instance.sesclient = new AmazonSimpleEmailServiceClient(new ClasspathPropertiesFileCredentialsProvider("AwsCredentials.properties"));
            instance.sesclient.setRegion(usEast1);
	    }
	    return instance;
	}
    public AmazonSimpleEmailServiceClient getAmazonSESClient() {
        return this.sesclient;
    }

}
