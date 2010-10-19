package org.ei.backoffice;


import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ei.domain.LhlUserInfo;
import org.ei.util.StringUtil;

public class LindaHallService extends HttpServlet
{
	private StringUtil sUtil = new StringUtil();
	private LindaHallBroker lhlBroker;


	public void init()
		throws ServletException
	{
		ServletConfig config = getServletConfig();
		lhlBroker = new LindaHallBroker();
	}



	public void service(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException
	{

		LhlUserInfo lhlUserInfo = null;
		String strCustomerID = request.getParameter("customerID");
		String strContractID= request.getParameter("contractID");
		String requestType = request.getParameter("requestType");
		String strIpAddress = request.getParameter("ipAddress");
		String strUsername= request.getParameter("username");
		String strPassword = request.getParameter("password");

		try {
			ServletOutputStream out = response.getOutputStream();
			if(requestType.equals("g")) {

				lhlUserInfo = lhlBroker.getLhlUserInfo(strCustomerID, strContractID);

				if(lhlUserInfo != null) {
					String strXML = lhlUserInfo.toXMLString();
					response.setHeader("LHLINFO", strXML);
				}
			} else if(requestType.equals("a")) {
				boolean auth = lhlBroker.authenticateByIp(strIpAddress, strContractID);
				response.setHeader("LHLAUTH", (new Boolean(auth)).toString());
			} else if(requestType.equals("p")) {
				boolean auth = lhlBroker.authenticateByPassword(strCustomerID, strPassword, strContractID);
				response.setHeader("LHLAUTH", (new Boolean(auth)).toString());
			} else if(requestType.equals("u")) {

				String docInfo = request.getParameter("docInfo");
				String attention = request.getParameter("attention");
				String shipInfo = request.getParameter("shipInfo");
				String deliveryValue = request.getParameter("deliveryValue");
				String serviceLevel = request.getParameter("serviceLevel");
				String orderNumber 		= request.getParameter("orderNumber");
				String confirmedEmail = request.getParameter("confirmedEmail");

				String lhlUserInfoXML = request.getParameter("lhlUserInfoXML");

				lhlUserInfo = new LhlUserInfo();
				lhlUserInfo.loadFromXMLString(lhlUserInfoXML);

				boolean blnUpdate = lhlBroker.sendOrderInfo(lhlUserInfo,docInfo,attention,shipInfo,deliveryValue,serviceLevel,orderNumber,confirmedEmail);

				response.setHeader("LHLUPDATE", (new Boolean(blnUpdate)).toString());

			}

		} catch(Exception e) {
			log("Error:",e);
		}
	}
}
