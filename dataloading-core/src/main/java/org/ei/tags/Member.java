package org.ei.tags;

import java.io.IOException;
import java.io.Writer;

public class Member
{
	private String memberID;
	private String email;
	private String title;
	private String firstName;
	private String lastName;
	private String fullName;

	public Member(String memberID,
		      	  String email)
	{
		this.memberID = memberID;
		this.email = email;
	}

	public Member(String memberID,
				  String email,
				  String title,
				  String firstName,
				  String lastName)
	{
		this.memberID = memberID;
		this.email = email;
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		setFullName(title, firstName, lastName);
	}

	public String getEmail()
	{
	    return email;
	}

	public String getMemberID()
	{
		return this.memberID;
	}

	private void setFullName(String title,
							 String firstName,
							 String lastName)
	{
		StringBuffer buf = new StringBuffer();
		if(title != null)
		{
			buf.append(title);
			buf.append(" ");
		}

		if(firstName != null)
		{
			buf.append(firstName);
			buf.append(" ");
		}

		if(lastName != null)
		{
			buf.append(lastName);
		}

		this.fullName = buf.toString();
	}

	public String getFullName()
	{
		return this.fullName;
	}


	public void toXML(Writer out)
		throws IOException
	{
		out.write("<MEMBER>");
		out.write("<MEMBER-ID>");
		out.write(memberID);
		out.write("</MEMBER-ID>");
		out.write("<MEMBER-EMAIL><![CDATA[");
		out.write(email);
		out.write("]]></MEMBER-EMAIL>");
		out.write("<FULL-NAME><![CDATA[");
		out.write(getFullName());
		out.write("]]></FULL-NAME>");
		out.write("</MEMBER>");
	}

}