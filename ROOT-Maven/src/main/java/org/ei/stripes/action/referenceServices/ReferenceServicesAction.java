package org.ei.stripes.action.referenceServices;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.security.AskAnExpertAccessControl;
import org.ei.biz.security.IAccessControl;
import org.ei.config.EVProperties;
import org.ei.config.JSPPathProperties;
import org.ei.exception.ErrorXml;
import org.ei.exception.InfrastructureException;
import org.ei.session.UserPreferences;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.adapter.GenericAdapter;
import org.ei.stripes.adapter.IBizBean;
import org.ei.stripes.adapter.IBizXmlAdapter;
import org.ei.stripes.view.Discipline;

@UrlBinding("/askanexpert/{$event}.url")
public class ReferenceServicesAction extends EVActionBean implements IBizBean {

	private final static Logger log4j = Logger
			.getLogger(ReferenceServicesAction.class);
	private Map<String, Discipline> disciplines = new HashMap<String, Discipline>();
    private String librarianEmail;
    private boolean mailto;
    private boolean javaScriptLink;

	public boolean isJavaScriptLink() {
		return javaScriptLink;
	}

	public void setJavaScriptLink(boolean javaScriptLink) {
		this.javaScriptLink = javaScriptLink;
	}

	@Override
	public String getXSLPath() {
		return "/transform/results/ReferenceServices.xsl";
	}

	@Override
	public String getXMLPath() {
		return EVProperties.getJSPPath(JSPPathProperties.REFERENCE_SERVICES_PATH);
	}

	/**
	 * Override for the ISecuredAction interface. This ActionBean requires
	 * ReferenceServices feature to be enabled in addition to regular
	 * authentication
	 */
	@Override
	public IAccessControl getAccessControl() {
		return new AskAnExpertAccessControl();
	}

	/**
	 * Process the Reference Services (Ask an expert) XML
	 * @throws InfrastructureException
	 */
	@Override
	public void processModelXml(InputStream instream) throws InfrastructureException  {
		IBizXmlAdapter adapter = new GenericAdapter();
		String stylesheet;
			stylesheet = this.getClass()
					.getResource("/transform/results/ReferenceServices.xsl")
					.toExternalForm();
			adapter.processXml(this, instream, stylesheet);


	}

	/**
	 * Default handler - displays the ask an expert page
	 *
	 * @return Resolution
	 */
	@HandlesEvent("display")
	@DontValidate
	public Resolution display() {
		setRoom(ROOM.blank);

		// Set the librarian email appropriately
		String refemail = context.getUserSession().getUserTextZones().get(UserPreferences.TZ_REFERENCE_SERVICES_LINK);
		if (GenericValidator.isBlankOrNull(refemail) || GenericValidator.isEmail(refemail)) {
		    // CASE 1: TextZone is empty - default to the popup form
		    this.librarianEmail = "/askanexpert/email/display.url?section=Ask%20a%20Librarian&sectionid=three";
		} else if (refemail.contains("mailto:")) {
		    this.librarianEmail = refemail;
		    this.mailto = true;
		} else if (refemail.contains("javascript:")) {
			javaScriptLink= true;
			this.librarianEmail = refemail;
		}else if(refemail.contains("http")){
			this.librarianEmail = refemail;
		}

		return new ForwardResolution(
				"/WEB-INF/pages/customer/askanexpert/referenceservices.jsp");
	}

	public void addDiscipline(String disName, Discipline dis) {
		getDisciplines().put(disName, dis);
	}

	public Map<String, Discipline> getDisciplines() {
		return disciplines;
	}

	public void setDisciplines(Map<String, Discipline> disciplines) {
		this.disciplines = disciplines;
	}

	public String getLibrarianEmail() {
		return librarianEmail;
	}

	public void setLibrarianEmail(String librarianEmail) {
		this.librarianEmail = librarianEmail;
	}

    public boolean isMailto() {
        return mailto;
    }

    public void setMailto(boolean mailto) {
        this.mailto = mailto;
    }

    /**
     * Default handling for exceptions from data service layer
     * @param errorXml
     * @return
     */
	public Resolution handleException(ErrorXml errorXml) {
		context.getRequest().setAttribute("errorXml", errorXml);
		return new ForwardResolution("/system/error.url");
	}

}
