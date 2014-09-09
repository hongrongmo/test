/**
 *
 */
package org.ei.biz.books;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.ei.books.BookDocument;
import org.ei.books.IBookPartFetcher;
import org.ei.exception.ServiceException;
import org.ei.service.amazon.s3.AmazonS3Service;
import org.ei.service.amazon.s3.AmazonS3ServiceImpl;

/**
 * @author harovetm
 *
 */
public class AmazonS3BookPartFetcher implements IBookPartFetcher {
    private static final Logger log4j = Logger.getLogger(BookDocument.class);

    /**
     * Retrieve tag cloud HTML from S3 bucket
     *
     * @param out
     * @throws IOException
     */
    public String getBookPart(String isbn13, String part) throws IOException {

        AmazonS3Service s3Service = new AmazonS3ServiceImpl();
        InputStream awsHtmlStream = null;
        InputStreamReader awsHtmlReader = null;
        BufferedReader awsBufferedReader = null;
        StringBuffer out = new StringBuffer("");
        try {
            awsHtmlStream = s3Service.getBookDataFileFromS3Bucket("wobl/" + isbn13 + "/" + isbn13 + part);
            awsHtmlReader = new InputStreamReader(awsHtmlStream, "UTF-8");
            awsBufferedReader = new BufferedReader(awsHtmlReader);
            int value = 0;
            while ((value = awsBufferedReader.read()) != -1) {
                out.append((char)value);
            }
        } catch (ServiceException e) {
            log4j.error("Error while reading stream from: 'wobl/" + isbn13 + "/" + isbn13 + part, e);
        } finally {
            // releases resources associated with the streams
            if (awsHtmlStream != null)
                awsHtmlStream.close();
            if (awsHtmlReader != null)
                awsHtmlReader.close();
            if (awsBufferedReader != null)
                awsBufferedReader.close();

        }

        return out.toString();
    }


}
