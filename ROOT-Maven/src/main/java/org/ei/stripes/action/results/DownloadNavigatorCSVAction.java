package org.ei.stripes.action.results;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.data.DataCleaner;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.FastSearchControl;
import org.ei.domain.Query;
import org.ei.domain.SearchControl;
import org.ei.domain.Searches;
import org.ei.domain.navigators.EiModifier;
import org.ei.domain.navigators.EiNavigator;
import org.ei.domain.navigators.Refinements;
import org.ei.domain.navigators.ResultNavigator;
import org.ei.domain.personalization.IEVWebUser;
import org.ei.parser.base.BooleanQuery;
import org.ei.parser.base.Field;
import org.ei.query.base.FastQueryWriter;
import org.ei.query.base.RefinementFieldGatherer;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.SystemMessage;

/**
 * This is an ActionBean class that is used to handle CSV downloads
 * of navigator data from search results pages from EV.
 * 
 * @author harovetm
 *
 */
@UrlBinding("/search/results/downloadnavigatorcsv.url")
public class DownloadNavigatorCSVAction extends EVActionBean {
	private final static Logger log4j = Logger
			.getLogger(DownloadNavigatorCSVAction.class);

	// Request parameters
	private String nav;
	private String searchid;

	/**
	 * Handler for the Charts display from navigators/facets
	 * 
	 * @return Resolution
	 */
	@DefaultHandler
	@DontValidate
	public Resolution display() {
		setRoom(ROOM.blank);

		// Ensure searchid is present
		if (GenericValidator.isBlankOrNull(searchid)) {
			log4j.error("'searchid' request parameter is required!");
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}
		
		// Default "nav" to DB nav if empty
		if (GenericValidator.isBlankOrNull(nav)) {
			nav = EiNavigator.DB;
		}

		// Get UserSession information
		UserSession usersession = context.getUserSession();
		String sessionid = usersession.getID();
		IEVWebUser user = usersession.getUser();
		String[] credentials = user.getCartridge();

		// Buffer for output
		StringBuffer buffer = new StringBuffer(1024);

		try {
			// Create a Query object and use it to create output
			Query queryObject = Searches.getSearch(searchid);
			if (queryObject != null) {
				queryObject.setSessionID(sessionid);
				queryObject.setSearchQueryWriter(new FastQueryWriter());
				queryObject.setDatabaseConfig(DatabaseConfig.getInstance());
				queryObject.setCredentials(credentials);

				SearchControl sc = new FastSearchControl();
				ResultNavigator navs = sc.getNavigator();

				buffer.append("Engineering Village Search query: ");
				buffer.append(queryObject.getIntermediateQuery());
				buffer.append("\n");

				if (queryObject.getRefinements() != null) {

					if (navs != null) {
						/*
						    JAM TURKEY CODE to parse navigators out of expert search query
						 */
						Refinements parsedrefinements = new Refinements();
						BooleanQuery bq = queryObject.getParseTree();
						RefinementFieldGatherer fg = new RefinementFieldGatherer();
						Map<Field, List<String>> mapFields = fg.gatherExactFields(bq);
						Iterator<Field> itr = mapFields.keySet().iterator();

						while (itr.hasNext()) {
							Field field = (Field) itr.next();
							String strfield = field.getValue().toLowerCase().trim();
							if (EiNavigator.ALL.equals(strfield)) {
								continue;
							}

							EiNavigator einavigator = EiNavigator.createNavigator(field);

							if (!EiNavigator.DB.equals(einavigator.getName())) {
								List<EiModifier> trmMods = new ArrayList<EiModifier>();

								// create modifiers from the list of terms and add them
								// to a navigator for this field
								List<String> trmList = (List<String>) mapFields.get(field);
								Iterator<String> itrTerms = trmList.iterator();
								while (itrTerms.hasNext()) {
									String term = (String) itrTerms.next();
									// This is messy.  Could be somewhere else?
									if (EiNavigator.AU.equals(einavigator.getName())) {
										// remove 'spacer' inserted into Author names by DataCleaner
										term = DataCleaner.restoreAuthor(term);
									}

									trmMods.add(EiModifier.parseModifier(term));
								}

								einavigator.setModifiers(trmMods);
								parsedrefinements.addRefinement(einavigator);
							}
						}

						navs.removeRefinements(parsedrefinements);
					}
				}

				EiNavigator anav = navs.getNavigatorByName(nav);

				if (anav != null) {
					buffer.append(anav.getDisplayname());
					buffer.append("\n");

					buffer.append(anav.toCSV());
					buffer.append("\n");
				}
			} else {
				buffer = new StringBuffer("Could not retrieve data!");
			}
		} catch (Exception e) {
			log4j.error("Unable to create CSV file: " + e.getMessage());
			buffer = new StringBuffer("Could not retrieve data!");
		}

		buffer.append("\n");
		buffer.append("Copyright " + Calendar.getInstance().get(Calendar.YEAR)
				+ " Elsevier Inc. All rights reserved.");
		
		// Create the file name and add it to the response header
		try {
			String strFilename = getContentDispositionFilenameTimestamp("Navigator.tab");
			context.getResponse().setHeader(
					"Content-Disposition", 
					"attachment; filename=" + strFilename + "");			
		} catch (UnsupportedEncodingException e) {
			log4j.error("Unable to create CSV filename!");
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}
		return new StreamingResolution("application/x-no-such-app", buffer.toString());
	}

	//
	//
	// GETTERS/SETTERS
	//
	//
	public String getNav() {
		return nav;
	}

	public void setNav(String nav) {
		this.nav = nav;
	}

	public String getSearchid() {
		return searchid;
	}

	public void setSearchid(String searchid) {
		this.searchid = searchid;
	}
}
