package org.ei.tags;

import java.io.*;
import java.util.*;

import org.ei.domain.EIDoc;
import org.ei.books.BookDocument;
import org.ei.util.StringUtil;
import org.ei.domain.Keys;


/*
url= * The URL for the document.
title= * The title for the URL.
source= Source title that the document was published in.
type= Reference type for the document
abs= Abstract of the document; stored in 2collab as the description if no other description is provided.

**** date= Date of the publication; stored in 2collab as the Publication Date
**** available= Date made available; stored in 2collab as the Publication Date.
**** created= Date the document was created; stored in 2collab as the Publication Date.
**** dateCopyrighted= Date the document was copyrighted; stored in 2collab as the Publication Date.
**** dateSubmitted= Date the document was submitted for publication; stored in 2collab as the Publication Date.
**** issued= Date the document was issued; stored in 2collab as the Publication Date.

description= The description of the document.
issn= The International Standard Serial Number (ISSN) for the document.
isbn= The International Standard Book Number (ISBN) for the document.
articletitle= The title of the article; not necessarily the same as the title for the URL.
volume= Volume, Issue, and pages of information for the document.
tags= Tags that the article should be tagged with; could be author keywords, index terms, or other similar words associated with the article.
pmid= The pubmed ID for the document.
pii= The PII for the document.
doi= Digital Object Identifier (DOI) for the document.

* Required Fields
**** Note If multiple dates are provided, the first one encountered will be used. The order checked is the same as the above list.
*/
public class Add2CollabLinkData
{
  private String puserID;
  private String customerID;
  private EIDoc curdoc;
  private Tag[] tags;
  private TagGroupBroker groupBroker = null;

  public Add2CollabLinkData(String puserID, String customerID, EIDoc curdoc)
    throws Exception
  {
    this.puserID = puserID;
    this.customerID = customerID;
    this.curdoc = curdoc;

/*
    if((puserID != null) && (puserID.trim().length() != 0))
    {
      groupBroker = new TagGroupBroker();
    }

    TagBroker tagBroker = new TagBroker(groupBroker);
    tags = tagBroker.getTags(docID.getDocID(),
                   puserID,
                   customerID,
                   new ScopeComp());
*/
  }

  public void toXML(Writer out)
    throws Exception
  {

    // title and url parameters are set in XSL TagBubble.xsl file on the 2collab link
    // these are the additional fields
    out.write("<COLLABLINKDATA><![CDATA[");

    // if we supply a DOI, all other fields are filled in automatically ?!
    String strdoi = curdoc.getDOI();
    if(strdoi != null)
    {
      out.write("&doi="+ strdoi);
    }
    else
    {
      String strstitle = curdoc.getMapDataElement(Keys.SERIAL_TITLE);
      if(strstitle != null)
      {
        out.write("&source="+ strstitle);
      }
      String stratitle = curdoc.getMapDataElement(Keys.TITLE);
      if(stratitle != null)
      {
        out.write("&articletitle="+ stratitle);
      }
      String strissn = curdoc.getMapDataElement(Keys.ISSN);
      {
        out.write("&issn="+ strissn);
      }

      /*
      String strty= curdoc.getType();
      if(strty != null)
      {
        out.write("&type="+ replaceTYwith2CollabString(strty));
      }*/
      String strabs = curdoc.getMapDataElement(Keys.ABSTRACT);
      if(strabs != null)
      {
        out.write("&description="+ ((strabs.length() > 250) ? strabs.substring(0,249).concat("...") : strabs));
      }
      String stryr = curdoc.getYear();
      if(stryr != null)
      {
        out.write("&date="+ stryr);
      }

      String strvo = curdoc.getVolumeNo();
      if(strvo != null)
      {
        out.write("&volume="+ strvo);
        String striss = curdoc.getIssueNo();
        if(striss != null)
        {
          out.write(" "+ striss);
        }
        String strpg = curdoc.getStartPage();
        if(strpg != null)
        {
          out.write(" "+ strpg);
        }
      }
      if(curdoc.isBook())
      {
        String strpii = ((BookDocument) curdoc).getChapterPii();
        if(strpii != null)
        {
          out.write("&pii="+ strpii);
          out.write("&type=Book Chapter");
        }
        else
        {
          out.write("&type=Book");
        }
        String strpage = String.valueOf(((BookDocument) curdoc).getPageNum());
        if(strpage != null)
        {
          out.write("&volume="+ strpage);
        }
        String strctitle = curdoc.getMapDataElement(Keys.BOOK_CHAPTER_TITLE);
        if(strctitle != null)
        {
          out.write("&articletitle="+ strctitle);
        }
        String strbyr = curdoc.getMapDataElement(Keys.BOOK_YEAR);
        if(strbyr != null)
        {
          out.write("&date="+ strbyr);
        }
        String strisbn = ((BookDocument) curdoc).getISBN13();
        if(strisbn != null)
        {
          out.write("&isbn="+ strisbn);
        }
      }
    }

    out.write("]]></COLLABLINKDATA>");
  }


    public  String  replaceTYwith2CollabString(String str)
    {
        if(str==null || str.equals("QQ"))
        {
          str=StringUtil.EMPTY_STRING;
        }
/*
          <option value="Journal Article">Journal Article</option>
          <option value="Patent">Patent</option>

          <option value="Pre-print">Pre-print</option>
          <option value="Technical Report">Technical Report</option>
          <option value="Thesis">Thesis</option>
          <option value="Dissertation">Dissertation</option>
          <option value="Journal Homepage">Journal Homepage</option>
          <option value="E-print">E-print</option>

          <option value="Book">Book</option>
          <option value="Book Chapter">Book Chapter</option>
          <option value="Conference Paper">Conference Paper</option>
          <option value="Article In Press">Article In Press</option>
          <option value="Web Document">Web Document</option>
          <option value="Presentation">Presentation</option>

          <option value="Course Material">Course Material</option>
          <option value="Statistical/Experimental Data">Statistical/Experimental Data</option>
          <option value="Generic">Generic</option></select>
*/
      if(!str.equals(StringUtil.EMPTY_STRING))
      {
        if (str.equals("JA")){str = "Journal Article";}
        else if (str.equals("CA")){str = "Conference Paper";}
        else if (str.equals("CP")){str = "Conference Paper";}
        else if (str.equals("MC")){str = "Book Chapter";}
        else if (str.equals("MR")){str = "Book";}
        else if (str.equals("RC")){str = "Technical Report";}
        else if (str.equals("RR")){str = "Technical Report";}
        else if (str.equals("DS")){str = "Thesis";}
        else if (str.equals("UP")){str = "";}
        else if (str.equals("MP")){str = "";}
        else
        {
          str=StringUtil.EMPTY_STRING;
        }
      }

      return str;
    }
}
