package org.ei.tags;

import java.io.Writer;

import org.ei.books.BookDocument;
import org.ei.domain.EIDoc;
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

    try
    {
      if(curdoc != null)
      {
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
          if(strissn != null)
          {
            out.write("&issn="+ strissn);
          }

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
        } // else
      }
    }
    catch (Exception e)
    {
      // we do not want this to mess up the record display if it fails
      // so continue on and just close the XML tag we opened
    }
    out.write("]]></COLLABLINKDATA>");
  }
}
