package org.ei.stripes.action.results;

import javax.servlet.ServletException;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.ei.service.CitedByService;
import org.ei.service.CitedByServiceImpl;
import org.ei.stripes.action.EVActionBean;



/**
 * @author kamaramx
 *
 */
@UrlBinding("/abstract/citedby.url")
public class CitedByAction extends EVActionBean {
	private final static Logger log4j = Logger.getLogger(CitedByAction.class);
	private String citedby;
	private String eid;
	private String doi;
	private String authordetail;

	/**
	 * Handles Ajax request for Scopus citedby info (abstract page)
	 * @return
	 * @throws ServletException
	 */
	@HandlesEvent("abstract")
	@DontValidate
	public Resolution handleabstract() throws ServletException {

		log4j.info("Servicing citedby request (abstract page)");
		JSONArray response = null;
		JSONObject authorDetails = null;
		try
		{
			CitedByService citedByService = new CitedByServiceImpl();
			if (!GenericValidator.isBlankOrNull(citedby)) {
				response = citedByService.getCitedByCount(citedby);
				// load the author detail links as a separate thread abd separate result object
				if(authordetail != null && authordetail.equalsIgnoreCase("yes")){
					authorDetails = citedByService.getAuthorDetails(citedby);
				}
			} else if(!GenericValidator.isBlankOrNull(eid)) {
				response = citedByService.getCitedByDetail(eid, doi);
			} else if(!GenericValidator.isBlankOrNull(doi)) {
				response = citedByService.getCitedByDetail(eid, doi);
			}
			JSONObject obj = new JSONObject();
			
			if(response != null && !response.isEmpty()){
				obj.put("result", response);
			}
			if(authorDetails != null && !authorDetails.isEmpty()){
				obj.put("authors", authorDetails);
			}
			
			if(!obj.isEmpty()){
				return new StreamingResolution("UTF-8", StringEscapeUtils.unescapeXml(obj.toString()));
			}else{
				log4j.error("************ Unable to create cited by related data!.***************");
				return new StreamingResolution("UTF-8", "{}");
			}
		}catch(Exception e)
		{
			log4j.error("************ Unable to create cited by data!.***************");
			return new StreamingResolution("UTF-8", "{}");
		}

	}
	
	

	//
	//
	// GETTERS/SETTERS
	//
	//

	public String getAuthordetail() {
		return authordetail;
	}
	
	public void setAuthordetail(String authordetail) {
		this.authordetail = authordetail;
	}

	public String getCitedby() {
		return citedby;
	}

	public void setCitedby(String citedby) {
		this.citedby = citedby;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}



}
