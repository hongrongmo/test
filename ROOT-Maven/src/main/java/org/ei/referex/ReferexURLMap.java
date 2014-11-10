package org.ei.referex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.service.amazon.s3.AmazonS3ServiceImpl;

import com.amazonaws.services.s3.model.S3Object;

/**
 * Class to map our old referex urls to the new home on Scidir. It reads a file called referex.csv from amazon s3 and stores it in the urlMapping hastTable. The
 * aws object will be checked to see if it was modified each time it is accessed.
 *
 *
 */
public class ReferexURLMap {
    private final static Logger log4j = Logger.getLogger(ReferexURLMap.class);
    private Hashtable<String, String> urlMappings;
    private Date lastModified;
    private static ReferexURLMap instance;

    private ReferexURLMap() throws ServiceException, IOException {
        AmazonS3ServiceImpl amazonService = new AmazonS3ServiceImpl();
        S3Object referexObj = null;
        try {
            referexObj = amazonService.getReferexDataFileFromS3Bucket();
            if (referexObj != null) {
                this.lastModified = referexObj.getObjectMetadata().getLastModified();
            }
            populateUrlMapping(referexObj);
        } catch (Exception e) {
            log4j.error("Unable to build Referex URL map", e);
        } finally {
            if (referexObj != null) {
                try {
                    referexObj.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static ReferexURLMap getInstance() throws ServiceException, IOException {
        if (instance == null) {

            instance = new ReferexURLMap();
        } else {
            instance.getLatestMap();
        }

        return instance;
    }

    /**
     * Get the latest file object
     *
     * @throws ServiceException
     * @throws IOException
     */
    private void getLatestMap() throws ServiceException, IOException {
        AmazonS3ServiceImpl amazonService = new AmazonS3ServiceImpl();
        S3Object referexObj = null;
        try {
            referexObj = amazonService.getReferexDataFileFromS3BucketIfModified(this.lastModified);
            if (referexObj != null) {
                this.lastModified = referexObj.getObjectMetadata().getLastModified();
                populateUrlMapping(referexObj);

            }
        } catch (Exception e) {
            log4j.error("Unable to build Referex URL map", e);
        } finally {
            if (referexObj != null) {
                try {
                    referexObj.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Get the redirecting url for this specific key.
     *
     * @param key
     * @return
     */
    public String getRedirectUrl(String key) {
        String redirectUrl = "";
        if (!urlMappings.isEmpty()) {
            redirectUrl = (String) urlMappings.get(key);
        }
        return redirectUrl;
    }

    /**
     * Read the filef rom s3 storage and parse it. It should be in a comma seperated file with the format old_referex_isbn, new SD url.
     *
     * @param referexObj
     * @throws IOException
     */
    private void populateUrlMapping(S3Object referexObj) throws IOException {

        if (referexObj != null) {
            urlMappings = new Hashtable<String, String>();
        } else {
            return;
        }

        InputStream refStream = referexObj.getObjectContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(refStream));

        while (true) {
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                log4j.error("Error reading Referex.csv");
                e.printStackTrace();
            }
            if (line == null) {
                break;
            } else {
                String[] urls = line.split(",");
                if (urls != null) {
                    urlMappings.put(urls[0], urls[1]);
                }
            }

        }

        refStream.close();
        reader.close();

    }

}
