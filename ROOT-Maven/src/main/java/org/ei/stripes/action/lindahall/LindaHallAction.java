package org.ei.stripes.action.lindahall;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.Abstract;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.domain.Page;
import org.ei.domain.PageEntry;
import org.ei.domain.PageEntryBuilder;
import org.ei.session.UserPreferences;
import org.ei.stripes.action.EVActionBean;

public abstract class LindaHallAction extends EVActionBean {

    private final static Logger log4j = Logger.getLogger(LindaHallAction.class);

    public static String LHL_SESSION_USERINFO = LindaHallAction.class.getName() + "_userinfo";
    
    @Validate(required=true)
    protected String docid;
    protected String timestamp;

    @Before
    protected void init() {
        // Ensure docid and database are present
        if (docid == null || GenericValidator.isBlankOrNull(docid)) {
            throw new IllegalArgumentException("'docid' is required!");
        } else if (database == null || GenericValidator.isBlankOrNull(database)) {
            throw new IllegalArgumentException("'database' is required!");
        }
    }
    
    /**
     * Retrieve the XML for the document to be ordered
     * 
     * @return
     * @throws Exception
     */
    protected String getDocumentXML() throws Exception {
        String xmlout = "";
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        List<DocID> l = new ArrayList<DocID>();

        Database databaseObj = databaseConfig.getDatabase(database);
        DocID docID = new DocID(docid, databaseObj);
        l.add(docID);

        String sessionId = context.getUserSession().getID();
        try {
            MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
            List builtList = builder.buildPage(l, Abstract.ABSTRACT_FORMAT);

            PageEntryBuilder eBuilder = new PageEntryBuilder(sessionId);
            List<PageEntry> pList = eBuilder.buildPageEntryList(builtList);
            Page page1 = new Page();
            
            Writer writer = new StringWriter();
            page1.addAll(pList);
            if(page1 != null) {
                page1.toXML(writer);
            }
            xmlout = writer.toString();
            
        } catch (Exception e) {
            log4j.error("Unable to get document info: ", e);
            throw e;
        }
        return xmlout;
    }

    protected Resolution buildOrderFormResolution() {
        return new RedirectResolution("/lindahall/orderform.url?docid="+docid+"&database="+database+"&timestamp="+timestamp);
    }

    protected Resolution buildAuthResolution() {
        return new RedirectResolution("/lindahall/auth.url?docid="+docid+"&database="+database+"&timestamp="+timestamp);
    }

    //
    // GETTERS/SETTERS
    //
    
    public String getDocid() {
        return this.docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEditable() {
        return context.getUserSession().getUser().getPreference(UserPreferences.FENCE_LHL_EDITABLE);
    }
}
