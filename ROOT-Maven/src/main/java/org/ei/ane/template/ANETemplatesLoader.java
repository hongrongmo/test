package org.ei.ane.template;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.CSWebService;
import org.ei.service.CSWebServiceImpl;

import com.elsevier.webservices.schemas.csas.constants.types.v7.NonCombEntValuesStatusCodeType;
import com.elsevier.webservices.schemas.csas.types.v13.GetNonCombEntValueDefaultsRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.NCEIdNameValueType;
import com.elsevier.webservices.schemas.csas.types.v13.NCEValuesByType;
import com.elsevier.webservices.schemas.csas.types.v13.NonCombEntValuesStatusInfoType;
import com.elsevier.webservices.schemas.csas.types.v13.PlatformNCEValuesType;
import com.elsevier.webservices.schemas.csas.types.v13.ProductNCEValuesType;

public class ANETemplatesLoader {

	private final static Logger log4j = Logger.getLogger(ANETemplatesLoader.class);
	public final static String NCE_CARS_TEMPLATE = "CARS_TEMPLATE";
	public final static String NCE_CARS_I18N_DATA = "CARS_TEMPLATE_I18N";
	private final static String TEXT_ZONE_ERR = "Load exception - Unable to load text Zone data";

	public Map<String, Map<String, Object>> buildAllNCETypesData() throws ServiceException {
		log4j.info("Building templates data");
		NonCombEntValuesStatusInfoType status;
		Map<String, Map<String, Object>> aneNceMapByType = new HashMap<String, Map<String, Object>>();
		try {
			GetNonCombEntValueDefaultsRespPayloadType payload = null;
			CSWebService service = new CSWebServiceImpl();
			payload = service.getNonCombEntValueDefaults();

			if (payload != null) {
				status = payload.getStatusInfo();
				if (status != null) {
					if (NonCombEntValuesStatusCodeType.OK.equals(status.getStatusCode())) {
						PlatformNCEValuesType platformNCEValuesType = payload.getPlatformNCEValues();
						if (platformNCEValuesType != null) {
							List<ProductNCEValuesType> siteNCEValuesTypes = platformNCEValuesType.getProductNCEValuesType();
							List<NCEValuesByType> nceValuesByTypes = new ArrayList<NCEValuesByType>();

							for (ProductNCEValuesType type : siteNCEValuesTypes) {
								nceValuesByTypes.addAll(type.getNceValuesByType());
							}
							aneNceMapByType = loadAllNCETypes(nceValuesByTypes);
						}
					} else {
						log4j.error(TEXT_ZONE_ERR + ": Status from CSWS = " + status.getStatusCode().value(), new Throwable(TEXT_ZONE_ERR));
						throw new org.ei.exception.ServiceException(SystemErrorCodes.CS_DEFAULT_TEXTZONE_FETCH_ERROR, 
								TEXT_ZONE_ERR + ": Status from CSWS = " + status.getStatusCode().value());
					}
				}
			}
		} catch (Exception e) {
			log4j.error("Unknown error occured during Text Zone cache loading", e);
			throw new ServiceException(SystemErrorCodes.UNKNOWN_SERVICE_ERROR, 
					"Unknown error occured during Text Zone cache loading", e);
		}

		return aneNceMapByType;
	}

	private Map<String, Map<String, Object>> loadAllNCETypes(List<NCEValuesByType> nceValuesByTypes) throws ServiceException {
		Map<String, Map<String, Object>> nceNamesMapByTypeName = new HashMap<String, Map<String, Object>>();

		TransformerFactory tFactory = null;
		if (null != nceValuesByTypes) {
			for (NCEValuesByType valuesByType : nceValuesByTypes) {
				Map<String, Object> nceDataByName = new HashMap<String, Object>();
				for (NCEIdNameValueType nameValueType : valuesByType.getNceIdNameValueType()) {
					if ((NCE_CARS_TEMPLATE).equalsIgnoreCase(valuesByType.getNceTypeName()) && !"CARS_HTT".equals(nameValueType.getNceName())) {

						try {
							tFactory = TransformerFactory.newInstance();

							/*
							 * if(nameValueType.getNceName().equalsIgnoreCase(
							 * "CARS_LOGIN")){
							 * System.out.println(nameValueType.getNceValue1
							 * ().trim()); }
							 */

							Templates template = tFactory.newTemplates(new StreamSource(new StringReader(nameValueType.getNceValue1().trim())));
							nceDataByName.put(nameValueType.getNceName(), template);
						} catch (TransformerConfigurationException e) {
							log4j.warn("Template creation failed for:" + nameValueType.getNceName());
							// throw new
							// ServiceException("Template creation failed",e);
						}
					} else if (NCE_CARS_I18N_DATA.equalsIgnoreCase(valuesByType.getNceTypeName())) {
						nceDataByName.put(nameValueType.getNceName(), nameValueType.getNceValue1());
					} else {

						nceDataByName.put(nameValueType.getNceId(), nameValueType.getNceName());
					}
				}
				nceNamesMapByTypeName.put(valuesByType.getNceTypeName(), nceDataByName);
			}
		}
		return nceNamesMapByTypeName;
	}

}
