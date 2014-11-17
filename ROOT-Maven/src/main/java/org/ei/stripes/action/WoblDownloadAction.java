package org.ei.stripes.action;

import java.io.InputStream;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.ei.exception.ServiceException;
import org.ei.service.amazon.s3.AmazonS3Service;
import org.ei.service.amazon.s3.AmazonS3ServiceImpl;

/**
 * This class handles requests for static "wobl" (Whole Book Download) files - e.g.
 * PDF and images.  Note that the TOC and Tag Cloud components are downloaded differently
 * (see org.ei.books.BookDocument.java)
 *  
 * @author harovetm
 *
 */
@UrlBinding("/wobl/{$event}")
public class WoblDownloadAction extends EVActionBean {

    private String searchType;
    
    @DefaultHandler
    public Resolution handle() throws ServiceException  {
    	
    	// Use S3 service to fetch content
    	AmazonS3Service s3Service= new AmazonS3ServiceImpl(); 
    	String key = getRequest().getRequestURI().substring(1);
    	InputStream bookStream = null;
    	
		bookStream = s3Service.getBookDataFileFromS3Bucket(key);
		
		// Set return type appropriate for content
		if (key.endsWith(".pdf")) {
			return new StreamingResolution("application/pdf", bookStream);
		} else if (key.toLowerCase().endsWith(".jpg") || key.toLowerCase().endsWith(".jpeg")) {
			return new StreamingResolution("image/jpeg", bookStream);
		} else if (key.toLowerCase().endsWith(".gif")) {
			return new StreamingResolution("image/gif", bookStream);
    	} else {
    		return new StreamingResolution("text/html", bookStream);
    	}
    }

    //
    // GETTERS/SETTERS
    //
    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }
    
}
