package org.ei.gui;

import java.util.*;

public class CustomerImage
{

	private static Map customerMap = new HashMap();
	private static String[] TAN  = {"tan.jpg", "http://www.ulakbim.gov.tr/cabim/ekual/"};
    private static String[] FCCN = {"fccn.jpg", "/controller/servlet/Controller"};

	public static boolean containsCustomer(String custID)
	{
		if(custID == null)
		{
			return false;
		}

		return customerMap.containsKey(custID);
	}

	public static String getImage(String custID)
	{
		String[] info = (String[])customerMap.get(custID);
		return info[0];
	}

	public static String getURL(String custID)
	{
		String[] info = (String[])customerMap.get(custID);
		return info[1];
	}

	static
	{
		customerMap.put("1001772",  TAN);
		customerMap.put("933375",   TAN);
		customerMap.put("933381",   TAN);
		customerMap.put("933385",   TAN);
		customerMap.put("933386",   TAN);
		customerMap.put("1003050",  TAN);
		customerMap.put("1001790",  TAN);
		customerMap.put("1002275",  TAN);
		customerMap.put("1002757",  TAN);
		customerMap.put("1002680",  TAN);
		customerMap.put("1002799",  TAN);
		customerMap.put("1002826",  TAN);
		customerMap.put("1002826",  TAN);
		customerMap.put("1002840",  TAN);
		customerMap.put("1001091",  TAN);
		customerMap.put("1002869",  TAN);
		customerMap.put("1002875",  TAN);
		customerMap.put("1002878",  TAN);
		customerMap.put("994541",   TAN);
		customerMap.put("993943",   TAN);
		customerMap.put("993928",   TAN);
		customerMap.put("1001572",  TAN);
		customerMap.put("1000755",  TAN);
		customerMap.put("996697",   TAN);
		customerMap.put("1003169",  TAN);
		customerMap.put("1001575",  TAN);
		customerMap.put("1001718",  TAN);
		customerMap.put("1002021",  TAN);
		customerMap.put("996803",   TAN);
		customerMap.put("997994",   TAN);
		customerMap.put("1001386",  TAN);
		customerMap.put("998418",   TAN);
		customerMap.put("999322",   TAN);
		customerMap.put("1000560",  TAN);
		customerMap.put("1000288",  TAN);
		customerMap.put("1001645",  TAN);
		customerMap.put("1000772",  TAN);
		customerMap.put("1001028",  TAN);
		customerMap.put("1000818",  TAN);
		customerMap.put("1000894",  TAN);
		customerMap.put("1000688",  TAN);
		customerMap.put("1000690",  TAN);
		customerMap.put("21004323", TAN);
		customerMap.put("21013108", TAN);
		customerMap.put("21013418", TAN);
		customerMap.put("21013445", TAN);
		customerMap.put("21020630", TAN);
		customerMap.put("21020636", TAN);
		customerMap.put("21020643", TAN);
		customerMap.put("21020677", TAN);
		customerMap.put("21020694", TAN);
		customerMap.put("21013249", TAN);
		customerMap.put("21021799", TAN);
		customerMap.put("21021804", TAN);
		customerMap.put("21021809", TAN);
		customerMap.put("21012597", TAN);
		customerMap.put("21012813", TAN);
		customerMap.put("21013032", TAN);
		customerMap.put("21013326", TAN);
		customerMap.put("21012741", TAN);
		customerMap.put("21012674", TAN);
		customerMap.put("21012732", TAN);
		customerMap.put("21012997", TAN);
		customerMap.put("21013365", TAN);
		customerMap.put("21013375", TAN);
		customerMap.put("21013398", TAN);
		customerMap.put("21010854", TAN);
		customerMap.put("21013484", TAN);
		customerMap.put("1003251",  TAN);
		customerMap.put("1003255",  TAN);
		customerMap.put("21020705", TAN);
		customerMap.put("21020710", TAN);
		customerMap.put("21020715", TAN);
		customerMap.put("21020720", TAN);
		customerMap.put("21013471", TAN);
		customerMap.put("21004694", TAN);
		customerMap.put("21005451", TAN);
		customerMap.put("21020654", TAN);
		customerMap.put("21020866", TAN);
		customerMap.put("21001854", TAN);
		customerMap.put("21008878", TAN);
		customerMap.put("21009199", TAN);
		customerMap.put("21013573", TAN);
		customerMap.put("21013608", TAN);
		customerMap.put("21040420", FCCN);
		customerMap.put("13800",    FCCN);
		customerMap.put("1000860",  FCCN);
		customerMap.put("1000866",  FCCN);
		customerMap.put("1001424",  FCCN);
		customerMap.put("21040636", FCCN);
		customerMap.put("21040642", FCCN);
		customerMap.put("21040730", FCCN);
		customerMap.put("21034742", FCCN);
		customerMap.put("21013983", FCCN);
		customerMap.put("21003191", FCCN);
	}
}