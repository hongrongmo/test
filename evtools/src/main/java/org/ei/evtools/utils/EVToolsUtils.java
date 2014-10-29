package org.ei.evtools.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public class EVToolsUtils {

	 private static Logger logger = Logger.getLogger(EVToolsUtils.class);
	 
	 private static final String validip = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	 private static Pattern ippattern =Pattern.compile(validip);
	 private static Matcher ipmatcher;
	 
	 private static List<String> usageOptions = new ArrayList<String>(Arrays.asList("downloadformat", "ip","acctNo"));
	 static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	 
    public enum TimePeriod {
        LASTYEAR(Calendar.YEAR, -1),
        LAST6MONTHS(Calendar.MONTH, -6),
        LASTMONTH(Calendar.MONTH, -1),
        LASTWEEK(Calendar.WEEK_OF_YEAR, -1),
        LASTDAY(Calendar.DAY_OF_YEAR, -1);

        int field;
        int period;
        TimePeriod(int field, int period) {
            this.field = field;
            this.period = period;
        }

        public Date date() {
            Calendar timestamp = Calendar.getInstance();
            timestamp.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            timestamp.add(field, period);
            return timestamp.getTime();
        }

        public String toString() {
            dateFormatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            return dateFormatter.format(this.date());
        }
    }
	
	 public static boolean isEmptyorNull(String value){
    	if(value == null || value.trim().equalsIgnoreCase("")) {
    		return true;
    	}
    	return false;
	 }
	 
	 public static boolean isValidDate(String dateString, String format){
		 try{
			 new SimpleDateFormat(format, Locale.ENGLISH).parse(dateString);
			 return true;
		 }catch(ParseException pexp){
			 logger.warn("Invalid date string passed!, dateString ="+dateString);
			 return false;
		 }
		
	 }
	 
	 public static Date convertStringToDate(String dateString, String format){
		 try{
			 Date date = new SimpleDateFormat(format, Locale.ENGLISH).parse(dateString);
			 return date;
		 }catch(ParseException pexp){
			 logger.warn("Invalid date string passed, could not convert to date object!, dateString ="+dateString);
			 return null;
		 }
	 }
	 
	public static boolean isValidUsageOption(String usageOption){
		if (!StringUtils.isNotBlank(usageOption)) {
            return false;
        }
    	if (!usageOptions.contains(usageOption)) {
            return false;
        }
		return true;
	}
	
	 /**
     * Utility method to determine if IP is valid
     * @param ipaddress
     * @return
     */
    public static boolean isValidIP(String ipaddress) {
        ipaddress = ipaddress.trim();
        if (!StringUtils.isNotBlank(ipaddress)) {
            return false;
        }
        ipmatcher = ippattern.matcher(ipaddress);
        if (!ipmatcher.matches()) {
            logger.error("Invalid IP format: '" + ipaddress + "'!");
            return false;
        }
        return true;
    }
    
    // This will not have default level, since that is not valid env 
    public static List<String> getEnvRunLevels(){
    	List<String> envrunlevellist = new ArrayList<String>();
        envrunlevellist.add(EVToolsConstants.ATTRIBUTE_CERT);
        envrunlevellist.add(EVToolsConstants.ATTRIBUTE_DEV);
        envrunlevellist.add(EVToolsConstants.ATTRIBUTE_LOCAL);
        envrunlevellist.add(EVToolsConstants.ATTRIBUTE_PROD);
        envrunlevellist.add(EVToolsConstants.ATTRIBUTE_RELEASE);
        return envrunlevellist;
    }
    
    public static boolean isValidEnv(String env) {
    	if (!StringUtils.isNotBlank(env)) {
            return false;
        }
    	if (!EVToolsUtils.getEnvRunLevels().contains(env)) {
            return false;
        }
    	return true;
    }
    
    public static boolean isValidIPBlockerStatus(String status) {
    	if (!StringUtils.isNotBlank(status)) {
            return false;
        }
    	if (status.equalsIgnoreCase("true") || status.equalsIgnoreCase("false")) {
            return true;
        }
    	return false;
    }
    
   
    
}
