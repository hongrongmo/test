package org.ei.domain.navigators.state;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.navigators.state.ResultNavigatorState.OPEN;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.session.UserSession;
import org.ei.stripes.view.SearchResultNavigator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML format for navigator state: <navstate> <navigator field="fieldname"
 * open="true|false" more="5|10|20|..." order="0,1,2,..."/> </navstate>
 * 
 * @author harovetm
 * 
 */
public class ResultNavigatorStateHelper {
	private final static Logger log4j = Logger.getLogger(ResultNavigatorStateHelper.class);

	private List<ResultNavigatorState> navstatelist = new ArrayList<ResultNavigatorState>();
	private UserSession usersession;

	// Special "append" navigator. Just track it's open/close state...
	private boolean appendopen = true;

	public static void main(String[] args) throws InfrastructureException {
		// Test xml string
		String xmlstate = "<navstate><navappend>false</navappend><navigator field='au' open='-1' more='0' order='0'/><navigator field='af' open='-5' more='5' order='2'/></navstate>";
		ResultNavigatorStateHelper helper = new ResultNavigatorStateHelper(null);
		helper.readFromXML(xmlstate);
		System.out.println("navappend = " + helper.isAppendopen());
	}

	/**
	 * Creates Helper from UserSession object
	 * 
	 * @throws InfrastructureException
	 */
	public ResultNavigatorStateHelper(UserSession usersession) throws InfrastructureException {
		if (usersession != null) {
			this.usersession = usersession;
			String xmlstate = this.usersession.getNavState();
			this.navstatelist = readFromXML(xmlstate);
		}
	}

