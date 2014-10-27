/** This is a basic Type for Contact Info which has the details of contact Information for a user.
*/
package org.ei.domain;

import java.io.ByteArrayInputStream;

import org.ei.util.StringUtil;
import org.ei.xml.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ContactInfo
{

	//First Name of user
	private String userFirstName = StringUtil.EMPTY_STRING;
	//Last name of user
	private String userLastName = StringUtil.EMPTY_STRING;
	//Email of user
	private String userEmail=StringUtil.EMPTY_STRING;
	//Company name of user
	private String company=StringUtil.EMPTY_STRING;
	//Primary Address
	private String address1=StringUtil.EMPTY_STRING;
	//City of user
	private String city=StringUtil.EMPTY_STRING;
	//State of user
	private String state=StringUtil.EMPTY_STRING;
	//Zip Code
	private String zip=StringUtil.EMPTY_STRING;
	//Phone number of user
	private String phoneNumber=StringUtil.EMPTY_STRING;
	//Fax numer of user
	private String faxNumber=StringUtil.EMPTY_STRING;
	//User Account Number
	private String accountNumber=StringUtil.EMPTY_STRING;
	//Country of usre
	private String country=StringUtil.EMPTY_STRING;
	//Address2 of User
	private String address2=StringUtil.EMPTY_STRING;



	public ContactInfo()
	{
		//Default Constructor.
	}

	//Get and set methods for userFirstna
	public void setUserFirstName(String userFirstName)
	{
		this.userFirstName = userFirstName;
	}

	public String getUserFirstName()
	{
		return this.userFirstName;
	}

	//Get and set methods for userLastName
	public void setUserLastName(String userLastName)
	{
		this.userLastName = userLastName;
	}

	public String getUserLastName()
	{
		return this.userLastName;
	}
	//Set and get methods for userEmail
	public void setUserEmail(String userEmail)
	{
		this.userEmail=userEmail;
	}

	public String getUserEmail()
	{
		return this.userEmail;
	}


	//set and get methods for company
	public void setCompany(String company)
	{
		this.company=company;
	}

	public String getCompany()
	{
		return this.company;
	}

	//set and get methods for address1
	public void setAddress1(String address1)
	{
		this.address1=address1;
	}

	public String getAddress1()
	{
		return this.address1;
	}

	//set and get methods for city
	public void setCity(String city)
	{
		this.city=city;
	}

	public String getCity()
	{
		return this.city;
	}

	//set and get methods for state
	public void setState(String state)
	{
		this.state= state;
	}

	public String getState()
	{
		return this.state;
	}

	//set and get methods for zip
	public void setZip(String zip)
	{
		this.zip=zip;
	}

	public String getZip()
	{
		return this.zip;
	}


	//set and get methods for phoneNumber
	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber=phoneNumber;
	}

	public String getPhoneNumber()
	{
		return this.phoneNumber;
	}


	//set and get methods for faxNumber
	public void setFaxNumber(String faxNumber)
	{
		this.faxNumber=faxNumber;
	}

	public String getFaxNumber()
	{
		return this.faxNumber;
	}


	//set and get methods for accountNumber
	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber=accountNumber;
	}

	public String getAccountNumber()
	{
		return this.accountNumber;
	}

	//set and get methods for country
	public void setCountry(String country)
	{
		this.country=country;
	}

	public String getCountry()
	{
		return this.country;
	}

	//set and get methods for address2
	public void setAddress2(String address2)
	{
		this.address2=address2;
	}

	public String getAddress2()
	{
		return this.address2;
	}


	//to string of this object
	public String toString()
	{
		StringBuffer sBuf=new StringBuffer();
		sBuf.append("\n\tuserFirstName  :").append(userFirstName);
		sBuf.append("\n\tuserLastName  :").append(userLastName);
		sBuf.append("\n\tuserEmail  :").append(userEmail);
		sBuf.append("\n\tcompany  :").append(company);
		sBuf.append("\n\taddress1  :").append(address1);
		sBuf.append("\n\tcity  :").append(city);
		sBuf.append("\n\tstate  :").append(state);
		sBuf.append("\n\tzip  :").append(zip);
		sBuf.append("\n\tphoneNumber   :").append(phoneNumber);
		sBuf.append("\n\tfaxNumber   :").append(faxNumber);
		sBuf.append("\n\taccountNumber   :").append(accountNumber);
		sBuf.append("\n\tcountry   :").append(country);
		sBuf.append("\n\taddress2   :").append(address2);

		return sBuf.toString();
	}

	//To xml string
	public String toXMLString()
	{
		StringBuffer xmlString=new StringBuffer();
		xmlString.append("<CONTACT-INFO>");
		xmlString.append("<FIRST-NAME>").append(userFirstName).append("</FIRST-NAME>");
		xmlString.append("<LAST-NAME>").append(userLastName).append("</LAST-NAME>");
		xmlString.append("<EMAIL>").append(userEmail).append("</EMAIL>");
		xmlString.append("<COMPANY-NAME>").append(company).append("</COMPANY-NAME>");
		xmlString.append("<ADDRESS1>").append(address1).append("</ADDRESS1>");
		xmlString.append("<CITY>").append(city).append("</CITY>");
		xmlString.append("<STATE>").append(state).append("</STATE>");
		xmlString.append("<ZIP>").append(zip).append("</ZIP>");
		xmlString.append("<PHONE>").append(phoneNumber).append("</PHONE>");
		xmlString.append("<FAX>").append(faxNumber).append("</FAX>");
		xmlString.append("<ACCOUNT-NUMBER>").append(accountNumber).append("</ACCOUNT-NUMBER>");
		xmlString.append("<COUNTRY>").append(country).append("</COUNTRY>");
		xmlString.append("<ADDRESS2>").append(address2).append("</ADDRESS2>");
		xmlString.append("</CONTACT-INFO>");

		return xmlString.toString();
	}

	public void loadFromXMLString(String strXML)
		throws Exception
	{

		DOMParser parser = new DOMParser();
		Document doc = parser.parse(new ByteArrayInputStream(strXML.getBytes()));

		this.loadFromXMLDocument(doc);
	}
	public void loadFromXMLDocument(Document doc)
		throws Exception
	{

		NodeList aNodes = doc.getElementsByTagName("FIRST-NAME");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setUserFirstName(strVal);
			}
		}
		aNodes = doc.getElementsByTagName("LAST-NAME");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setUserLastName(strVal);
			}
		}
		aNodes = doc.getElementsByTagName("EMAIL");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setUserEmail(strVal);
			}
		}

		aNodes = doc.getElementsByTagName("COMPANY-NAME");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setCompany(strVal);
			}
		}

		aNodes = doc.getElementsByTagName("ADDRESS1");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setAddress1(strVal);
			}
		}
		aNodes = doc.getElementsByTagName("ADDRESS2");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setAddress2(strVal);
			}
		}
		aNodes = doc.getElementsByTagName("CITY");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setCity(strVal);
			}
		}
		aNodes = doc.getElementsByTagName("STATE");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setState(strVal);
			}
		}
		aNodes = doc.getElementsByTagName("COUNTRY");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setCountry(strVal);
			}
		}
		aNodes = doc.getElementsByTagName("ZIP");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setZip(strVal);
			}
		}
		aNodes = doc.getElementsByTagName("PHONE");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setPhoneNumber(strVal);
			}
		}
		aNodes = doc.getElementsByTagName("FAX");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setFaxNumber(strVal);
			}
		}
		aNodes = doc.getElementsByTagName("ACCOUNT-NUMBER");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setAccountNumber(strVal);
			}
		}
	}

}
