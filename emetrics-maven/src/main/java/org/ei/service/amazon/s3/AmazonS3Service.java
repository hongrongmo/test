package org.ei.service.amazon.s3;

import java.io.InputStream;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

public class AmazonS3Service {

    private final static Logger log4j = Logger.getLogger(AmazonS3Service.class);
    private final static String S3_REPORTS_BUCKET = "emetrics";

    private static AmazonS3Service instance;
    private static int S3_CONN_TIMEOUT = 5 * 1000;     // 5 second timeout
    private static int S3_SOCK_TIMEOUT = 5 * 1000;     // 5 second timeout
    private AmazonS3 s3service;
    private static ClientConfiguration clientconfig;

    private AmazonS3Service() {
    };

    public static AmazonS3Service getInstance() {
        if (instance == null) {
            clientconfig = new ClientConfiguration();
            clientconfig.setConnectionTimeout(S3_CONN_TIMEOUT);
            clientconfig.setSocketTimeout(S3_SOCK_TIMEOUT);

            instance = new AmazonS3Service();

            log4j.info("Creating Amazon S3 client...");
            instance.s3service = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider("/AwsCredentials.properties"), clientconfig);
            instance.s3service.setRegion(Region.getRegion(Regions.US_EAST_1));
        }
        return instance;
    }

    public AmazonS3 getAmazonS3Service() {
        return this.s3service;
    }

    public InputStream getReportFromS3Bucket(String key) {
        log4j.info("Calling Amazon S3 Service to get the data file for key: " + key);
        return getObjectInputStreamFromS3Bucket(S3_REPORTS_BUCKET, key);
    }

    private InputStream getObjectInputStreamFromS3Bucket(String bucketName, String key) {
        S3Object object = instance.getAmazonS3Service().getObject(new GetObjectRequest(bucketName, key));
        return object.getObjectContent();
    }

    public ObjectMetadata getMetadata(String key) {
        log4j.info("Retrieving metadata for key: " + key);
        return instance.getAmazonS3Service().getObjectMetadata(S3_REPORTS_BUCKET, key);
    }
    
    public boolean isReportAvailable(String key) {
        log4j.info("Checking Amazon S3 Service if " + key + " is available...");
        try {
            ObjectMetadata metadata = getMetadata(key);
            if (metadata != null && metadata.getContentLength() > 0) return true;
        } catch (Exception e) {
            log4j.error("Unable to get metedata for object with key: " + key);
        }
        return false;
    }

}
