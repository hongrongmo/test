package org.ei.ane.textzones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;

import org.apache.log4j.Logger;
import org.ei.ane.template.ANETemplatesLoader;
import org.ei.ane.template.ANETemplatesServiceImpl;
import org.ei.cache.EVCache;
import org.ei.exception.ServiceException;
import org.ei.service.CSWebService;
import org.ei.service.CSWebServiceImpl;

import com.elsevier.webservices.schemas.csas.constants.types.v7.NonCombEntValuesStatusCodeType;
import com.elsevier.webservices.schemas.csas.types.v13.NCEIdValuesType;
import com.elsevier.webservices.schemas.csas.types.v13.NCENameValuesByType;
import com.elsevier.webservices.schemas.csas.types.v13.NonCombEntValuesForSessionRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.NonCombEntValuesStatusInfoType;

public class TextZonesServiceImpl implements TextZonesService {

	private final static Logger log4j = Logger.getLogger(TextZonesServiceImpl.class);

	public final static String NCE_CARS_TEMPLATE = "CARS_TEMPLATE";
	private final static String TEXT_ZONE_ERR = "Load exception - Unable to load text Zone data";

	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Object>> getPlatformTextZones() throws ServiceException {
		Cache cache = EVCache.getPaltformTextZonesCache();

		if ((null != cache.get(EVCache.PLATFORM_TEXT_ZONES))) {
			return (Map<String, Map<String, Object>>) cache.get(EVCache.PLATFORM_TEXT_ZONES).getObjectValue();
		}

		ANETemplatesServiceImpl service = new ANETemplatesServiceImpl();
		service.populateCacheWithCarsTemplateAndTextZones();

		return (Map<String, Map<String, Object>>) cache.get(EVCache.PLATFORM_TEXT_ZONES).getObjectValue();
	}

	public Map<String, String> getUserTextZones(String authToken, String carsSessionId) throws ServiceException {
		log4j.info("Building user text zone data");

		CSWebService service = new CSWebServiceImpl();
		NonCombEntValuesForSessionRespPayloadType payload = service.getNonCombEntValueForSession(authToken, carsSessionId);

		if (payload != null) {
			NonCombEntValuesStatusInfoType status = payload.getNonCombEntValuesStatusInfoType();
			if (null != status && NonCombEntValuesStatusCodeType.OK.equals(status.getStatusCode())) {
				List<NCENameValuesByType> nceValuesTypes = payload.getNceNameValuesByType();
				if (!nceValuesTypes.isEmpty()) {
					return populateUserTextZoneValues(nceValuesTypes);
				}
			} else {
				log4j.error(TEXT_ZONE_ERR + ": Status from CSWS = " + status.getStatusCode().value(), new Throwable(TEXT_ZONE_ERR));
			}
		}
		return new HashMap<String, String>();
	}

	private Map<String, String> populateUserTextZoneValues(List<NCENameValuesByType> nceNameValuesByTypes) throws ServiceException {
		Map<String, Map<String, Object>> platformTextZones = new HashMap<String, Map<String, Object>>();
		Map<String, String> userTextZones = new HashMap<String, String>();
		platformTextZones = getPlatformTextZones();

		for (NCENameValuesByType valuesByType : nceNameValuesByTypes) {
			for (NCEIdValuesType nameValueType : valuesByType.getNceIdValuesType()) {

				if (!(ANETemplatesLoader.NCE_CARS_TEMPLATE).equalsIgnoreCase(valuesByType.getNceTypeName())) {
					if(platformTextZones.get(valuesByType.getNceTypeName()) != null){
						String textZoneName = (String) platformTextZones.get(valuesByType.getNceTypeName()).get(nameValueType.getNceId());
						userTextZones.put(textZoneName, nameValueType.getNceValue1());
					}
				}
			}
		}

		return userTextZones;
	}

}
