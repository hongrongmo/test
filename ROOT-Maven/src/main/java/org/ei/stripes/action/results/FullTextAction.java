package org.ei.stripes.action.results;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;

import org.apache.log4j.Logger;
import org.ei.controller.logging.LogEntry;
import org.ei.domain.Citation;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.EIDoc;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.fulldoc.LinkInfo;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.SystemMessage;

@UrlBinding("/search/results/fulltext.url")
public class FullTextAction extends EVActionBean implements ValidationErrorHandler {
    private final static Logger log4j = Logger.getLogger(SelectedRecordsAction.class);

    @Validate(required = true)
    private String docID;

    /**
     * Default handler for the FullText action. It requires a doc ID string to be passed in. This is used to look up the full text info.
     * 
     * @return
     */
    @DefaultHandler
    @Validate
    public Resolution handle() {
        try {
            // Create a new DocID object from docID string
            DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
            DocID docidObj = new DocID(docID, databaseConfig.getDatabase(docID.substring(0, 3)));

            // Add to List to be used by Builder
            List<DocID> docIDList = new ArrayList<DocID>();
            docIDList.add(docidObj);

            // Build the EIDoc object from incoming docID
            MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
            List bList = builder.buildPage(docIDList, Citation.CITATION_FORMAT);
            if (bList == null || bList.size() == 0) {
                log4j.error("Unable to build Page, docID = " + docID);
                return SystemMessage.SYSTEM_ERROR_RESOLUTION;
            }

            // Get EIDoc object from built Page
            EIDoc eiDoc = (EIDoc) bList.get(0);
            LinkInfo linkInfo = eiDoc.buildFT();

            if (linkInfo != null) {
                //
                // Add some info to Usage logging!
                //
                LogEntry logentry = context.getLogEntry();
                Properties logproperties = logentry.getLogProperties();
                String issn = eiDoc.getISSN2();
                if (issn != null) {
                    logproperties.put("FULLTEXT", issn);
                } else {
                    logproperties.put("FULLTEXT", "Y");
                }
                logproperties.put("DOC_ID", docidObj.getDocID());
                logproperties.put("DB_NAME", Integer.toString(docidObj.getDatabase().getMask()));

                // ***************************************************************
                // Redirect to the full text link
                // ***************************************************************
                log4j.info("Response.sendRedirect2 to: '" + linkInfo.url + "'");
                // IMPORTANT!!! Don't use Stripes Redirect Resolution since it will use the default protocol
                // which means if the user has an HTTPS session these full-text links will also be HTTPS.
                // The dx.doi.org site does not work with HTTPS!!!
                HttpServletResponse response = context.getResponse();
                response.addHeader("Location", linkInfo.url);
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                // this.context.getResponse().sendRedirect(linkInfo.url);
                return null;
            } else {
                log4j.error("Unable to get FullText link info for docID = " + docID);
                return SystemMessage.SYSTEM_ERROR_RESOLUTION;
            }
        } catch (Exception e) {
            log4j.error("Unable to build FullText info, docID = " + docID, e);
            return SystemMessage.SYSTEM_ERROR_RESOLUTION;
        }

    }

    /**
     * This method gets called after validation if there are errors!
     * 
     * @param arg0
     * @return
     * @throws Exception
     */
    public Resolution handleValidationErrors(ValidationErrors errors) throws Exception {
        //
        // If there are still errors, return to the System Error page.
        // Otherwise return null to continue processing
        //
        if (errors.size() > 0) {
            return SystemMessage.SYSTEM_ERROR_RESOLUTION;
        } else {
            return null;
        }
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }
}
