package org.ei.service.amazon.s3;

import java.io.InputStream;
import java.util.List;

import org.ei.exception.ServiceException;




public interface AmazonS3Service {

	InputStream getBulletinDataFileFromS3Bucket(String key) throws ServiceException ;
	
	InputStream getCustomerImageFileFromS3Bucket(String key) throws ServiceException ;
	
	InputStream getBookDataFileFromS3Bucket(String key) throws ServiceException ;
	
	String getSignedURLForS3Bucket(String key) throws ServiceException ;
	
	List<String> getCustomerImagesList() throws ServiceException ;

}