	/**
	 * Creates helper with state from xmlstate variable. Expected that this
	 * variable is retrieved from User session.
	 * 
	 * @param xmlstate
	 *            Nav state as XML string
	 * @throws InfrastructureException
	 */
	public List<ResultNavigatorState> readFromXML(String xmlstate) throws InfrastructureException {

		if (!GenericValidator.isBlankOrNull(xmlstate)) {
			//
			// Build from XML string
			//
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true); // never forget this!
			DocumentBuilder dBuilder = null;
			Document doc = null;
			// try {
			try {
				dBuilder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new InfrastructureException(SystemErrorCodes.NAVIGATOR_STATE_ERROR, e);
			}
			try {
				doc = dBuilder.parse(new ByteArrayInputStream(xmlstate.getBytes()));
			} catch (SAXException e) {
				throw new InfrastructureException(SystemErrorCodes.NAVIGATOR_STATE_ERROR, e);
			} catch (IOException e) {
				throw new InfrastructureException(SystemErrorCodes.NAVIGATOR_STATE_ERROR, e);
			}
			doc.getDocumentElement().normalize();

			NodeList navigators = doc.getElementsByTagName("navigator");
			for (int i = 0; i < navigators.getLength(); i++) {
				NamedNodeMap attributes = navigators.item(i).getAttributes();
				ResultNavigatorState state = new ResultNavigatorState(attributes.getNamedItem("field").getNodeValue());
				state.setOpen(OPEN.fromVal(Integer.parseInt(attributes.getNamedItem("open").getNodeValue())));
				state.setOrder(Integer.parseInt(attributes.getNamedItem("order").getNodeValue()));
				this.navstatelist.add(state);
			}
			XPath xpath = XPathFactory.newInstance().newXPath();
			try {
				appendopen = Boolean.parseBoolean(xpath.evaluate("//navappend", doc));
			} catch (XPathExpressionException e) {
				throw new InfrastructureException(SystemErrorCodes.NAVIGATOR_STATE_ERROR, e);
			}
			/*
			 * } catch (Exception e) {
			 * log4j.error("Unable to parse nav state XML: " + e.getMessage());
			 * }
			 */
		}
		return this.navstatelist;
	}

	/**
	 * Returns the XML version of the current nav state Also updates the session
	 * (if UserSession object is present)
	 * 
	 * @return Nav state as XML
	 */
	private String writeNavState() {
		// Write current nav state to session database
		StringBuffer navstate = new StringBuffer("<navstate>");
		navstate.append("<navappend>" + appendopen + "</navappend>");
		ResultNavigatorState item;
		Collections.sort(navstatelist);
		for (int i = 0; i < navstatelist.size(); i++) {
			item = navstatelist.get(i);
			navstate.append("<navigator field='" + item.getField() + "' open='" + item.getOpen().getVal() + "' order='" + item.getOrder() + "'/>");
		}
		navstate.append("</navstate>");
		// Update the session
		if (this.usersession != null) {
			usersession.setNavState(navstate.toString());
		}
		// System.out.println(navstate.toString());
		return navstate.toString();
	}

	/**
	 * Find a nav state by field name
	 * 
	 * @param navfield
	 * @return
	 */
	ResultNavigatorState item;

	public ResultNavigatorState find(String navfield) {
		for (int i = 0; i < navstatelist.size(); i++) {
			item = navstatelist.get(i);
			if (navfield.equals(item.getField())) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Find a nav state by order number
	 * 
	 * @param navfield
	 * @return
	 */
	public ResultNavigatorState findByOrder(int order) {
		ResultNavigatorState item;
		for (int i = 0; i < navstatelist.size(); i++) {
			item = navstatelist.get(i);
			if (order == item.getOrder()) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Update the open/close state.
	 * 
	 * @param navfield
	 * @param open
	 */
	public void updateNavOpen(String navfield, int open) {
		ResultNavigatorState navstate = find(navfield);
		if (navstate == null) {
			navstate = new ResultNavigatorState(navfield);
			navstatelist.add(navstate);
		}
		navstate.setOpen(OPEN.fromVal(open));
		if (usersession != null) {
			usersession.setNavState(writeNavState());
		}
	}

	public void updateNavState(String navFields) {
		if (!navstatelist.isEmpty()) {
			navstatelist.clear();
		}
		String[] navField = navFields.split(",");

		for (int i = 0; i < navField.length; i++) {
			String[] navStates = navField[i].split("_");
			// Special case for "append" field
			if ("append".equals(navStates[0])) {
				updateNavAppend(Integer.parseInt(navStates[1]) > 0);
				continue;
			}
			ResultNavigatorState navstate = new ResultNavigatorState(navStates[0]);
			if (navStates.length > 1) {
				navstate.setOpen(OPEN.fromVal(Integer.parseInt(navStates[1])));
			} else {
				navstate.setOpen(OPEN.UNINITIALIZED);
			}
			navstate.setOrder(i + 1);
			navstatelist.add(navstate);
		}

		if (usersession != null) {
			usersession.setNavState(writeNavState());
		}
	}

	public void UpdatedNavigatorsFromSessionData(List<SearchResultNavigator> navigators) {

		if (!getNavstatelist().isEmpty()) {
			for (int i = 0; i < navigators.size(); i++) {
				SearchResultNavigator item = navigators.get(i);
				ResultNavigatorState navState = find(item.getField());

				if (null != navState) {
					item.setOrder(navState.getOrder());
					item.setOpen(navState.getOpen().getVal());
				} else {
					item.setOrder(i + 1);
				}
			}

			Collections.sort(navigators);
		}

		updateFirstTwoToShowAtleastFiveItemOpen(navigators);

	}

	private void updateFirstTwoToShowAtleastFiveItemOpen(List<SearchResultNavigator> navigators) {
		for (int i = 0; i < navigators.size(); i++) {
			SearchResultNavigator nav = navigators.get(i);
			if (i < 2) {
				if (nav.getOpen() == ResultNavigatorState.OPEN.UNINITIALIZED.getVal()) {
					nav.setOpen(ResultNavigatorState.OPEN.SHOW5.getVal());
				}
			} else {
				break;
			}

		}
	}

	/**
	 * Update the display order
	 * 
	 * @param navfield
	 * @param order
	 */
	public void updateNavAppend(boolean append) {
		this.appendopen = append;
		if (usersession != null) {
			usersession.setNavState(writeNavState());
		}
	}

	public boolean isAppendopen() {
		return appendopen;
	}

	public List<ResultNavigatorState> getNavstatelist() {
		return navstatelist;
	}

	public void setNavstatelist(List<ResultNavigatorState> navstatelist) {
		this.navstatelist = navstatelist;
	}

}
