package org.ei.ane.template;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Templates;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.ei.cache.EVCache;
import org.ei.exception.ServiceException;

public class ANETemplatesServiceImpl implements ANETemplatesService {

	private final static Logger log4j = Logger.getLogger(ANETemplatesServiceImpl.class);

	public Templates getTemplateForNCEName(String nceName, Timestamp templateUpdateDate) throws ServiceException {
		Map<String, Object> nceCarsTemplates = new HashMap<String, Object>();

		Cache cache = EVCache.getCarsTemplatesCache();

		
		if ((null != cache.get(EVCache.NCE_CARS_TEMPLATE))) {
			if (!IsCARSTemplatedUpdatedAfterCaching(templateUpdateDate, cache)) {
				return (Templates) getANETemplateMapFromCache(cache).get(nceName);
			}
		}
		
		nceCarsTemplates = populateCacheWithCarsTemplateAndTextZones();
		
		return (Templates) nceCarsTemplates.get(nceName);
	}

	public Map<String, Object> populateCacheWithCarsTemplateAndTextZones() throws ServiceException {
		log4j.info("populating cars template and text zone data into cache from CSAS service calls..Begin");
		Map<String, Object> nceCarsTemplates = new HashMap<String, Object>();
		ANETemplatesLoader loader = new ANETemplatesLoader();
		Map<String, Map<String, Object>> allNCETypesData = loader.buildAllNCETypesData();

		Cache templatesCache = EVCache.getCarsTemplatesCache();
		Cache i18nDataCache = EVCache.getCarsI18nDataCache();
		Cache textZonesCache = EVCache.getPaltformTextZonesCache();

		// Populate Cars Template data from CSAS call
		nceCarsTemplates = getNceCarsTemplates(allNCETypesData);
		CARSTemplateData templateData = new CARSTemplateData();
		templateData.setCacheBuiltUpTime(new Timestamp(System.currentTimeMillis()));
		templateData.setAneTemplateMap(nceCarsTemplates);

		// put Cars Template data in cache
		templatesCache.put(new Element(EVCache.NCE_CARS_TEMPLATE, templateData));

		// Populate Cars I18n data from CSAS call and put Cars Template data in
		// cache
		i18nDataCache.put(new Element(EVCache.NCE_CARS_I18N_DATA, getNceCarsI18nData(allNCETypesData)));

		// put Platform level Text Zone data in Cache
		textZonesCache.put(new Element(EVCache.PLATFORM_TEXT_ZONES, getTextZones(allNCETypesData)));
		log4j.info("populating cars template and text zone data into cache from CSAS service calls..End");

		return nceCarsTemplates;
	}

	private Map<String, Object> getNceCarsTemplates(Map<String, Map<String, Object>> allNCETypesData) {
		return allNCETypesData.get(ANETemplatesLoader.NCE_CARS_TEMPLATE);
	}

	private Map<String, Object> getNceCarsI18nData(Map<String, Map<String, Object>> allNCETypesData) {
		return allNCETypesData.get(ANETemplatesLoader.NCE_CARS_I18N_DATA);
	}

	private Map<String, Object> getTextZones(Map<String, Map<String, Object>> allNCETypesData) {
		Map<String, Object> textZones = new HashMap<String, Object>();

		for (String nceByType : allNCETypesData.keySet()) {
			if ((!ANETemplatesLoader.NCE_CARS_TEMPLATE.equalsIgnoreCase(nceByType)) && (!ANETemplatesLoader.NCE_CARS_I18N_DATA.equalsIgnoreCase(nceByType))) {
				textZones.put(nceByType, allNCETypesData.get(nceByType));
			}
		}
		return textZones;
	}

	private boolean IsCARSTemplatedUpdatedAfterCaching(Timestamp templateUpdateDate, Cache cache) {

		if (null != templateUpdateDate) {
			return getCARSTemplateDataFromCache(cache).getCacheBuiltUpTime().before(templateUpdateDate);
		}
		return false;

	}

	private Map<String, Object> getANETemplateMapFromCache(Cache cache) {
		return (Map<String, Object>) getCARSTemplateDataFromCache(cache).getAneTemplateMap();
	}

	private CARSTemplateData getCARSTemplateDataFromCache(Cache cache) {
		return (CARSTemplateData) cache.get(ANETemplatesLoader.NCE_CARS_TEMPLATE).getObjectValue();
	}

}
