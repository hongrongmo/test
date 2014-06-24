package org.ei.stripes.view;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.ei.domain.personalization.GlobalLinks;
import org.ei.domain.personalization.IEVWebUser;
import org.ei.session.UserSession;
import org.ei.stripes.EVActionBeanContext;
import org.w3c.dom.Document;

public class GlobalLinksDisplay {
	private final static Logger log4j = Logger.getLogger(GlobalLinksDisplay.class);

	private boolean isThesaurus = false;
    private boolean isBook = false;
    private boolean isReference = true;
    private boolean isTag = true;
    private boolean isBulletin = false;
    private String message;
    
	/**
	 *  Default constructor!
	 */
	public GlobalLinksDisplay() { };

	/**
	 * Create a new GlobalLinksDisplay from the current request
	 * @param context
	 */
	public GlobalLinksDisplay(EVActionBeanContext context) {
    	// Get the user session object
        UserSession ussession = context.getUserSession();
    	if (ussession == null) {
			log4j.info("No user session object, Global Links NOT set");
			return;
    	}

    	// Get the user object
        IEVWebUser user = ussession.getUser();
    	if (user == null || user.getCartridge() == null) {
			log4j.info("No user object, Global Links NOT set");
			return;
    	}

    	// Now get the global links XML
    	String globallinksXML = GlobalLinks.toXML(user.getCartridge());
	    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true); // never forget this!
	    DocumentBuilder builder;
		try {
			builder = domFactory.newDocumentBuilder();
		    Document doc = builder.parse(new ByteArrayInputStream(globallinksXML.getBytes("UTF-8")));
		    parse(doc);
		} catch (Exception e) {
			log4j.warn("Unable to get global links (via context object)");
		}
	}

	/**
	 * Create a new GLobalLinksDisplay from Document (XML) object
	 * @param modelXML
	 */
	public GlobalLinksDisplay(Document doc) {
		if (doc == null) {
			log4j.warn("Unable to get global links - document object null!");
		} else {
			parse(doc);
		}
	}

	/**
	 * Internal method to parse document object for GLOBAL-LINKS settings
	 * @param doc The Document object
	 */
	private void parse(Document doc) {
		try {
		    XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();

		    this.setThesaurus((Boolean)xpath.evaluate("//GLOBAL-LINKS/THESAURUS",doc, XPathConstants.BOOLEAN));
		    this.setBooks((Boolean)xpath.evaluate("//GLOBAL-LINKS/BOOK",doc, XPathConstants.BOOLEAN));
		    this.setTag((Boolean)xpath.evaluate("//GLOBAL-LINKS/TAGGROUPS",doc, XPathConstants.BOOLEAN));
		    this.setBulletin((Boolean)xpath.evaluate("//GLOBAL-LINKS/BULLETINS",doc, XPathConstants.BOOLEAN));
		    this.setReference((Boolean)xpath.evaluate("//GLOBAL-LINKS/REFERENCE",doc, XPathConstants.BOOLEAN));
		    this.setMessage((String)xpath.evaluate("//GLOBAL-LINKS/GLOBAL-MESSAGE",doc, XPathConstants.STRING));
		    
		} catch (Exception e) {
			log4j.warn("Unable to get global links (initXml())");
		}
		
	}


    //
    // GETTERS/SETTERS
    //
    
	public boolean isThesaurus() {
		return isThesaurus;
	}

	public void setThesaurus(boolean isThesaurus) {
		this.isThesaurus = isThesaurus;
	}

	public boolean isBook() {
		return isBook;
	}

	public void setBooks(boolean isBook) {
		this.isBook = isBook;
	}

	public boolean isReference() {
		return isReference;
	}

	public void setReference(boolean isReference) {
		this.isReference = isReference;
	}

	public boolean isTag() {
		return isTag;
	}

	public void setTag(boolean isTag) {
		this.isTag = isTag;
	}

	public boolean isBulletin() {
		return isBulletin;
	}

	public void setBulletin(boolean isBulletin) {
		this.isBulletin = isBulletin;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
}
