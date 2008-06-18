package org.ei.data.books.tocs;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IncludeItemLoader extends XMLLoader {
    protected static Log log = LogFactory.getLog(IncludeItemLoader.class);

    public static void main(String[] args) {
        List<IncludeItem> iss = IncludeItemLoader.getIncludeItems(new File("W:/Developers/Referex/EW/EVF0010/9780750682701/main.xml"));
        Iterator<IncludeItem> piis = iss.iterator();
        int pageIndex = 0;
        try {
            while(piis.hasNext()) {
                IncludeItem pii = piis.next();
                List<PageRange> prs = pii.getRanges();
                Iterator<PageRange> ranges =  prs.iterator();
                log.info("<" + pii.getPii() + ">");
                while(ranges.hasNext()) {
                    PageRange pr = ranges.next();
                    int len = pr.length();
                    List pglblseq = pr.sequence();
                    log.info(pr.toString() + "[" + len + "]");
                    log.info("Page label sequence: " + pglblseq);
    
                    pageIndex += len;
                    log.info("Page index:" + pageIndex); 
                    if(pglblseq.size() != len) {
                        log.error("unmatched sequence !");
                        break;
                    }
                }
            }
        }
        catch(Exception e) {
            log.error("Bad page range!",e);
        }
        log.info(pageIndex);
    }
    
    private static final StringBuffer rulesString = new StringBuffer();
    static {
        rulesString.append("<digester-rules>");
        rulesString.append("<pattern value=\"*/ce:include-item\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.IncludeItem\"/>");
        rulesString.append("<set-next-rule methodname=\"add\" paramtype=\"java.lang.Object\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:pii\" propertyname=\"pii\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:title\" propertyname=\"title\"/>");
        rulesString.append("<call-method-rule pattern=\"ce:pages\" methodname=\"addRange\" paramcount=\"2\" />");
        rulesString.append("<call-param-rule pattern=\"ce:pages/ce:first-page\" paramnumber=\"0\"/>");
        rulesString.append("<call-param-rule pattern=\"ce:pages/ce:last-page\" paramnumber=\"1\"/>");
        rulesString.append("</pattern>");
        rulesString.append("</digester-rules>");
    }
    
    public static List<IncludeItem> getIncludeItems(File issue) {
        List<IncludeItem> items = new LinkedList<IncludeItem>();
        digest(issue, rulesString.toString(), items);
        return items;
    }
    
    public static String toXML(List<IncludeItem> includeitems) {
        int pageIndex = 0;
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("<piis>");
        try {
            Iterator<IncludeItem> items = includeitems.iterator();
            while(items.hasNext()) {
                IncludeItem pii = items.next();
                List<PageRange> prs = pii.getRanges();
                Iterator<PageRange> ranges =  prs.iterator();
                while(ranges.hasNext()) {
                    PageRange pr = ranges.next();
                    int len = pr.length();
                    
                    strbuf.append("<pii>");
                    strbuf.append("<id>").append(pii.getPii()).append("</id>");
                    strbuf.append("<page>").append(pageIndex + 1).append("</page>");
                    strbuf.append("</pii>");
                    
                    pageIndex += len;
                }
            }
        }
        catch(Exception e) {
            log.error("Bad page range!",e);
        }
        strbuf.append("</piis>");
        return (strbuf.toString());
    }
}
