package org.ei.stripes.action.results;

import java.util.Iterator;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.data.georef.runtime.GeoRefCoordinateMap;
import org.ei.domain.Query;
import org.ei.domain.Searches;
import org.ei.domain.navigators.EiModifier;
import org.ei.domain.navigators.EiNavigator;
import org.ei.domain.navigators.NavigatorCache;
import org.ei.domain.navigators.ResultNavigator;
import org.ei.stripes.action.EVActionBean;

@UrlBinding("/search/results/geoterms.url")
public class GeoTermsAction extends EVActionBean {
	private final static Logger log4j = Logger.getLogger(GeoTermsAction.class);

	private String searchid;
	private String dbmask;

	/**
	 * Geo terms AJAX handler
	 * 
	 * @return Resolution
	 */
	@DefaultHandler
	@DontValidate
	public Resolution handle() {

		log4j.info("GEO map opened...");
		String emptyresponse = "{}";
		StringBuffer jsonresponse = new StringBuffer();
		ResultNavigator nav = null;
		String sessionid = context.getUserSession().getID();

		if (GenericValidator.isBlankOrNull(searchid)
				|| "undefined".equals(searchid)) {
			log4j.warn("No searchid found!");
			return new StreamingResolution("text", emptyresponse);
		}

		try {

			//
			// Calculate the DB mask
			//
			if (GenericValidator.isBlankOrNull(dbmask)) {
				Query queryObject = Searches.getSearch(searchid);
				if (queryObject != null) {
					dbmask = String.valueOf(queryObject.getDataBase());
				}
			}

			//
			// Get the navigator cache
			//
			NavigatorCache navcache = new NavigatorCache(sessionid);
			nav = navcache.getFromCache(searchid);

			//
			// Get the coordinates
			//
			GeoRefCoordinateMap coords = GeoRefCoordinateMap.getInstance();
			EiNavigator geo = nav.getNavigatorByName(EiNavigator.GEO);
			if (geo == null) {
				log4j.warn("No GEO terms found!");
				return new StreamingResolution("text", emptyresponse);
			}

			//
			// Build the JSON for Google Maps API!
			//
			Iterator itrmods = (geo.getModifiers()).iterator();
			jsonresponse.append("{");
			jsonresponse.append("\"placemarks\":[ ");
			long start = System.currentTimeMillis();
			for (int mindex = 0; itrmods.hasNext(); mindex++) {
				EiModifier modifier = (EiModifier) itrmods.next();
				if (modifier != null) {
				    
				    log4j.info("EiModifier object: label=" + modifier.getLabel() + 
		                ", value=" + modifier.getValue() + 
		                ", count=" + modifier.getCount());
				    
					String geoterm = modifier.getLabel();
					String geovalue = modifier.getValue();
					int geocount = modifier.getCount();

					String coordniates = coords.lookupGeoRefTermCoordinates(geoterm);
					if (coordniates != null) {
						jsonresponse.append("{");
						jsonresponse
								.append("\"search\":\"/search/results/expert.url?CID=expertSearchCitationFormat"
										+ ((dbmask != null) ? ("&database=" + dbmask)
												: "")
										+ "&RERUN="
										+ searchid
										+ "&geonav="
										+ geocount
										+ "~"
										+ geovalue
										+ "~"
										+ geoterm
										+ "&mapevent=search\",");
						jsonresponse.append("\"count\":\""
								+ modifier.getCount() + "\",");
						jsonresponse
								.append("\"name\":\"" + geoterm + "\",");
						jsonresponse.append("\"description\":\"" + geoterm
								+ " (" + modifier.getCount()
								+ " records)\",");
						jsonresponse.append(coordniates);
						jsonresponse.append("}");
						if (itrmods.hasNext()) {
							jsonresponse.append(",");
						}
					}
				} else {
                    log4j.info("EiModifier object at " + mindex + " is NULL");
				}
			}
			jsonresponse.append(" ]");
			jsonresponse.append("}");
			
			// Return it as StreamingResolution
			return new StreamingResolution("application/json", jsonresponse.toString());
			
		} catch (Throwable t) {
			log4j.error("Unable to process GEO terms!", t);
			return new StreamingResolution("text", emptyresponse);
		}
	}

	public String getSearchid() {
		return searchid;
	}

	public void setSearchid(String searchid) {
		this.searchid = searchid;
	}

	public String getDbmask() {
		return dbmask;
	}

	public void setDbmask(String dbmask) {
		this.dbmask = dbmask;
	}

	//
	//
	// GETTERS/SETTERS
	//
	//

}
