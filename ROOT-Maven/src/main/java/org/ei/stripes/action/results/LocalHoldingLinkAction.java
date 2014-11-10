package org.ei.stripes.action.results;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.DocumentBuilderException;
import org.ei.domain.EIDoc;
import org.ei.domain.FullDoc;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.SystemMessage;


@UrlBinding("/search/results/localholdinglinks{$event}.url")
public class LocalHoldingLinkAction extends EVActionBean {
	
	private final static Logger log4j = Logger.getLogger(LocalHoldingLinkAction.class);
	
	private String docId;
	private String position;
	private String url;
		
	@DefaultHandler
	@DontValidate
	public Resolution handler() {
		
		if(!StringUtils.isNotBlank(docId) ||  !StringUtils.isNotBlank(position) ||  !StringUtils.isNotBlank(url)){
			 log4j.error("handler -> Unable produce the localholding url, required parameter is empty/blank.");
			 return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}
		
		EIDoc doc = buildDoc(docId);
		
		if(doc == null){
			log4j.error("handler -> Unable produce the localholding url, document could not be built.");
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}
		
		String redirectUrl = null;
		UserSession usersession = this.context.getUserSession();
		if(usersession != null){
			String propString = usersession.getLocalHoldingKey();
			
			if (StringUtils.isNotBlank(propString)){
				StringTokenizer st = new StringTokenizer(propString, "|", false);
				while (st.hasMoreTokens()){
		           StringTokenizer st2 = new StringTokenizer(st.nextToken(), ",", false);
	               if (st2.hasMoreTokens()){
	            	    String linkkey = st2.nextToken();
	            	    @SuppressWarnings("unused")
	            	    String slinkLabel = st2.nextToken();
	                    String sdynamicUrl = st2.nextToken();
	                    String sdefaultUrl = st2.nextToken();
	                    if(position.equalsIgnoreCase(linkkey)){
                    		if(url.equalsIgnoreCase("DYNAMIC-URL")){
                    			redirectUrl = doc.getLocalHoldingLink(sdynamicUrl);
    	                    }else if(url.equalsIgnoreCase("DEFAULT-URL")){
    	                    	redirectUrl = doc.getLocalHoldingLink(sdefaultUrl);
                    		}
                    		break;
                    	}
	                    
	                }
	            }
		     }
		}
		if(redirectUrl == null){
			log4j.error("handler -> Unable produce the localholding url, error occured.");
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}
	    return new RedirectResolution(redirectUrl);	
	}
	
	 @HandlesEvent("streamurl")
	 public Resolution streamurl() throws Exception {
		 	if(!StringUtils.isNotBlank(docId) ||  !StringUtils.isNotBlank(position) ||  !StringUtils.isNotBlank(url)){
				 log4j.error("streamurl -> Unable produce the localholding url, required parameter is empty/blank.");
				 return SystemMessage.SYSTEM_ERROR_RESOLUTION;
			}
			
			EIDoc doc = buildDoc(docId);
			
			if(doc == null){
				log4j.error("streamurl -> Unable produce the localholding url, document could not be built.");
				return SystemMessage.SYSTEM_ERROR_RESOLUTION;
			}
			
			String redirectUrl = null;
			UserSession usersession = this.context.getUserSession();
			if(usersession != null){
				String propString = usersession.getLocalHoldingKey();
				
				if (StringUtils.isNotBlank(propString)){
					StringTokenizer st = new StringTokenizer(propString, "|", false);
					while (st.hasMoreTokens()){
			           StringTokenizer st2 = new StringTokenizer(st.nextToken(), ",", false);
		               if (st2.hasMoreTokens()){
		            	    String linkkey = st2.nextToken();
		            	    @SuppressWarnings("unused")
		            	    String slinkLabel = st2.nextToken();
		                    String sdynamicUrl = st2.nextToken();
		                    String sdefaultUrl = st2.nextToken();
		                    if(position.equalsIgnoreCase(linkkey)){
	                    		if(url.equalsIgnoreCase("DYNAMIC-URL")){
	                    			redirectUrl = doc.getLocalHoldingLink(sdynamicUrl);
	    	                    }else if(url.equalsIgnoreCase("DEFAULT-URL")){
	    	                    	redirectUrl = doc.getLocalHoldingLink(sdefaultUrl);
	                    		}
	                    		break;
	                    	}
		                    
		                }
		            }
			     }
			}
			if(redirectUrl == null){
				log4j.error("streamurl -> Unable produce the localholding url, error occured.");
				return SystemMessage.SYSTEM_ERROR_RESOLUTION;
			}
			return new StreamingResolution("text/plain", redirectUrl);
	 }
	
	public EIDoc buildDoc(String docid) {
        List<?> eiDocs = null;
        EIDoc doc = null;
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        DocumentBuilder builder = new MultiDatabaseDocBuilder();

        if (!GenericValidator.isBlankOrNull(docid)) {
            List<DocID> docIds = new ArrayList<DocID>();
            DocID did = new DocID(docid, databaseConfig.getDatabase(docid.substring(0, 3)));
            docIds.add(did);
            try {
                eiDocs = builder.buildPage(docIds, FullDoc.FULLDOC_FORMAT);
                doc = (EIDoc) eiDocs.get(0);
            } catch (DocumentBuilderException e) {
                log4j.error("Unable to retrieve EIDoc with id: " + docid);
            }
        } else {
            log4j.error("Unable to retrieve EIDoc with null id");
        }

        return doc;
    }
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
}
