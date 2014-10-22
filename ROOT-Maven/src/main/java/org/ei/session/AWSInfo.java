package org.ei.session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;

public class AWSInfo {
    private static Logger log4j = Logger.getLogger(AWSInfo.class);

    public static final String AMI_ID = "ami-id";
    public static final String INSTANCE_ID = "instance-id";
    public static final String INSTANCE_TYPE = "instance-type";
    public static final String HOST_NAME = "hostname";
    public static final String IAM_INFO = "iam/info";

    public static final List<String> DEFAULT_KEYS = new ArrayList<String>();
    static {
        DEFAULT_KEYS.add(AMI_ID);
        DEFAULT_KEYS.add(INSTANCE_ID);
        DEFAULT_KEYS.add(HOST_NAME);
    }

    private Map<String, String> awsvals;

    public AWSInfo() {
        this(DEFAULT_KEYS);
    }

    public AWSInfo(List<String> keys) {
        try {
            awsvals = new HashMap<String, String>();
            String runlevel = EVProperties.getApplicationProperties().getRunlevel();
            if (GenericValidator.isBlankOrNull(runlevel) || "local".equals(runlevel)) {
                return;
            }
            if (keys == null || keys.isEmpty()) {
                throw new IllegalArgumentException("No keys present - please pass a list of keys to retrieve AWS info!");
            }
            for (String key : keys) {
                awsvals.put(key, getAWSMetaData(key));
            }
        } catch (Exception e) {
            log4j.error("Could not get build AWS Info", e);
        }
    }

    /**
     * Retrieve AWS metadata via URL
     *
     * @param dataKey
     * @return
     * @throws IOException
     */
    public static String getAWSMetaData(String dataKey) {

        try {
            String url = EVProperties.getProperty(EVProperties.AWS_METADATA_URL);
            String value = "";
            String inputLine;

            if (StringUtils.isNotBlank(url)) {
                if (StringUtils.isNotBlank(dataKey)) {
                    url += "/" + dataKey;
                }

                URL EC2MetaData = new URL(url);

                URLConnection EC2MD = EC2MetaData.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(EC2MD.getInputStream()));

                while ((inputLine = in.readLine()) != null) {
                    value += inputLine;
                }

                in.close();
            } else if ("local".equals(EVProperties.getApplicationProperties().getRunlevel())) {
                value = "local";
            }
            return value;
        } catch (Exception e) {
            log4j.error("Unable to get AWS metadata for '" + dataKey + "'", e);
            return null;
        }
    }

    /**
     * Retrieve AWS info by key
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return awsvals.get(key);
    }

    public String getEc2Id()  {
        return awsvals.get(INSTANCE_ID);
    }

    public String getAmiId() {
        return awsvals.get(AMI_ID);
    }

    public String getHostName() {
        return awsvals.get(HOST_NAME);
    }
}
