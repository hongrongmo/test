package org.ei.struts.emetrics.tiles.channel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.tiles.DefinitionsFactoryException;
import org.apache.struts.tiles.FactoryNotFoundException;
import org.apache.struts.tiles.xmlDefinition.DefinitionsFactory;
import org.apache.struts.tiles.xmlDefinition.FactorySet;
import org.apache.struts.tiles.xmlDefinition.XmlDefinitionsSet;
import org.apache.struts.tiles.xmlDefinition.XmlParser;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.customer.view.UserContainer;
import org.ei.struts.emetrics.customer.view.UserView;
import org.xml.sax.SAXException;

/**
 * Component Definitions factory.
 * This implementation of Component Definitions factory allows i18n
 * Component definitions factory allowing i18n definition.
 * A definition is retrieved by its name, and using locale setted in appropriate session context.
 * Definitions are defined in different files, one for each locale. A definition file is loaded using common name extended with locale code (ex : templateDefinitions_fr.xml). If no file is found under this name, use default file.
*/
public class ChannelFactorySet extends FactorySet {

	protected static Log log = LogFactory.getLog(ChannelFactorySet.class);


	public static final String DEFAULT_DEFINITIONS_FILE_NAME = "/WEB-INF/templateDefinitions.xml";
	public static final String DEFINITIONS_CONFIG_PARAMETER_NAME = 	"definitions-config";

	/** Default factory */
	protected DefinitionsFactory defaultFactory;
	protected XmlParser xmlParser;

	public static final String FILENAME_EXTENSION = ".xml";

	private String filename;
	private Map loaded;

	/** Role key presently selected if switching to another role. */
	Object presentkey = null;

	/**
	 * Parameterless Constructor.
	 * Method initFactory must be called prior to any use of created factory.
	 */
	public ChannelFactorySet() {
	}

	/**
	 * Constructor.
	 * Init the factory by reading appropriate configuration file.
	 * @throw FactoryNotFoundException Can't find factory configuration file.
	 */
	public ChannelFactorySet(ServletContext servletContext, Map properties)
		throws DefinitionsFactoryException {
		initFactory(servletContext, properties);
	}

	/**
	 * Initialization method.
	 * Init the factory by reading appropriate configuration file.
	 * This method is called exactly once immediately after factory creation in
	 * case of internal creation (by DefinitionUtil).
	 * @param servletContext Servlet Context passed to newly created factory.
	 * @param properties Map of name/property passed to newly created factory. Map can contains
	 * more properties than requested.
	 * @throws DefinitionsFactoryException An error occur during initialization.
	 */
	public void initFactory(ServletContext servletContext, Map properties)
		throws DefinitionsFactoryException {
		// read properties values
		String proposedFilename =
			(String) properties.get(DEFINITIONS_CONFIG_PARAMETER_NAME);

		// Compute filenames to use
		boolean isFileSpecified = true;
		if (proposedFilename == null) {
			proposedFilename = DEFAULT_DEFINITIONS_FILE_NAME;
			isFileSpecified = false;
		}

		try {
			initFactory(servletContext, proposedFilename);
			return;
		} catch (FileNotFoundException ex) {
			// If a filename is specified, throw appropriate error.
			if (isFileSpecified) {
				throw new FactoryNotFoundException(
					ex.getMessage()
						+ " : Can't find file '"
						+ proposedFilename
						+ "'");
			} // end if
		} // end catch

	}

	/**
	 * Initialization method.
	 * Init the factory by reading appropriate configuration file.
	 * This method is called exactly once immediately after factory creation in
	 * case of internal creation (by DefinitionUtil).
	 * @param servletContext Servlet Context passed to newly created factory.
	 * @param proposedFilename File names, comma separated, to use as  base file names.
	 * @throws DefinitionsFactoryException An error occur during initialization.
	 */
	protected void initFactory(
		ServletContext servletContext,
		String proposedFilename)
		throws DefinitionsFactoryException, FileNotFoundException {

		filename = proposedFilename;
		loaded = new HashMap();
		defaultFactory = createDefaultFactory(servletContext);
	}

