package org.ei.books;

import org.ei.books.collections.*;

public class BookCredentials
{
	public static String toString(String[] credentials)
	{
		StringBuffer buf = new StringBuffer();
		ReferexCollection[] allcolls = ReferexCollection.allcolls;
		for(int i=0; i<credentials.length; i++)
		{
        for(int j = 0; j < allcolls.length; j++)
        {
          ReferexCollection acol = allcolls[j];
          if(credentials[i].equals(acol.getAbbrev()))
          {
    				if(buf.length() > 0)
    				{
    					buf.append(":");
    				}
    				buf.append(acol.getAbbrev());
          }
        }
		}

		if(buf.length() > 0)
		{
			buf.append(":");
		}
		buf.append(ReferexCollection.CIV.getAbbrev());

		if(buf.length() > 0)
		{
			buf.append(":");
		}
		buf.append(ReferexCollection.COM.getAbbrev());

		if(buf.length() > 0)
		{
			buf.append(":");
		}
		buf.append(ReferexCollection.SEC.getAbbrev());

		return buf.toString();
	}
}