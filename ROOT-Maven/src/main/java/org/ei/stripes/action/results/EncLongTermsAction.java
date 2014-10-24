package org.ei.stripes.action.results;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.EIDoc;
import org.ei.domain.LinkedTermDetail;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.domain.Query;
import org.ei.domain.Searches;
import org.ei.exception.EVBaseException;
import org.ei.parser.base.BooleanQuery;
import org.ei.query.base.FastQueryWriter;
import org.ei.query.base.HitHighlightFinisher;
import org.ei.query.base.HitHighlighter;
import org.ei.session.UserSession;

@UrlBinding("/search/doc/enclongterms.url")
public class EncLongTermsAction extends AbstractSearchResultsAction {

    private String istag;
    private String timestamp;
    
    /**
     * Ajax-based event to handle EncompassPat results that need 
     * special encoding.  To test, search EncompassPat for accession
     * number '8811835'.  Go to details page and investigate the 
     * 'Linked terms' section
     * 
     * @return 
     * @throws EVBaseException 
     */
    @DefaultHandler
    public Resolution handle() throws EVBaseException {
        
        UserSession ussession=context.getUserSession();
        IEVWebUser user = ussession.getUser();
        boolean isTag  = "tag".equals(this.istag);
        Query queryObject = null;
        BooleanQuery bQuery = null;
        HitHighlighter highlighter = null;
        String terms = null;

        // If this is NOT a tag-based item, build Query object from search ID
        if (!isTag && !GenericValidator.isBlankOrNull(this.searchid)) {
            queryObject = Searches.getSearch(this.searchid);
            queryObject.setSearchQueryWriter(new FastQueryWriter());
            queryObject.setDatabaseConfig(DatabaseConfig.getInstance());
            queryObject.setCredentials(user.getCartridge());
            bQuery = queryObject.getParseTree();     
            highlighter = new HitHighlighter(bQuery);
        }
        
        MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();    
        List<DocID> docIds = new ArrayList<DocID>();
        DatabaseConfig dConfig = DatabaseConfig.getInstance();
        Database databse = dConfig.getDatabase(this.docid.substring(0, 3));
        
        DocID did = new DocID(this.docid, databse);
        docIds.add(did); 
        List<EIDoc> listOfDocIDs = builder.buildPage(docIds,LinkedTermDetail.LINKEDTERM_FORMAT);

        if(listOfDocIDs != null && listOfDocIDs.size() > 0) {
            EIDoc eiDoc = (EIDoc) listOfDocIDs.get(0);
            if(!isTag && !GenericValidator.isBlankOrNull(this.searchid)) {
                eiDoc = (EIDoc)highlighter.highlight(eiDoc);
            }
            
            terms = (String) eiDoc.getLongTerms();
            
            if(!isTag && !GenericValidator.isBlankOrNull(this.searchid)) {
                terms = HitHighlightFinisher.addMarkup(terms);
            }
        }

        if (GenericValidator.isBlankOrNull(terms)) {
            terms = "SAMPLE;SAMPLE|SAMPLE;SAMPLE";
        }
        
        return new StreamingResolution("text/text", terms);
    }

    public String getIstag() {
        return istag;
    }

    public void setIstag(String istag) {
        this.istag = istag;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