	/**
	 * Get default factory.
	 * @return Default factory
	 */
	protected DefinitionsFactory getDefaultFactory() {
		return defaultFactory;
	}

	/**
	 * Create default factory .
	* @param servletContext Current servlet context. Used to open file.
	* @return Created default definition factory.
	* @throws DefinitionsFactoryException If an error occur while creating factory.
	* @throws FileNotFoundException if factory can't be loaded from filenames.
	 */
	protected DefinitionsFactory createDefaultFactory(ServletContext servletContext)
		throws DefinitionsFactoryException, FileNotFoundException {
		XmlDefinitionsSet rootXmlConfig =
			parseXmlKeyFile(servletContext, "", null);
		if (rootXmlConfig == null)
			throw new FileNotFoundException();
		rootXmlConfig.resolveInheritances();
		return new DefinitionsFactory(rootXmlConfig);
	}

	/**
	 * Extract key that will be used to get the sub factory.
	 * @param name Name of requested definition
	 * @param request Current servlet request.
	 * @param servletContext Current servlet context
	 * @return the key or null if not found.
	 * @roseuid 3AF6F887018A
	 */
	protected Object getDefinitionsFactoryKey(
		String name,
		ServletRequest request,
		ServletContext servletContext) {
		Object key = null;
		UserContainer usercontainer = null;
		UserView user = null;

		HttpSession session = ((HttpServletRequest) request).getSession(false);

		if (session != null) {
			try {

				usercontainer = getUserContainer((HttpServletRequest) request);
				if (usercontainer != null) {
					user = (UserView) usercontainer.getUserView();
					if (user != null) {
						this.presentkey = user.getChannel();
					}
				}
			} catch (Exception e) {
				log.error("Error", e);
				return null;
			}
		}

		key = request.getParameter("channel");
		if (key == null) {

			if (this.presentkey != null) {

				key = this.presentkey; // stay in same definition
			}

		}

		return key;
	}

	/**
	 * Create a factory for specified key.
	* If creation failed, return default factory, and output an error message in
	* console.
	* @param key
	* @return Definition factory for specified key.
	* @throws DefinitionsFactoryException If an error occur while creating factory.
	 */
	protected DefinitionsFactory createFactory(
		Object key,
		ServletRequest request,
		ServletContext servletContext)
		throws DefinitionsFactoryException {
		if (key == null)
			return getDefaultFactory();

		// Already loaded ?
		DefinitionsFactory factory = (DefinitionsFactory) loaded.get(key);
		if (factory != null) { // yes, stop loading
			return factory;
		} // end if
		// Try to load file associated to key. If fail, stop and return default factory.
		XmlDefinitionsSet lastXmlFile =
			parseXmlKeyFile(servletContext, "_" + (String) key, null);
		if (lastXmlFile == null) {
			log.warn(
				"Warning : No definition factory associated to key '"
					+ key
					+ "'. Use default factory instead.");
			factory = getDefaultFactory();
			loaded.put(key, factory);
			return factory;
		} // end if

		// Parse default file, and add key file.
		XmlDefinitionsSet rootXmlConfig =
			parseXmlKeyFile(servletContext, "", null);

		rootXmlConfig.extend(lastXmlFile);
		rootXmlConfig.resolveInheritances();

		factory = new DefinitionsFactory(rootXmlConfig);
		loaded.put(key, factory);
		// User help

		log.debug(factory);
		// return last available found !
		return factory;
	}

