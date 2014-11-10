package org.ei.stripes.customtags;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.EIDoc;
import org.ei.domain.LocalHolding;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.session.UserPreferences;
import org.ei.stripes.view.SearchResult;

public class LocalHoldingsTag extends SimpleTagSupport {
    private final static Logger log4j = Logger.getLogger(LocalHoldingsTag.class);
    
    private  Map<String,String> textzones;  //get text zones from user session.  
    private String snvalue;                 // Display Local holdings based on SN value.
    private String source;                  // page source to manage paragraph tag.
    private boolean status;                 // status for adding pipe symbol in front.
    private int limit = UserPreferences.TZ_LINK_LABELS.length;  // Limit of links to show
    private String docid;                   // Document ID .
    DocumentBuilder builder = new MultiDatabaseDocBuilder();
    
    int index = 1;
    
    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        JspWriter out = pageContext.getOut();
        StringBuffer locHoldKeybuff = new StringBuffer();
        
        // Ensure textzones are available
        if (textzones == null) {
            log4j.warn("Unable to retrieve user text zones!");
            return;
        }
        
        // Retreive the EIDoc object from tag parameters
        EIDoc doc = EIDoc.buildDoc(docid);
        if (doc == null) {
            log4j.warn("Unable to render tag contents:  no EIDoc!");
            return;
        }
        Database database = (doc.getDocID()).getDatabase();

        List<LocalHolding> lhlist = LocalHolding.buildFromTextZones(textzones);
        int count = 0;
        String strTmpLbl = null;
        String strTmpDefURL = null;
        String strTmpDynURL = null;
        String strTmpImgURL = null;
        String strPipe = null;
        for (LocalHolding lh : lhlist) {
            strTmpLbl = lh.getLinkLabel();
            strTmpDynURL = lh.getDynamicUrl();
            strTmpDefURL = lh.getDefaultUrl();
            strTmpImgURL = lh.getImageUrl();
            
            if (!database.linkLocalHoldings(strTmpLbl)) {
                log4j.info("Skipping local holding with link label: '" + lh.getLinkLabel() + "'");
                continue;
            }

            if (!GenericValidator.isBlankOrNull(strTmpDynURL)) {
                strTmpDynURL = doc.getLocalHoldingLink(strTmpDynURL);
            }

            if (!GenericValidator.isBlankOrNull(strTmpLbl) && !GenericValidator.isBlankOrNull(strTmpDynURL)) {
                if (status) {
                    if (count > 0) {
                        strPipe = "<span class=\"pipe\"> | </span> ";
                    } else {
                        strPipe = "";
                    }
                } else {
                    strPipe = "<span class=\"pipe\">|</span> ";
                }
                
                
                if (!GenericValidator.isBlankOrNull(strTmpDefURL)) {
                    strTmpDefURL = doc.getLocalHoldingLink(strTmpDefURL);
                }
                
                if (!GenericValidator.isBlankOrNull(snvalue)) {
                    
                    locHoldKeybuff.append(strPipe);
                    
                    if (!GenericValidator.isBlankOrNull(strTmpImgURL)) {
                        locHoldKeybuff.append(" <a class=\"LgBlueLink\" href=\"" + strTmpDynURL + "\" target=\"new\"><img src=\"" + strTmpImgURL + "\" alt=\""
                            + strTmpLbl + "\" border=\"0\"/> </a>");
                    } else {
                        locHoldKeybuff.append(" <a class=\"LgBlueLink\" href=\"" + strTmpDynURL + "\" target=\"new\">" + strTmpLbl + "</a>");
                    }
                } else if (!GenericValidator.isBlankOrNull(strTmpDefURL)) {
                    
                    locHoldKeybuff.append(strPipe);
                    
                    if (!GenericValidator.isBlankOrNull(strTmpImgURL)) {
                        locHoldKeybuff.append(" <a class=\"LgBlueLink\" href=\"" + strTmpDefURL + "\" target=\"new\"><img src=\"" + strTmpImgURL + "\" alt=\""
                            + strTmpLbl + "\" border=\"0\"> </a>");
                    } else {
                        locHoldKeybuff.append(" <a class=\"LgBlueLink\" href=\"" + strTmpDefURL + "\" target=\"new\">" + strTmpLbl + "</a>");
                    }
                }
                
                strTmpLbl = null;
                strTmpDynURL = null;
                strTmpDefURL = null;
                strTmpImgURL = null;
                strPipe = null;
                count ++;
                if (count >= limit) break;
            }
        }
        
        if (!source.equals("results") && locHoldKeybuff.length() > 0) {
            
            locHoldKeybuff.insert(0, "<p>");
            locHoldKeybuff.append("</p>");
        }
        out.write(locHoldKeybuff.toString());
    }

    //
    // GETTERS / SETTERS
    //
    
    public void setSnvalue(String snvalue) {
        this.snvalue = snvalue;
    }
    
    public void setResult(SearchResult result) {
    }
    
	public void setTextzones(Map<String, String> textzones) {
		this.textzones = textzones;
	}
	
    public void setSource(String source) {
        this.source = source;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }
    
    public void setDocid(String sDocid) {
        this.docid = sDocid;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
}
