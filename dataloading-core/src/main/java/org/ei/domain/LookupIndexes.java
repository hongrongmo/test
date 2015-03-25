package org.ei.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.ei.domain.lookup.LookupIndexFactory;
import org.ei.domain.lookup.LookupIndexSearcher;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * This object <code>LookupIndexes </code> is a part of Factory Method framework The framework contains
 * LookupIndexFactory() class, LookupIndexSearcher interface and CPX, INSPEC, NTIS, Combined lookup classes which
 * implement LookupIndexSearcher interface. LookupIndexFactory framework encapsulates the knowledge of which
 * LookupIndexSearcher' subclass to create CPX, INSPEC, NTIS, Combined lookup classes write XML
 */
public class LookupIndexes
{
    private int count;
    private int indexesPerPage;
    private String searchfield;
    private String sessionID;
    private DatabaseConfig config = null;
    
    public LookupIndexes(String sessionID,
        int indexesPerPage,
        DatabaseConfig config)
    {
        this.indexesPerPage = indexesPerPage;
        this.sessionID = sessionID;
        this.config = config;
    }
    
    /**
     * Build LookupIndex object list from XML
     * 
     * @param xmlout
     * @return
     * @throws IOException
     * @throws JDOMException
     */
    public List<LookupIndex> buildLookupIndexList(Writer xmlout) throws JDOMException, IOException {
        List<LookupIndex> lookupIndexList = new ArrayList<LookupIndex>();
        
        // Convert XML to list of LookupIndex objects!
        SAXBuilder builder = new SAXBuilder(false);
        Document lidoc = builder.build(new ByteArrayInputStream(xmlout.toString().getBytes()));
        Element page = lidoc.getRootElement();
        Element limain = page.getChild("LOOKUP-INDEXES");
        List<Element> lilist = limain.getChildren("LOOKUP-INDEX");
        for (Element liele : lilist) {
            LookupIndex li = new LookupIndex();
            Element lookupdbs = liele.getChild("LOOKUP-DBS");
            if (lookupdbs != null && lookupdbs.getChildren().size() > 0) {
                List<Element> dblist = lookupdbs.getChildren("DB");
                for (Element dbele : dblist) {
                    li.addDatabase(dbele.getValue());
                }
            }
            li.setName(liele.getChildText("LOOKUP-NAME"));
            li.setValue(liele.getChildText("LOOKUP-VALUE"));
            
            lookupIndexList.add(li);
        }
        
        return lookupIndexList;
    }
    
    /**
     * Determine if request is dynamic or not from XML
     * 
     * @param xmlout
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public boolean isDynamic(Writer xmlout) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder(false);
        Document lidoc = builder.build(new ByteArrayInputStream(xmlout.toString().getBytes()));
        Element page = lidoc.getRootElement();
        Element limain = page.getChild("LOOKUP-INDEXES");
        return "DYNAMIC".equals(limain.getAttribute("TYPE").getValue());
    }
    
    /**
     * This method generates the xml from the current count to next 100 records.
     * 
     * @ param java.lang int @ param java.lang.String searchword @ param java.lang.String seachfield @ param
     * java.lang.string database @ return void
     */
    
    public void getXML(int pageNumber,
        String lookupSearchID,
        String searchword,
        String searchfield,
        int databaseMask,
        Writer out)
        throws LookupIndexException
    {
        String xmlData = null;
        LookupIndexSearcher lisearcher = null;
        searchword = searchword.toUpperCase(); // index in upper case
        try
        {
            LookupIndexFactory lifactory = new LookupIndexFactory(this.sessionID,
                this.indexesPerPage);
            lisearcher = lifactory.getIndexSearcher(searchfield);
            
            Database[] databases = config.getDatabases(databaseMask);
            
            lisearcher.lookupIndexXML(lookupSearchID,
                searchword,
                databases,
                pageNumber,
                out);
            
            out.write(buildSearchSequence(searchword.charAt(0)));
        } catch (Exception e)
        {
            throw new LookupIndexException(e);
        }
    }
    
    public String buildSearchSequence(char firstChar) {
        StringBuffer xmlString = new StringBuffer();
        char[] chararray = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        if (!Character.isDigit(firstChar))
        {
            xmlString.append("<ALPHABET-SEQUENCE>");
            for (int charsize = 0; charsize < chararray.length; charsize++) {
                xmlString.append("<SEQUENCE>");
                xmlString.append(Character.toUpperCase(firstChar));
                xmlString.append(chararray[charsize]);
                xmlString.append("</SEQUENCE>");
            }
            xmlString.append("</ALPHABET-SEQUENCE>");
        } else {
            xmlString.append("<ALPHABET-SEQUENCE>");
            for (int charsize = 0; charsize < 10; charsize++) {
                xmlString.append("<SEQUENCE>");
                xmlString.append(charsize);
                xmlString.append("</SEQUENCE>");
            }
            xmlString.append("</ALPHABET-SEQUENCE>");
        }
        return xmlString.toString();
    }
}