	/**
	 * Parse files associated to postix if they exist.
	 * For each name in filenames, append postfix before file extension,
	 * then try to load the corresponding file.
	 * If file doesn't exist, try next one. Each file description is added to
	 * the XmlDefinitionsSet description.
	 * The XmlDefinitionsSet description is created only if there is a definition file.
	 * Inheritance is not resolved in the returned XmlDefinitionsSet.
	 * If no description file can be opened, and no definiion set is provided, return null.
	 * @param postfix Postfix to add to each description file.
	 * @param xmlDefinitions Definitions set to which definitions will be added. If null, a definitions
	 * set is created on request.
	 * @return XmlDefinitionsSet The definitions set created or passed as parameter.
	 * @throws DefinitionsFactoryException If an error happen during file parsing.
	 */
	private XmlDefinitionsSet parseXmlKeyFile(
		ServletContext servletContext,
		String postfix,
		XmlDefinitionsSet xmlDefinitions)
		throws DefinitionsFactoryException {
		if (postfix != null && postfix.length() == 0)
			postfix = null;

		String fullName = concatPostfix(filename, postfix);
		return parseXmlFile(servletContext, fullName, xmlDefinitions);
	}
	/**
	 * Parse specified xml file and add definition to specified definitions set.
	 * This method is used to load several description files in one instances list.
	 * If filename exist and definition set is null, create a new set. Otherwise, return
	 * passed definition set (can be null).
	 * @param servletContext Current servlet context. Used to open file.
	 * @param filename Name of file to parse.
	 * @param xmlDefinitions Definitions set to which definitions will be added. If null, a definitions
	 * set is created on request.
	 * @return XmlDefinitionsSet The definitions set created or passed as parameter.
	 * @throws DefinitionsFactoryException If an error happen during file parsing.
	 */
	private XmlDefinitionsSet parseXmlFile(
		ServletContext servletContext,
		String filename,
		XmlDefinitionsSet xmlDefinitions)
		throws DefinitionsFactoryException {
		try {
			log.debug("Try to load '" + filename + "'.");
			InputStream input = servletContext.getResourceAsStream(filename);
			if (input == null)
				return xmlDefinitions;

			// Create parser
			xmlParser = new XmlParser();
			// Check if definition set already exist.
			if (xmlDefinitions == null) { // create it
				xmlDefinitions = new XmlDefinitionsSet();
			}

			xmlParser.parse(input, xmlDefinitions);
		} catch (SAXException ex) {
			log.error("SAX Error while parsing file '" + filename + "'.", ex);
			throw new DefinitionsFactoryException(
				"Error while parsing file '"
					+ filename
					+ "'. "
					+ ex.getMessage(),
				ex);
		} catch (IOException ex) {
			log.error("IO Error while parsing file '" + filename + "'.", ex);
			throw new DefinitionsFactoryException(
				"IO Error while parsing file '"
					+ filename
					+ "'. "
					+ ex.getMessage(),
				ex);
		}

		return xmlDefinitions;
	}

	/**
	 * Concat postfix to the name. Take care of existing filename extension.
	 * Transform the given name "name.ext" to have "name" + "postfix" + "ext".
	 * If there is no ext, return "name" + "postfix".
	 */
	private String concatPostfix(String name, String postfix) {
		if (postfix == null)
			return name;

		//postfix = "_" + postfix;
		// Search file name extension.
		// take care of Unix files starting with .
		int dotIndex = name.lastIndexOf(".");
		int lastNameStart = name.lastIndexOf(java.io.File.pathSeparator);
		if (dotIndex < 1 || dotIndex < lastNameStart)
			return name + postfix;

		String ext = name.substring(dotIndex);
		name = name.substring(0, dotIndex);
		return name + postfix + ext;
	}

	/**
	 * Retrieve the UserContainer for the user tier to the request.
	 */
	protected UserContainer getUserContainer(HttpServletRequest request) {

		UserContainer userContainer = null;

		HttpSession session = request.getSession(false);

		if (session != null) {
			userContainer =
				(UserContainer) session.getAttribute(
					Constants.USER_CONTAINER_KEY);
			// Create a UserContainer for the user if it doesn't exist already
			// THis was a problem with logging in -
			// THE ONLY part of the system that can do this is the LoginAction!!!!
			/*		if(userContainer == null) {
					  userContainer = new UserContainer();
					  userContainer.setLocale(request.getLocale());
					  session = request.getSession();
					  session.setAttribute(Constants.USER_CONTAINER_KEY, userContainer);
					}
			*/
		}

		return userContainer;
	}

}
