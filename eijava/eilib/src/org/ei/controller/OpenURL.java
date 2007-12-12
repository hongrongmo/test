package org.ei.controller;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.domain.Query;
import org.ei.domain.SearchForm;

/**
 *  an openURL resolver
 */

public class OpenURL extends HttpServlet
{


  private Perl5Util perl = new Perl5Util();


  public void service(HttpServletRequest request,
            HttpServletResponse response)
        throws IOException, ServletException
  {


    String serverName = request.getServerName();
    int serverPort = request.getServerPort();
    if(serverPort != 80)
    {
        serverName = serverName+":"+Integer.toString(serverPort);
    }


    //variable to hold document type
    String genre = null;

    //variable to hold first author's last name
    String aulast = null;

    //variable to hold international standard serial number
    String issn = null;

    //variable to hold CODEN
    String coden = null;

    //variable to hold international standard book number
    String isbn = null;

    //variable to hold the title of a bundle(journal, book, conference)
    String title = null;

    //variable to hold the abbreviated title of a bundle
    String stitle = null;

    //variable to hold the title of an individual item
    String atitle = null;

    //variable to hold the publication date
    String date = null;

    //variable to hold the issue
    String issue = null;

    //variable to hold the volume
    String volume = null;

    //variable to hold the starting page
    String spage = null;

    //variable to hold service id and database id
    String[] sid = null;

    //variable to hold database ID
    String dbID = null;
    //Variable to hold full text serch terms
    String terms = null;

    //variable to hold searchWord1 string
    StringBuffer searchWord = new StringBuffer();

    String startYear = null;

    String endYear = null;

	int dbMask = 0;

    /** get all parameters through OpenURL
     *  currently we support 9 out of 25 fields, will add more later
     */

    /** genre(DT), aulast(AU), issn(SN), coden(CN), isbn(BN),
     *  title(ST), atitle(TI), stitle(ST), date(YR)
     *  terms (ALL) - not supported by OpenURL Spec
     */

    /** 10/21/03
     *  added issue(SU), volume(VO), spage(SP)
     *  total fields supported: 12
     */


    try
    {

        genre = request.getParameter("genre");

        if(genre != null &&
           genre.equals("book"))
        {
            response.sendRedirect("http://"+serverName+"/controller/servlet/Controller?CID=quickSearchCitationFormat&database=131072&yearselect=yearrange&searchtype=Book&allcol=ALL&section1=BN&searchWord1="+prep(request.getParameter("isbn")));
        }
        else
        {
            if(genre != null)
            {
            	if (genre.equalsIgnoreCase("article"))
            	{
            	  genre = "(JA) wn DT";
            	}
            	else if (genre.equalsIgnoreCase("proceeding"))
            	{
            	  genre = "(CA) OR (CP) wn DT";
            	}
            	else if (genre.equalsIgnoreCase("conference"))
            	{
            	  genre = "(CA) OR (CP) wn DT";
            	}
            	else
            	{
            	  genre = null;
            	}
			}


			if (genre != null)
			{
				if (searchWord.length() == 0)
			 	{
					searchWord.append("(").append(genre).append(")");
			  	}
			  	else
			  	{
					searchWord.append(" and (").append(genre).append(")");
			  	}
			}

			if((request.getParameter("terms") != null) &&
			  (request.getParameter("terms").trim().length() > 0))
			{
				terms = request.getParameter("terms");
				if (searchWord.length() == 0)
				{
					searchWord.append(" (").append(terms).append(") ");
				}
				else
				{
					searchWord.append(" and (").append(terms).append(") ");
				}
			}



          if((request.getParameter("aulast") != null) &&
              (request.getParameter("aulast").trim().length() > 0))
          {
            	aulast = request.getParameter("aulast");
            	if (searchWord.length() == 0)
            	{
              		searchWord.append("((").append(aulast).append(")").append(" wn AU)");
            	}
            	else
            	{
              		searchWord.append(" and ((").append(aulast).append(")").append(" wn AU)");
            	}
          }

          if ((request.getParameter("issn") != null) &&
              (request.getParameter("issn").trim().length() > 0))
          {
            issn = request.getParameter("issn");
            if (searchWord.length() == 0)
            {
              searchWord.append("((").append(issn).append(")").append(" wn SN)");
          	}
            else
            {
              searchWord.append(" and ((").append(issn).append(")").append(" wn SN)");
            }
          }

          if((request.getParameter("coden") != null)
               && (request.getParameter("coden").trim().length() > 0))
             {
            	coden = request.getParameter("coden");
            	if (searchWord.length() == 0)
            	{
              		searchWord.append("((").append(coden).append(")").append(" wn CN)");
            	}
            	else
            	{
              		searchWord.append(" and ((").append(coden).append(")").append(" wn CN)");
            	}
          	}

          if((request.getParameter("isbn") != null)
          		&& (request.getParameter("isbn").trim().length() > 0))
          	{
            	isbn = request.getParameter("isbn");
            	if (searchWord.length() == 0)
            	{
              		searchWord.append("((").append(isbn).append(")").append(" wn BN)");
            	}
            	else
            	{
              		searchWord.append(" and ((").append(isbn).append(")").append(" wn BN)");
            	}
          	}

          if ((request.getParameter("title") != null)
          		&& (request.getParameter("title").trim().length() > 0))
          	 {
            	title = request.getParameter("title");
            	if (searchWord.length() == 0)
            	{
           	   		searchWord.append("((").append(title).append(")").append(" wn ST)");
            	}
            	else
            	{
              		searchWord.append(" and ((").append(title).append(") wn ST)");
            	}
          	}

          if ((request.getParameter("stitle") != null)
          	  && (request.getParameter("stitle").trim().length() > 0))
         	{
            	stitle = request.getParameter("stitle");
            	if (searchWord.length() == 0)
            	{
              		searchWord.append("((").append(stitle).append(")").append(" wn ST)");
            	}
            	else
            	{
              		searchWord.append(" and ((").append(stitle).append(") wn ST)");
            	}
          	}

			if ((request.getParameter("atitle") != null)
			&& (request.getParameter("atitle").trim().length() > 0))
			{
				atitle = request.getParameter("atitle");
				if (searchWord.length() == 0)
				{
					searchWord.append("((").append(atitle).append(")").append(" wn TI)");
				}
				else
				{
					searchWord.append(" and ((").append(atitle).append(") wn TI)");
				}
			}

          if ((request.getParameter("issue") != null)
               && (request.getParameter("issue").trim().length() > 0))
          {
            	issue = request.getParameter("issue");
            	if (searchWord.length() == 0)
            	{
              		searchWord.append("((").append(issue).append(")").append(" wn SU)");
            	}
            	else
            	{
              		searchWord.append(" and ((").append(issue).append(") wn SU)");
            	}
          }

          if ((request.getParameter("spage") != null)
          		&& (request.getParameter("spage").trim().length() > 0))
          	{
            	spage = request.getParameter("spage");
            	if (searchWord.length() == 0)
            	{
              		searchWord.append("((").append(spage).append(")").append(" wn SP)");
            	}
            	else
            	{
              		searchWord.append(" and ((").append(spage).append(") wn SP)");
            	}
          }

          if ((request.getParameter("volume") != null)
          	  && (request.getParameter("volume").trim().length() > 0))
          	  {
            	volume = request.getParameter("volume");
            	if (searchWord.length() == 0)
            	{
              		searchWord.append("((").append(volume).append(")").append(" wn VO)");
            	}
            	else
            	{
              		searchWord.append(" and ((").append(volume).append(") wn VO)");
            	}
          }

          /** get database ID
           *  1 Compendex
           *  2 INSPEC
           *  4 NTIS
           *  3 Combined Compendex & INSPEC
           *  5 Combined Compendex & NTIS
           *  6 Combined INSPEC & NTIS
           *  7 Combined Compendex & INSPEC & NTIS
           */

          if (request.getParameterValues("sid") != null)
          {
            sid = request.getParameterValues("sid");
            for(int i=0;i<sid.length;i++)
            {
				String sidString = sid[i];
				if(sidString.length()>0 && sidString.indexOf(":")>0)
				dbID = sidString.substring(sidString.indexOf(':')+1);
				dbMask = dbMask + Integer.parseInt(dbID);
			}
          }


          // if year range is not specified, get default year range
          if ((request.getParameter("date") != null)
               && (request.getParameter("date").trim().length() > 0))
          {
            date = request.getParameter("date");
          	// leave date range open to defaults
            if (searchWord.length() == 0)
            {
              searchWord.append("(").append(date).append(")").append(" wn YR");
            }
            else
            {
              searchWord.append(" and ((").append(date).append(") wn YR)");
            }
          }

          // date range is left open to defaults
          startYear = "1969";
          endYear = String.valueOf(SearchForm.ENDYEAR);
          //forward to expert search results page
          response.sendRedirect("http://"+serverName+"/controller/servlet/Controller?CID=expertSearchCitationFormat&searchtype="+Query.TYPE_EXPERT+"&database="+dbMask+"&searchWord1="+URLEncoder.encode(searchWord.toString())+"&yearselect=yearrange&startYear="+startYear+"&endYear="+endYear);
		}

    }
    catch (Exception e)
    {
       log("Error", e);
    }
  }


  private String prep(String s)
  {
      s = s.trim();
      s = perl.substitute("s/[- ]//g",s);
      if(s.indexOf("*") == -1)
      {
          s = s+"*";
      }

      return s;
  }
}
