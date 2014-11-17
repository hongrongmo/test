package org.ei.service.cars;

/**
 * Enum class containing the MIME response prefix and namespace
 * 
 * @author naikn1
 * @version 1.0
 *
 */
public enum XMLNamespaceEnum {
	/**
	 * Constant to hold the header types prefix and namespace as key value pair
	 */
	HEADER_TYPES_NS_URI("Q1", 
			"http://webservices.elsevier.com/schemas/easi/headers/types/v1"),
	/**
	 * Constant to hold the header types prefix and namespace as key value pair
	 */
	CARS_CLIENT_NS_URI("ns2", 
			"http://cars-services.elsevier.com/cars/client"),
	/**
	 * Constant to hold the CARS server prefix and namespace as key value pair
	 */
	CARS_SERVER_NS_URI("cars", 
			"http://cars-services.elsevier.com/cars/server"),
	/**
	 * Constant to hold the SOAP Envelope prefix and namespace as key value pair
	 */
	SOAP_ENVELOPE_NS_URI("soapenv", 
			"http://schemas.xmlsoap.org/soap/envelope/"),
	/**
	 * Constant to hold the CSAS types prefix and namespace as key value pair
	 */
	CSAS_TYPES_NS_URI("soapRef", 
			"http://webservices.elsevier.com/schemas/CSAS/types/v11"),
	/**
	 * Constant to hold the CSMS types prefix and namespace as key value pair
	 */
	CSMS_TYPES_NS_URI("genericContentMetadataUploadResponseElem", 
			"http://webservices.elsevier.com/schemas/CSMS/types/genericContentMetadata/v1");
	
	/** Attribute to hold the XML prefix */
	private final String m_prefix;
	
	/** Attribute to hold the XML namespace */
	private final String m_namespace;
	
	/**
	 * Constructor for initializing prefix and namespace
	 * 
	 * @param prefix - the prefix value
	 * @param namespace - the namespace value
	 */
	private XMLNamespaceEnum(String prefix, String namespace) {
		m_prefix = prefix;
		m_namespace = namespace;
	}
	
	/**
	 * Get the XML prefix value
	 * 
	 * @return String - m_prefix
	 */
	public String getPrefix() {
		return m_prefix;
	}
	
	/**
	 * Get the XML namespace value
	 * 
	 * @return String - m_namespace
	 */
	public String getNamespace() {
		return m_namespace;
	}
	
	/**
	 * This method will fetch the prefix for given namespace
	 * 
	 * @param namespace - the namespace value passed as input
	 * @return String - the corresponding prefix value
	 */
	public static String getPrefix(String namespace) {
		if (null != namespace) {
			for (XMLNamespaceEnum type : XMLNamespaceEnum.values()) {
				if (namespace.equalsIgnoreCase(type.m_namespace)) {
					return type.m_prefix;
				}
			}
		}
		return null;
	}
	
	/**
	 * This method will fetch the namespace for given prefix
	 * 
	 * @param prefix - the prefix value used as input
	 * @return String - the namespace for the prefix
	 */
	public static String getNameSpace(String prefix) {
		if (null != prefix) {
			for (XMLNamespaceEnum type : XMLNamespaceEnum.values()) {
				if (prefix.equalsIgnoreCase(type.m_prefix)) {
					return type.m_namespace;
				}
			}
		}
		return null;
	}
}

