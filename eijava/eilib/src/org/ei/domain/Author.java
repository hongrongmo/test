package org.ei.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import java.io.*;
import java.net.*;

import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;


import org.ei.data.*;
import org.ei.query.base.*;
import org.ei.util.StringUtil;


public class Author
{
    Perl5Util util = new Perl5Util();

    public static DataCleaner dataCleaner = new DataCleaner();

    public static void main(String[] args)
    {
        Author au = new Author();
        String strauthor = args[0];
        String strtag = args[1];
        String strauthorxml = new String();
        strauthorxml = au.getAuthorXML(strauthor,strtag);
    }

    public Author()
    {
    }

    public String getAuthorXML(String strAuthors,String strTag)
    {
 	    StringBuffer strXML = new StringBuffer();
        AuthorStream aStream = null;

    	char[] f = new char[1];
		f[0] = (char)31;
        String iddelimiter = new String(f);

        strAuthors = dataCleaner.cleanEntitiesForDisplay(strAuthors);
        try {
            aStream = new AuthorStream(new ByteArrayInputStream(strAuthors.getBytes()));
            boolean blnFirstAuthor = true;
            String strToken = null;
            String id = null;
            String idsubscript = null;
            while((strToken = aStream.readAuthor()) != null)
            {

                strXML.append("<").append(strTag);

			   if(strToken.indexOf(iddelimiter) > -1) {
				  int i = strToken.indexOf(iddelimiter);
				  id = strToken.substring(i+1);
				  int j = id.lastIndexOf('.');
				   if(j != -1)
				   {
						idsubscript = id.substring(j+1);
         		   }
				  strToken = strToken.substring(0,i);
  			  		if(idsubscript != null && !idsubscript.equals(StringUtil.EMPTY_STRING)) {
							strXML.append(" id=").append("\"").append(idsubscript).append("\"");
					 }
			    }
                strXML.append(">");
                strXML.append("<![CDATA[").append(strToken).append("]]>");
                strXML.append("</").append(strTag).append(">");

            }
        } catch (IOException ioe) {
            System.out.println("IOE " + ioe.getMessage());

        } finally {
            if(aStream != null)
                try {
                    aStream.close();
                    aStream = null;
                } catch (IOException ioe) {
                }
        }
       return strXML.toString();
   }


}



