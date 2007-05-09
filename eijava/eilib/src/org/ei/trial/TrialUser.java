package org.ei.trial;

import java.sql.*;
/**
*	This class represents encapsulates the concept of a Trial User
*	who is registering for the system.
**/

public class TrialUser
{
	private String referringUrl = "-";
	private String firstName = "-";
	private String lastName = "-";
	private String jobtitle = "-";
	private String company = "-";
	private String website = "-";
	private String address1 = "-";
	private String address2 = "-";
	private String city = "-";
	private String state = "-";
	private String zip = "-";
	private String country = "-";
	private String phone = "-";
	private String email = "-";
	private String howHear = "-";
	private String product= "-";
	private String trialDate=null;
	private String byMail="-";
	private String byEmail="-";
	private String promoID = "-";
	private String howHearExplained = "-";
	private String indexID = "-";

	public String getReferringURL()
	{
		return this.referringUrl;
	}

	public void setReferringURL(String referringUrl)
	{
		this.referringUrl = referringUrl;
	}

	public String getFirstName()
	{
		return this.firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getIndexID()
	{
		return this.indexID;
	}

	public void setIndexID(String indexID)
	{
		this.indexID = indexID;
	}

	public String getLastName()
	{
		return this.lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getWebSite()
	{
		return this.website;
	}

	public void setWebSite(String website)
	{
		this.website = website;
	}

	public String getByMail()
	{
		return this.byMail;
	}

	public void setByMail(String byMail)
	{
		this.byMail = byMail;
	}

	public String getByEmail()
	{
		return this.byEmail;
	}

	public void setByEmail(String byEmail)
	{
		this.byEmail = byEmail;
	}

	public String getProduct()
	{
		return this.product;
	}


	public void setProduct(String product)
	{
		this.product = product;
	}

	public String getJobTitle()
	{
		return this.jobtitle;
	}

	public void setJobTitle(String jobtitle)
	{
		this.jobtitle = jobtitle;
	}


	public String getAddress1()
	{
		return this.address1;
	}

	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	public String getAddress2()
	{
		return this.address2;
	}

	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	public String getCompany()
	{
		return this.company;
	}

	public void setCompany(String company)
	{
		this.company = company;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getCity()
	{
		return this.city;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getState()
	{
		return this.state;
	}

	public String getZip()
	{
		return this.zip;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}

	public String getCountry()
	{
		return this.country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getPhone()
	{
		return this.phone;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getHowHear()
	{
		return this.howHear;
	}

	public void setHowHear(String howHear)
	{
		this.howHear = howHear;
	}

	public String getTrialDate()
	{
		return trialDate;
	}

	public void setTrialDate(String trialDate)
	{
		this.trialDate = trialDate;
	}


	public String getHowHearExplained()
	{
		return this.howHearExplained;
	}

	public void setHowHearExplained(String howHearExplained)
	{
		this.howHearExplained = howHearExplained;
	}

}


