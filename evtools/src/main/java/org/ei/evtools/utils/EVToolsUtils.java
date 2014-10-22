package org.ei.evtools.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public class EVToolsUtils {

	 private static Logger logger = Logger.getLogger(EVToolsUtils.class);
	 
	 private static List<String> usageOptions = new ArrayList<String>(Arrays.asList("downloadformat", "ip","acctNo"));
	
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
		return usageOptions.contains(usageOption);
	}
}
