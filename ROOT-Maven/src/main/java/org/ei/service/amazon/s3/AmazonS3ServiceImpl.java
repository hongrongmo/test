package org.ei.service.amazon.s3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.amazon.AmazonServiceHelper;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AmazonS3ServiceImpl implements AmazonS3Service {

	private final static Logger log4j = Logger.getLogger(AmazonS3ServiceImpl.class);

	public InputStream getBulletinDataFileFromS3Bucket(String key) throws ServiceException {
		log4j.info("Calling Amazon S3 Service to get the data file for key: " + key);

		try {
			return getObjectInputStreamFromS3Bucket(getS3BulletinBucketName(), key);
		} catch (IOException e) {
			throw new ServiceException(SystemErrorCodes.AWS_S3_CANNOT_GET_BULLETIN_BUCKET, "Unable to get book data file for key: " + key, e);
		}
	}

	public InputStream getCustomerImageFileFromS3Bucket(String key) throws ServiceException {
		log4j.info("Calling Amazon S3 Service to get the customer image file for key: " + key);

		try {
			return getObjectInputStreamFromS3Bucket(getS3CustomerImageBucketName(), key);
		} catch (IOException e) {
			throw new ServiceException(SystemErrorCodes.AWS_S3_CANNOT_GET_CUSTOMERIMAGE_BUCKET, "Unable to get customer image file for key: " + key, e);
		}
	}

	public List<String> getCustomerImagesList() throws ServiceException {

		List<String> customerImages = new ArrayList<String>();

		log4j.info("Calling Amazon S3 Service to get the list of customer image files.");
		String bucketName = "";
		String prefix="";
		try {
			AmazonS3 amazonS3Service = AmazonServiceHelper.getInstance().getAmazonS3Service();
			bucketName = getS3CustomerImageBucketName();
			prefix = getPropertyForEVProjectLayer(ApplicationProperties.CUSTOMER_IMAGES_PREFIX);
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix);
			ObjectListing objectListing  = amazonS3Service.listObjects(listObjectsRequest);
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				String keyVal = objectSummary.getKey();
				if(!keyVal.equalsIgnoreCase(prefix)){
					customerImages.add(keyVal.replace(prefix, ""));
				}
			}

		} catch (Exception e) {
			throw new ServiceException(SystemErrorCodes.AWS_S3_CANNOT_GET_CUSTOMERIMAGES_LIST, "Unable to get customer images list from the bucket: " + bucketName);
		}
		Collections.sort(customerImages);
		return customerImages;
	}

	public InputStream getBookDataFileFromS3Bucket(String key) throws ServiceException {
		log4j.info("Calling Amazon S3 Service to get the data file for key: " + key);

		try {
			return getObjectInputStreamFromS3Bucket(getS3DocViewBucketName(), key);
		} catch (IOException e) {
			throw new ServiceException(SystemErrorCodes.AWS_S3_CANNOT_GET_DOCVIEW_BUCKET, "Unable to get book data file for key: " + key, e);
		}
	}

	public S3Object getReferexDataFileFromS3Bucket() throws ServiceException{
		try {
			return getObjectFromS3Bucket(getS3ReferexBucketName(),getS3ReferexKey());
		} catch (IOException e) {
			throw new ServiceException(SystemErrorCodes.AWS_S3_CANNOT_GET_REFEREX_BUCKET, "Unable to get referex data file", e);
		}
	}

	public S3Object getReferexDataFileFromS3BucketIfModified(Date lastModified) throws ServiceException{
		try {
			return getObjectFromS3BucketIfModified(getS3ReferexBucketName(),getS3ReferexKey(),lastModified);
		} catch (IOException e) {
			throw new ServiceException(SystemErrorCodes.AWS_S3_CANNOT_GET_REFEREX_BUCKET, "Unable to get referex data file for key: " , e);
		}
	}

	private InputStream getObjectInputStreamFromS3Bucket(String bucketName, String key) {
		AmazonS3 amazonS3Service = AmazonServiceHelper.getInstance().getAmazonS3Service();
		S3Object object = amazonS3Service.getObject(new GetObjectRequest(bucketName, key));

		return object.getObjectContent();

	}

	private S3Object getObjectFromS3BucketIfModified(String bucketName, String key, Date lastModified) {
		AmazonS3 amazonS3Service = AmazonServiceHelper.getInstance().getAmazonS3Service();

		S3Object object = amazonS3Service.getObject(new GetObjectRequest(bucketName, key));
		if(object.getObjectMetadata().getLastModified().equals(lastModified)){
		    try { object.close(); } catch (IOException e) {}
			return null;
		}else{
			return object;
		}

	}
	private S3Object getObjectFromS3Bucket(String bucketName, String key) {
		AmazonS3 amazonS3Service = AmazonServiceHelper.getInstance().getAmazonS3Service();
		S3Object object = amazonS3Service.getObject(new GetObjectRequest(bucketName, key));
		return object;
	}
	public String getSignedURLForS3Bucket(String key) throws ServiceException {

		log4j.info("Calling Amazon S3 Service to generate the data file for url: " + key);

		GeneratePresignedUrlRequest generatePresignedUrlRequest;
		try {
			generatePresignedUrlRequest = new GeneratePresignedUrlRequest(getS3DocViewBucketName(), key);
			generatePresignedUrlRequest.setMethod(HttpMethod.GET);
			generatePresignedUrlRequest.setExpiration(getURLExpirationTime());
			ResponseHeaderOverrides r = new ResponseHeaderOverrides();
			generatePresignedUrlRequest.withResponseHeaders(r);
			AmazonS3 amazonS3Service = AmazonServiceHelper.getInstance().getAmazonS3Service();
			URL url = amazonS3Service.generatePresignedUrl(generatePresignedUrlRequest);
			return url.toString();
		} catch (IOException e) {
			throw new ServiceException(SystemErrorCodes.AWS_S3_BAD_SIGNED_URL, "Unable to get bucket for key: " + key, e);
		}

	}

	private java.util.Date getURLExpirationTime() {
		java.util.Date expiration = new java.util.Date();
		long milliSeconds = expiration.getTime();
		milliSeconds += 1000 * 60 * 60; // Add 1 hour.
		expiration.setTime(milliSeconds);
		return expiration;
	}

	private String getS3BulletinBucketName() throws IOException {
		if (null == EVProperties.getInstance() || null == EVProperties.getApplicationProperties()) {
			return getPropertyForEVProjectLayer(EVProperties.S3_BULLETIN_BUCKET_NAME);
		}
		return EVProperties.getProperty(EVProperties.S3_BULLETIN_BUCKET_NAME);
	}

	private String getS3CustomerImageBucketName() throws IOException {
		if (null == EVProperties.getInstance() || null == EVProperties.getApplicationProperties()) {
			return getPropertyForEVProjectLayer(EVProperties.S3_CUSTOMERIMAGE_BUCKET_NAME);
		}
		return EVProperties.getProperty(EVProperties.S3_CUSTOMERIMAGE_BUCKET_NAME);
	}

	private String getS3DocViewBucketName() throws IOException {
		if (null == EVProperties.getInstance() || null == EVProperties.getApplicationProperties()) {
			return getPropertyForEVProjectLayer(EVProperties.S3_DOCVIEW_BUCKET_NAME);
		}
		return EVProperties.getProperty(EVProperties.S3_DOCVIEW_BUCKET_NAME);
	}


	private String getS3ReferexBucketName() throws IOException {
		if (null == EVProperties.getInstance() || null == EVProperties.getApplicationProperties()) {
			return getPropertyForEVProjectLayer(EVProperties.S3_REFEREX_BUCKET_NAME);
		}
		return EVProperties.getProperty(EVProperties.S3_REFEREX_BUCKET_NAME);
	}
	private String getS3ReferexKey() throws IOException {
		if (null == EVProperties.getInstance() || null == EVProperties.getApplicationProperties()) {
			return getPropertyForEVProjectLayer(EVProperties.S3_REFEREX_BUCKET_KEY);
		}
		return EVProperties.getProperty(EVProperties.S3_REFEREX_BUCKET_KEY);
	}

	private String getPropertyForEVProjectLayer(String key) throws IOException {

		return EVProperties.getInstance().getProperty(key);
	}

}
