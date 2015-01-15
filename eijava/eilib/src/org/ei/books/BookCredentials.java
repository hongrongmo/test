package org.ei.books;

public class BookCredentials
{
	public static String toString(String[] credentials)
	{
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<credentials.length; i++)
		{
			if(credentials[i].equals("ELE"))
			{
				if(buf.length() > 0)
				{
					buf.append(":");
				}
				buf.append("ELE");
			}
			else if(credentials[i].equals("MAT"))
			{
				if(buf.length() > 0)
				{
					buf.append(":");
				}
				buf.append("MAT");
			}
			else if(credentials[i].equals("CHE"))
			{
				if(buf.length() > 0)
				{
					buf.append(":");
				}
				buf.append("CHE");
			}
			else if(credentials[i].equals("CIV"))
			{
				if(buf.length() > 0)
				{
					buf.append(":");
				}
				buf.append("CIV");
			}
			else if(credentials[i].equals("COM"))
			{
				if(buf.length() > 0)
				{
					buf.append(":");
				}
				buf.append("COM");
			}
			else if(credentials[i].equals("SEC"))
			{
				if(buf.length() > 0)
				{
					buf.append(":");
				}
				buf.append("SEC");
			}
		}

		return buf.toString();
	}
}