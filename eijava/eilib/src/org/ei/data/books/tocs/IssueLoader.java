package org.ei.data.books.tocs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IssueLoader extends XMLLoader {
    protected static Log log = LogFactory.getLog(IssueLoader.class);
    
    public static void main(String[] args) {
        Issue iss = IssueLoader.getIssue(new File("w:/developers/referex/EW/EVIBS02C/14701804/0007000C/issue.xml"));
        
        if(iss != null) {
            log.info("ISBN: " + iss.getIsbn().replaceAll("-", ""));
        }
        
    }
    private static final StringBuffer rulesString = new StringBuffer();
    static {
        rulesString.append("<digester-rules>");
        rulesString.append("<pattern value=\"serial-issue/issue-info\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.Issue\"/>");
        rulesString.append("<set-next-rule methodname=\"add\" paramtype=\"java.lang.Object\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:issn\" propertyname=\"issn\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:isbn\" propertyname=\"isbn\"/>");
        rulesString.append("</pattern>");
        rulesString.append("</digester-rules>");
    }

    public static Issue getIssue(File issue) {
      List items = new LinkedList();
      Issue anissue = null;
      digest(issue, rulesString.toString(), items);
     
      if(!items.isEmpty()) {
        anissue = (Issue) items.iterator().next();
      }        
      return anissue;
  }
}
