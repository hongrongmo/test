package org.ei.stripes.action.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.controller.logging.LogEntry;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.LookupIndex;
import org.ei.domain.LookupIndexException;
import org.ei.domain.LookupIndexes;
import org.ei.domain.lookup.LookUpParameters;
import org.ei.domain.personalization.IEVWebUser;
import org.ei.gui.ListBoxOption;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVActionBean;
import org.ei.util.GUID;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

@UrlBinding("/search/browseindex.url")
public class BrowseIndexAction extends EVActionBean {
    private final static Logger log4j = Logger.getLogger(BrowseIndexAction.class);

    @Validate(trim=true,mask="Quick|Expert")
    private String searchtype;
    @Validate(mask=".*")
    private String searchword = "A";
    @Validate(trim=true, mask="AUS|AF|CVS|LA|ST|DT|PN|TR|PC|DI|PID")
    private String lookup;
    @Validate(mask="\\d*")
    private int count = 1;
    @Validate(mask=".*")
    private String lookupSearchID;

    private List<ListBoxOption> selectedIndex = new ArrayList<ListBoxOption>();
    private List<LookupIndex> lookupIndexList;
    private boolean dynamic;

    @DefaultHandler
    public Resolution display() {

        UserSession ussession = (UserSession) context.getUserSession();
        IEVWebUser user = ussession.getUser();

        String sessionId = ussession.getSessionID().toString();
        String sesID = ussession.getID();

        int databaseMask = -1;
        databaseMask = Integer.parseInt(super.getDatabase());

        if (GenericValidator.isBlankOrNull(this.lookupSearchID)) {
            try {
                lookupSearchID = new GUID().toString();
            } catch (Exception e) {
                log4j.error("Unable to use LookupIndex XML!", e);
                context.getResponse().setStatus(500);
                return new StreamingResolution("text/html", "Unable to process this request!");
            }
        }
        if (GenericValidator.isBlankOrNull(this.searchword)) {
            this.searchword = "A";
        }

        DatabaseConfig dconfig = DatabaseConfig.getInstance();
        LookupIndexes lookupIndexes = new LookupIndexes(ussession.getID(), 100, dconfig);

        //
        // Add some info to Usage logging!
        //
        LogEntry logentry = context.getLogEntry();
        Properties logproperties = logentry.getLogProperties();
        logproperties.put("context", "search");
        logproperties.put("action", "lookup");
        logproperties.put("index", Integer.toString(count));
        logproperties.put("selected", lookup);
        logproperties.put("type", searchtype);
        logproperties.put("query_string", searchword);
        logproperties.put("db_name", new Integer(databaseMask).toString());

        // Too much legacy XML logic to convert to bean.  Just parse the
        // generated XML
        Writer xmlout = new StringWriter();
        try {
            xmlout.write("<PAGE>");
            xmlout.write("<SESSION-ID>" + sessionId + "</SESSION-ID>");
            xmlout.write("<SEARCHWORD>" + searchword + "</SEARCHWORD>");
            xmlout.write("<LOOKUP-SEARCHID>" + lookupSearchID + "</LOOKUP-SEARCHID>");
            xmlout.write("<SELECTED-LOOKUP>" + lookup + "</SELECTED-LOOKUP>");

            xmlout.write("<DATABASE>" + databaseMask + "</DATABASE>");
            xmlout.write("<SEARCH-TYPE>" + searchtype + "</SEARCH-TYPE>");
            xmlout.write("<PREV-PAGE-COUNT>" + (count - 1) + "</PREV-PAGE-COUNT>");
            xmlout.write("<CURR-PAGE-COUNT>" + count + "</CURR-PAGE-COUNT>");
            xmlout.write("<NEXT-PAGE-COUNT>" + (count + 1) + "</NEXT-PAGE-COUNT>");

            int upgradeMask = dconfig.doUpgrade(databaseMask, user.getCartridge());

            lookupIndexes.getXML(count,
                lookupSearchID,
                searchword,
                lookup,
                upgradeMask,
                xmlout);
            xmlout.write("</PAGE>");

            // Convert XML to list of LookupIndex objects!
            this.lookupIndexList = lookupIndexes.buildLookupIndexList(xmlout);
            this.dynamic = lookupIndexes.isDynamic(xmlout);

            // Build selected index option list
            String xmllookupparms = LookUpParameters.lookupParametersToXML(sesID, database, this.searchtype);
            SAXBuilder builder = new SAXBuilder(false);
            Document lidoc = builder.build(new ByteArrayInputStream(xmllookupparms.getBytes()));
            Element fields = lidoc.getRootElement();
            List<Element> fieldlist = fields.getChildren("FIELD");
            for (Element field : fieldlist) {
                ListBoxOption option = new ListBoxOption("", field.getAttributeValue("SHORTNAME"), field.getAttributeValue("DISPLAYNAME"));
                selectedIndex.add(option);
            }

        } catch (LookupIndexException e) {
            log4j.error("Unable to use LookupIndex XML!", e);
            context.getResponse().setStatus(500);
            return new StreamingResolution("text/html", "Unable to process this request!");
        } catch (IOException e) {
            log4j.error("IO exception!", e);
            context.getResponse().setStatus(500);
            return new StreamingResolution("text/html", "Unable to process this request!");
        } catch (JDOMException e) {
            log4j.error("JDOM exception!", e);
            context.getResponse().setStatus(500);
            return new StreamingResolution("text/html", "Unable to process this request!");
        }

        return new ForwardResolution("/WEB-INF/pages/customer/search/browseindex.jsp");
    }

    private List<String> sequence = new ArrayList<String>();
    public List<String> getSequence() {
        if (sequence.isEmpty()) {
            if (GenericValidator.isBlankOrNull(this.searchword)) {
                this.searchword = "A";
            }
            char firstChar = searchword.charAt(0);
            char[] chararray = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
            if (!Character.isDigit(firstChar))
            {
                for (int charsize = 0; charsize < chararray.length; charsize++) {
                    sequence.add(Character.toString(Character.toUpperCase(firstChar)) + chararray[charsize]);
                }
            } else {
                for (int charsize = 0; charsize < 10; charsize++) {
                    sequence.add(Integer.toString(charsize));
                }
            }
        }
        return sequence;
    }

    //
    // GETTERS/SETTERS
    //

    public String getSearchword() {
        return this.searchword;
    }

    public void setSearchword(String searchword) {
        this.searchword = searchword;
    }

    public String getLookup() {
        return this.lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSearchtype() {
        return this.searchtype;
    }

    public void setSearchtype(String searchtype) {
        this.searchtype = searchtype;
    }

    public String getLookupSearchID() {
        return this.lookupSearchID;
    }

    public void setLookupSearchID(String lookupSearchID) {
        this.lookupSearchID = lookupSearchID;
    }

    public List<LookupIndex> getLookupIndexList() {
        return this.lookupIndexList;
    }

    public boolean isDynamic() {
        return this.dynamic;
    }

    public List<ListBoxOption> getSelectedIndex() {
        return selectedIndex;
    }

}
