package org.ei.backoffice;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.ei.domain.LhlUserInfo;
import org.ei.util.StringUtil;

public final class BackofficeClient {

	private String authURL = StringUtil.EMPTY_STRING;
	//private static String appName = StringUtil.EMPTY_STRING;
	private static BackofficeClient instance;

	private BackofficeClient(String authURL) {
		this.authURL = authURL;

	}

	//This method should be called once by the controller during
	//initialization.

	public static synchronized BackofficeClient getInstance(String authURL)
	{
		if(instance == null) {
			instance = new BackofficeClient(authURL);
		}

		return instance;
	}


	public LhlUserInfo getLhlUserInfo(String strCustomerID, String strContractID)
		throws Exception
	{
		LhlUserInfo lhlUserInfo = null;

		try {
			StringBuffer buf = new StringBuffer();
			buf.append(authURL).append("?");
			buf.append("requestType=g");
			buf.append("&customerID=").append(strCustomerID);
			buf.append("&contractID=").append(strContractID);

			String strResult = returnResultHttpHeader(buf.toString());

			if ((strResult != null) && !strResult.equals(StringUtil.EMPTY_STRING)) {
				lhlUserInfo = new LhlUserInfo();
				lhlUserInfo.loadFromXMLString(strResult);
			}
		} catch(Exception e) {
			throw e;
		}
		finally {
		}

		return lhlUserInfo;
	} // getLhlUserInfo

	public boolean authenticateByIp(String strIpAddress, String strContractID)
		throws Exception
	{

		boolean blnAuth = false;

		try {
			StringBuffer buf = new StringBuffer();
			buf.append(authURL).append("?");
			buf.append("requestType=a");
			buf.append("&contractID=").append(strContractID);
			buf.append("&ipAddress=").append(strIpAddress);

			String strResult = returnResultHttpHeader(buf.toString());

			if ((strResult != null) && !strResult.equals(StringUtil.EMPTY_STRING)) {
				blnAuth = (Boolean.valueOf(strResult)).booleanValue();
			}
		} catch(Exception e) {
			throw e;
		}
		finally {
		}

		return blnAuth;
	} // authenticateByIp

	public boolean authenticateByPassword(String strCustomerID, String strPassword, String strContractID)
		throws Exception
	{

		boolean blnAuth = false;

		try {
			StringBuffer buf = new StringBuffer();
			buf.append(authURL).append("?");
			buf.append("requestType=p");
			buf.append("&contractID=").append(strContractID);
			buf.append("&customerID=").append(strCustomerID);
			buf.append("&password=").append(strPassword);

			String strResult = returnResultHttpHeader(buf.toString());

			if ((strResult != null) && !strResult.equals(StringUtil.EMPTY_STRING)) {
				blnAuth = (Boolean.valueOf(strResult)).booleanValue();
			}
		}
		catch(Exception e) {
			throw e;
		}
		finally {
		}

		return blnAuth;
	} // authenticateByPassword


	public boolean sendOrderInfo(LhlUserInfo lhlUserInfo, String docInfo, String attention, String shipInfo, String deliveryValue, String serviceLevel, String confirmedEmail, String orderNumber)
		throws Exception
	{

		boolean blnUpdated = false;

		try {
			StringBuffer buf = new StringBuffer();
			buf.append(authURL).append("?");
			buf.append("requestType=u");
//			buf.append("&docInfo=").append(docInfo);
//			buf.append("&attention=").append(attention);
//			buf.append("&shipInfo=").append(shipInfo);
//			buf.append("&deliveryValue=").append(deliveryValue);
//			buf.append("&serviceLevel=").append(serviceLevel);
//			buf.append("&confirmedEmail=").append(confirmedEmail);

			String strUserInfoXML = lhlUserInfo.toXMLString();
			Map mapPostData = new Hashtable();
			mapPostData.put("docInfo",docInfo);
			mapPostData.put("attention",attention);
			mapPostData.put("shipInfo",shipInfo);
			mapPostData.put("deliveryValue",deliveryValue);
			mapPostData.put("serviceLevel",serviceLevel);
			mapPostData.put("lhlUserInfoXML",strUserInfoXML);
			mapPostData.put("confirmedEmail",confirmedEmail);
			mapPostData.put("orderNumber",orderNumber);



			String strResult = returnResultHttpHeader(buf.toString(), mapPostData);



			if ((strResult != null) && !strResult.equals(StringUtil.EMPTY_STRING)) {
				blnUpdated = (Boolean.valueOf(strResult)).booleanValue();
			}
		} catch(Exception e) {
			System.out.println("ERROR eeee ");
			throw e;
		}
		finally {
		}

		return blnUpdated;
	} // updateUserInfo

	private String returnResultHttpHeader(String strURL) {
		return returnResultHttpHeader(strURL, null);
	}
	private String returnResultHttpHeader(String strURL, Map mapPostData) {

		String strResult = StringUtil.EMPTY_STRING;
		InputStream in = null;
		PrintWriter out = null;
		HttpURLConnection  ucon = null;

		try {
			//System.out.println("AuthString:["+ strURL + "]");

			URL url = new URL(strURL);
			ucon = (HttpURLConnection) url.openConnection();
			if(mapPostData != null) {
				ucon.setDoOutput(true);
				ucon.setRequestMethod("POST");

				out = new PrintWriter(ucon.getOutputStream());
				Iterator itrPostData = mapPostData.keySet().iterator();
				for(boolean blnFirst = true; itrPostData.hasNext(); blnFirst = false) {
					if(!blnFirst) {
						out.print("&");
					}
					String strKey = (String) itrPostData.next();
					String strValue = (String) mapPostData.get(strKey);
					out.print(strKey.concat("=").concat(URLEncoder.encode(strValue)));
				}
				out.println(StringUtil.EMPTY_STRING);
				out.flush();

			}

			in = ucon.getInputStream();

			for(int j = 1; true; j++) {
				String hkey = ucon.getHeaderFieldKey(j);

				if(hkey == null) {
					break;
				}

				// Get LHL Header
				if(hkey.indexOf("LHL") > -1) {
					strResult = ucon.getHeaderField(j);
					break;
				}
			}


		} catch(Exception e) {
			System.out.println("ERROR returnResultHttpHeader" + e.getMessage());
			//e.printStackTrace();
		} finally {

			try {
				if(out != null) {
					out.close();
				}
				if(in != null) {
					in.close();
				}
				if(ucon != null) {
					ucon.disconnect();
				}
			} catch(Exception e1) {
				System.out.println("ERROR returnResultHttpHeader" + e1.getMessage());
			}
		}
		return strResult;

	} // returnResultHttpHeader

}
