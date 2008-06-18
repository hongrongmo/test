package org.ei.data.books.tocs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ArticleLoader extends XMLLoader {
    protected static Log log = LogFactory.getLog(ArticleLoader.class);

    public static void main(String[] args) {
        IncludeItem article = ArticleLoader.getIncludeItem(new File("W:/Developers/Referex/EW/EVFB010A/09275193/0012000C/07120234/main.xml"));
        log.info(article.toString());
    }
    
    private static final StringBuffer rulesString = new StringBuffer();
    static {
        rulesString.append("<digester-rules>");
        rulesString.append("<pattern value=\"article\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.IncludeItem\"/>");
        rulesString.append("<set-next-rule methodname=\"add\" paramtype=\"java.lang.Object\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"head/ce:title\" propertyname=\"title\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"item-info/ce:pii\" propertyname=\"pii\"/>");
        rulesString.append("</pattern>");
        rulesString.append("<pattern value=\"simple-article\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.IncludeItem\"/>");
        rulesString.append("<set-next-rule methodname=\"add\" paramtype=\"java.lang.Object\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"simple-head/ce:title\" propertyname=\"title\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"item-info/ce:pii\" propertyname=\"pii\"/>");
        rulesString.append("</pattern>");
        rulesString.append("<pattern value=\"converted-article\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.IncludeItem\"/>");
        rulesString.append("<set-next-rule methodname=\"add\" paramtype=\"java.lang.Object\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"head/ce:title\" propertyname=\"title\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"item-info/ce:pii\" propertyname=\"pii\"/>");
        rulesString.append("</pattern>");
        rulesString.append("</digester-rules>");
    }
    
    public static IncludeItem getIncludeItem(File xmlfile) {
        List items = new LinkedList();
        IncludeItem anarticle = null;
        digest(xmlfile, rulesString.toString(), items);
       
        if(!items.isEmpty()) {
          anarticle = (IncludeItem) items.iterator().next();
        }        
        return anarticle;
    }
    
}
