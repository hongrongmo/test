package org.ei.stripes.action.results;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.config.JSPPathProperties;
import org.ei.domain.SelectOption;
import org.ei.exception.EVBaseException;
import org.ei.exception.InfrastructureException;
import org.ei.stripes.adapter.GenericAdapter;
import org.ei.stripes.adapter.IBizBean;
import org.ei.stripes.adapter.IBizXmlAdapter;

@UrlBinding("/search/results/dedupForm.url")
public class DedupResultsAction extends SearchResultsAction implements IBizBean {

	private final static Logger log4j = Logger
			.getLogger(DedupResultsAction.class);

	private int count;
	private int fieldpref;
	private List<SelectOption> databaseprefs;

	/**
	 * Return the XML adapter for quick search results display! (executed from
	 * org.ei.stripes.AuthInterceptor)
	 * @throws InfrastructureException 
	 */
	public void processModelXml(InputStream instream) throws InfrastructureException {
		IBizXmlAdapter adapter = new GenericAdapter();
		adapter.processXml(this, instream, getXSLPath());
		
	}

	public String getXSLPath() {
		return this.getClass().getResource("/transform/results/RemoveDuplicates.xsl").toExternalForm();
	}

	public String getXMLPath() {
			return EVProperties.getJSPPath(JSPPathProperties.DEDUP_FORM_PATH);
	}
	/**
	 * Display the remove duplicates form
	 * 
	 * @return Resolution
	 */
	@DefaultHandler
	@DontValidate
	public Resolution display() {
		setRoom(ROOM.search);

		return new ForwardResolution(
				"/WEB-INF/pages/customer/results/removeduplicates.jsp");
	}

	//
	//
	// GETTERS/SETTERS
	//
	//

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getFieldpref() {
		return fieldpref;
	}

	public void setFieldpref(int fieldpref) {
		this.fieldpref = fieldpref;
	}

	public List<SelectOption> getDatabaseprefs() {
		return databaseprefs;
	}

	public void setDatabaseprefs(List<SelectOption> databaseprefs) {
		this.databaseprefs = databaseprefs;
	}

	public void addDatabasepref(SelectOption option) {
		if (this.databaseprefs == null)
			this.databaseprefs = new ArrayList<SelectOption>();
		this.databaseprefs.add(option);
	}

}
