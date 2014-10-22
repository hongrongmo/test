package org.ei.ane.fences;

import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.ei.cache.EVCache;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;

public class PlatformFencesServiceImpl implements IPlatformFencesService {

	private final static Logger log4j = Logger.getLogger(PlatformFencesServiceImpl.class);
	public final static String PLATFORM_FENCES = "PLATFORM_FENCES";

	@SuppressWarnings("unchecked")
	public Map<String, String> getPlatformFences() throws ServiceException {
		Map<String, String> platformFenceMap = null;

		Cache cache = EVCache.getPlatformFencesCache();

		// System.out.println(new
		// Timestamp(System.currentTimeMillis())+"----------"+
		// cache.isElementInMemory(PLATFORM_FENCES)+"-------"+cache.calculateInMemorySize());

		// Attempt to retrieve Map of fence key/value pairs from cache
		if (null != cache.get(EVCache.PLATFORM_FENCES_CACHE)) {
			platformFenceMap = (Map<String, String>) cache.get(EVCache.PLATFORM_FENCES_CACHE).getObjectValue();
			if ((null != platformFenceMap)) {
				return platformFenceMap;
			}
		}
		// Map could not be populated from Cache, create via call
		// to CS Web Service
		platformFenceMap = populatePlatformFences();

		// System.out.println(new
		// Timestamp(System.currentTimeMillis())+"----------"+
		// cache.isElementInMemory(PLATFORM_FENCES)+"-------"+cache.calculateInMemorySize());

		if (log4j.isInfoEnabled()) {
			for (String key : platformFenceMap.keySet()) {
				log4j.info("Platform Fence Key: " + key + ": " + platformFenceMap.get(key));
			}
		}

		return platformFenceMap;
	}

	/**
	 * Attempt to retrieve Platform Fences from CS and put into ehcache
	 * 
	 * @param platformFenceCache
	 * @throws ServiceException
	 */
	public Map<String, String> populatePlatformFences() throws ServiceException {
		Map<String, String> platformFenceMap = null;
		try {
			platformFenceMap = FencesDataPopulator.buildDefaultFencesData();
			EVCache.getPlatformFencesCache().put(new Element(EVCache.PLATFORM_FENCES_CACHE, platformFenceMap));
		} catch (Exception e) {
			log4j.warn("Default Platform Fences can not be fetched", e);
			throw new ServiceException(SystemErrorCodes.CS_DEFAULT_FENCE_FETCH_ERROR, "Default Platform Fences can not be fetched From CSAS Service call..", e);
		}
		return platformFenceMap;
	}
}
