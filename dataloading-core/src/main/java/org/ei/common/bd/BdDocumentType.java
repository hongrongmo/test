/*
 * Created on Jun 16, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.common.bd;
import java.util.*;
//import org.ei.data.bd.loadtime.*;
/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BdDocumentType
{
    private static final Map bdDocType = new HashMap();

    public static final String JOURNAL_ARTICLE_CODE = "JA";
    public static final String CONFERENCE_ARTICLE_CODE = "CA";
    public static final String CONFERENCE_PROCEEDING_CODE = "CP";
    public static final String MONOGRAPH_CHAPTER_CODE = "MC";
    public static final String MONOGRAPH_REVIEW_CODE = "MR";
    public static final String REPORT_CHAPTER_CODE = "RC";
    public static final String REPORT_REVIEW_CODE = "RR";
    public static final String DISSERTATION_CODE = "DS";
    public static final String UNPUBLISHED_PAPER_CODE = "UP";
    public static final String PATENT_CODE = "PA";
    public static final String ARTICLE_IN_PRESS = "IP";
    public static final String IN_PROCESS = "GI";
    public static final String BOOK_CODE = "BR";
    public static final String CHAPTER_CODE = "CH";
    public static final String STANDARD_CODE = "ST"; //added for standard code by hmo on 7/17/2017
    public static final String ERRATUM_CODE = "ER"; //added for standard code by hmo on 7/27/2017

    static
    {
      bdDocType.put("cp",CONFERENCE_ARTICLE_CODE);
      bdDocType.put("ab",JOURNAL_ARTICLE_CODE);
      bdDocType.put("ar",JOURNAL_ARTICLE_CODE);
      bdDocType.put("bk",MONOGRAPH_CHAPTER_CODE);
      bdDocType.put("br",MONOGRAPH_REVIEW_CODE);
      bdDocType.put("bz",JOURNAL_ARTICLE_CODE);
      bdDocType.put("ca",CONFERENCE_ARTICLE_CODE);
      bdDocType.put("ch",MONOGRAPH_CHAPTER_CODE);
      bdDocType.put("cr",CONFERENCE_PROCEEDING_CODE);
      bdDocType.put("di",DISSERTATION_CODE);
      bdDocType.put("ds",DISSERTATION_CODE);
      bdDocType.put("ed",JOURNAL_ARTICLE_CODE);
      //bdDocType.put("er",JOURNAL_ARTICLE_CODE);
      //change for erratum project by hmo at 7/27/2017
      bdDocType.put("er",ERRATUM_CODE);      
      bdDocType.put("ip",JOURNAL_ARTICLE_CODE);
      bdDocType.put("le",JOURNAL_ARTICLE_CODE);
      bdDocType.put("mc",MONOGRAPH_CHAPTER_CODE);
      bdDocType.put("mr",MONOGRAPH_REVIEW_CODE);
      //added for book project by hmo at 5/16/2017
      //bdDocType.put("mc",CHAPTER_CODE);
      //bdDocType.put("mr",BOOK_CODE);
      //bdDocType.put("bk",BOOK_CODE);
      bdDocType.put("no",JOURNAL_ARTICLE_CODE);
      bdDocType.put("pa",JOURNAL_ARTICLE_CODE);
      bdDocType.put("pr",JOURNAL_ARTICLE_CODE);
      bdDocType.put("re",JOURNAL_ARTICLE_CODE);
      bdDocType.put("rp",REPORT_REVIEW_CODE);
      bdDocType.put("sh",JOURNAL_ARTICLE_CODE);
      bdDocType.put("wp",JOURNAL_ARTICLE_CODE);
      bdDocType.put("ja",JOURNAL_ARTICLE_CODE);
      bdDocType.put("pa",PATENT_CODE);
      bdDocType.put("ip",ARTICLE_IN_PRESS);
      bdDocType.put("gi",IN_PROCESS);
      bdDocType.put("st",STANDARD_CODE);//added by hmo on 07/17/2017
  }

    public static String getDocType(String doctype, boolean confCode)
    {
      if(doctype != null)
      {
        String dt = doctype.toLowerCase().trim();

        if(bdDocType.containsKey(dt))
        {
        	doctype = (String) bdDocType.get(dt);
        }
        else
        {
        	System.out.println("No BdDocumentType mapping for: " + doctype);
        }

      }
      return doctype;
    }
}
