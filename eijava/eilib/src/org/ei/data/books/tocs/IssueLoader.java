package org.ei.data.books.tocs;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IssueLoader extends XMLLoader {
    protected static Log log = LogFactory.getLog(IssueLoader.class);
    
    public static void main(String[] args) {
        //Issue iss = IssueLoader.getIssue(new File("W:/Developers/Referex/EW/EVIBS02C/14701804/0007000C/issue.xml"));
        Issue iss = IssueLoader.getIssue(new File("W:/Developers/Referex/EW/EVF0010/9780750682701/main.xml"));
        if(iss != null)
        {
          log.info(iss.toString());
        }
        else {
          log.error("parse result is null!");
        }
    }
    
    private static final StringBuffer rulesString = new StringBuffer();
    static {
        rulesString.append("<digester-rules>");
        rulesString.append("<pattern value=\"serial-issue\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.Issue\" />");
        rulesString.append("<bean-property-setter-rule pattern=\"issue-info/ce:issn\" propertyname=\"issn\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"issue-info/ce:isbn\" propertyname=\"isbn\"/>");
        rulesString.append("</pattern>");
        rulesString.append("<pattern value=\"book\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.Issue\" />");
        rulesString.append("<bean-property-setter-rule pattern=\"info/ce:issn\" propertyname=\"issn\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"info/ce:isbn\" propertyname=\"isbn\"/>");
        rulesString.append("</pattern>");
        rulesString.append("<pattern value=\"*/ce:include-item\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.IncludeItem\" />");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:pii\" propertyname=\"pii\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:title\" propertyname=\"title\"/>");
        rulesString.append("<set-next-rule methodname=\"addIncludeItem\"/>");
        rulesString.append("<pattern value=\"ce:pages\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.PageRange\" />");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:first-page\" propertyname=\"start\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:last-page\" propertyname=\"end\"/>");
        rulesString.append("<set-next-rule methodname=\"addPageRange\"/>");
        rulesString.append("</pattern>");
        rulesString.append("</pattern>");

        rulesString.append("</digester-rules>");
    }

    public static Issue getIssue(File issue) {
      Issue anissue = (Issue) digest(issue, rulesString.toString());
      return anissue;
  }
}
