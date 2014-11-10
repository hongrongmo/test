/**
 *
 */
package org.ei.biz.personalization;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.service.amazon.AmazonServiceHelper;
import org.ei.session.UserPreferences;
import org.ei.stripes.action.delivery.AbstractDeliveryAction;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

/**
 * @author harovetm
 *
 */
@DynamoDBTable(tableName = "UserPrefs")
public class UserPrefs  {
    private static final Logger log4j = Logger.getLogger(UserPrefs.class);


    public static final String KEY_USERID = "User ID";
    public static final String ATTRIBUTE_ENVIRONMENT = "Environment";
    public static final String ATTRIBUTE_DL_FORMAT = "DL_FORMAT";
    public static final String ATTRIBUTE_DL_OUTPUT = "DL_OUTPUT";
    public static final String ATTRIBUTE_DL_LOCATION = "DL_LOCATION";
    public static final String ATTRIBUTE_RESULTS_PER_PAGE = "RESULTS_PER_PAGE";
    public static final String ATTRIBUTE_SHOW_PREVIEW = "SHOW_PREVIEW";
    public static final String ATTRIBUTE_SORT = "SORT";
    public static final String ATTRIBUTE_TIMESTAMP = "Timestamp";
    public static final String ATTRIBUTE_HIGHLIGHT = "HIGHLIGHT_COLOR";
    private static final String ATTRIBUTE_HIGHLIGHT_BG = "HIGHLIGHT_BG";
    private static final String ATTRIBUTE_DL_FILENAME_PFX = "DL_FILENAME_PFX";
    private static final DynamoDBMapperConfig saveconfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER);
    public static final String HIGHTLIGHT_COLOR = "#ff8200";
    public static final String DL_FILENAME_PFX = "Engineering_Village";



    private String userid;
    private String environment;
    private String downloadformat;
    private String downloadoutput;
    private String downloadLocation;
    private String downloadFileNamePrefix;

	private int resultsperpage;
    private boolean showpreview;
    private String sort;
    private Date timestamp = new Date();
    private String highlight = HIGHTLIGHT_COLOR;
    private boolean highlightBackground = false;

    private ArrayList<String> resultsPerPageOpt = new ArrayList<String>();

    public UserPrefs() {
        try {
            this.resultsPerPageOpt = getResultsPerPageOptions();
            this.environment = EVProperties.getApplicationProperties().getRunlevel();
            setDefaults();
        } catch (Throwable t) {
            this.environment = ApplicationProperties.RUNLEVEL_PROD;
        }
    }

    public UserPrefs(String userid) {
        this();
        this.userid = userid;
    }

    /**
     * Set the default user prefs.
     */
    @DynamoDBIgnore
    private void setDefaults(){
        this.resultsperpage = Integer.parseInt(this.resultsPerPageOpt.get(0));
        this.sort = UserPreferences.EVPREFS_SORT_REL;
        this.showpreview = false;
        this.downloadoutput = UserPreferences.EVPREFS_DL_OUTPUT_DEF;
        this.downloadformat = AbstractDeliveryAction.DOWNLOAD_FORMAT_PDF;
        this.downloadLocation = UserPreferences.EVPREFS_DL_LOC_PC;
        this.highlight = HIGHTLIGHT_COLOR;
        this.highlightBackground = false;
        this.downloadFileNamePrefix = DL_FILENAME_PFX;
    }

    /**
     * Get the result options for the results page.
     * @return
     */
    @DynamoDBIgnore
    public static ArrayList<String> getResultsPerPageOptions(){
        String pageSizeOptions = EVProperties.getProperty(ApplicationProperties.PAGESIZE_OPTIONS);
        StringTokenizer st = new StringTokenizer(pageSizeOptions, ",");
        ArrayList<String> pageOptions = new ArrayList<String>();
        if (null != st) {
            while (st.hasMoreTokens()) {
                String value = st.nextToken();
                if (!value.isEmpty()) {
                    pageOptions.add(value);
                }
            }
        }
        return pageOptions;
    }

    @DynamoDBIgnore
    public ArrayList<String> getResultsPerPageOpt() {
        return resultsPerPageOpt;
    }

    @DynamoDBHashKey(attributeName = KEY_USERID)
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @DynamoDBRangeKey(attributeName = ATTRIBUTE_ENVIRONMENT)
    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_DL_FORMAT)
    public String getDlFormat() {
        return this.downloadformat;
    }

    public void setDlFormat(String downloadformat) {
        this.downloadformat = downloadformat;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_DL_OUTPUT)
    public String getDlOutput() {
        return this.downloadoutput;
    }

    public void setDlOutput(String downloadoutput) {
        this.downloadoutput = downloadoutput;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_DL_LOCATION)
    public String getDlLocation() {
        return this.downloadLocation;
    }

    public void setDlLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_RESULTS_PER_PAGE)
    public int getResultsPerPage() {
        return this.resultsperpage;
    }

    public void setResultsPerPage(int resultsperpage) {
        this.resultsperpage = resultsperpage;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_SHOW_PREVIEW)
    public boolean getShowPreview() {
        return this.showpreview;
    }

    public void setShowPreview(boolean showpreview) {
        this.showpreview = showpreview;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_SORT)
    public String getSort() {
        return this.sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
    @DynamoDBAttribute(attributeName = ATTRIBUTE_HIGHLIGHT)
    public String getHighlight() {
		return highlight;
	}

	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}

    @DynamoDBAttribute(attributeName = ATTRIBUTE_TIMESTAMP)
    public Date getTimestamp() {
        return timestamp;
    }

    @DynamoDBAttribute(attributeName = ATTRIBUTE_HIGHLIGHT_BG)
    public boolean getHighlightBackground() {
		return highlightBackground;
	}

    public void setHighlightBackground(boolean highlightBackground) {
		this.highlightBackground = highlightBackground;
	}

    @DynamoDBAttribute(attributeName = ATTRIBUTE_DL_FILENAME_PFX)
    public String getDlFileNamePrefix() {
		return downloadFileNamePrefix;
	}

	public void setDlFileNamePrefix(String downloadFileNamePrefix) {
		this.downloadFileNamePrefix = downloadFileNamePrefix;
	}

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    @DynamoDBIgnore
    public String getPrettyTimestamp() {
        if (this.timestamp == null) return "";
        else return dateFormatter.format(this.timestamp);
    }


    @DynamoDBIgnore
    private static String getCurrentEnvironment() {
        // Set then environment from current runtime properties
        String environment = ApplicationProperties.RUNLEVEL_PROD;
        try {
            environment = EVProperties.getApplicationProperties().getRunlevel();
        } catch (Throwable t) {
            log4j.error("Unable to retrieve ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL", t);
            environment = ApplicationProperties.RUNLEVEL_PROD;
        }
        return environment;
    }

    /**
     * Retrieve all BlockedIPEvent objects by Environment
     * @param IP
     * @param status
     * @return
     */
    public static List<UserPrefs> getByEnvironment(String environment) {
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        // Add Environment to scan filter
        scanExpression.addFilterCondition("Environment",
            new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(new AttributeValue().withS(environment)));

        return mapper.scan(UserPrefs.class, scanExpression);
    }

    /**
     * Delete the blocking events for an Environment
     * @param ipstatus
     */
    public static void deleteByEnvironment(String environment) {
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
        mapper.batchDelete(UserPrefs.getByEnvironment(environment));
    }

    /**
     * Retrieve status by IP
     *
     * @param IP
     * @return
     */
    public static UserPrefs load(String userid, String environment) {
        if (GenericValidator.isBlankOrNull(userid)) return null;
        // Get DynamoDB client
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
        return mapper.load(UserPrefs.class, userid, environment);
    }

    /**
     * Retrieve status by IP
     *
     * @param IP
     * @return
     */
    public static UserPrefs load(String userid) {
        return load(userid, getCurrentEnvironment());
    }

    /**
     * Save the blocking status of an IP address
     * @param ipstatus
     */
    public void save() {
        if (GenericValidator.isBlankOrNull(this.userid)) {
            log4j.warn("Attempt to save UserPrefs without user ID!");
            return;
        }
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);

        mapper.save(this, saveconfig);
    }

    /**
     * Delete the blocking status of an IP address
     * @param ipstatus
     */
    public void delete() {
        if (GenericValidator.isBlankOrNull(this.userid)) {
            log4j.warn("Attempt to delete UserPrefs without user ID!");
            return;
        }
        // Get DynamoDB client
        AmazonDynamoDBClient dynamoDBClient = AmazonServiceHelper.getInstance().getAmazonDynamoDBClient();
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDBClient);
        mapper.delete(this, saveconfig);
    }



}
