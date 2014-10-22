package org.ei.cache;

import java.io.InputStream;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;

public final class EVCache {
	private static Logger log4j = Logger.getLogger(EVCache.class);

	private static EVCache instance;
	private CacheManager cacheManager;
	
    public final static String CARS_TEMPLATES_CACHE = "cars_templates";
    public final static String CARS_I18N_DATA_CACHE = "cars_i18n_data";
	public final static String PLATFORM_TEXT_ZONES_CACHE = "platform_text_zones";
	public final static String PLATFORM_FENCES_CACHE = "platform_fences";
	public final static String PLATFORM_ENTITLEMENTS_CACHE = "platform_entitlements";
	
    public final static String NCE_CARS_TEMPLATE = "CARS_TEMPLATE";
    public final static String NCE_CARS_I18N_DATA = "CARS_I18N_DATA";
	public final static String PLATFORM_TEXT_ZONES = "PLATFORM_TEXT_ZONES";
	
	public final static String PLATFORM_FENCES = "platform_fences_map";

	/**
	 * Private constructor
	 */
	private EVCache() {
	}

	/**
	 * Initi method for cache - attempts to initialize from InputStream.  It
	 * expects this to be an XML file conforming to ehcache DTD.
	 * 
	 * @param configuration
	 * @return
	 */
	public static EVCache getInstance(InputStream configuration) {
		if (instance == null) {
			instance = new EVCache();
			instance.cacheManager = CacheManager.create(configuration);
		} else {
			log4j.warn("EVCache is already initialized!");
		}
		return instance;
	}

	/**
	 * Return the singleton instance
	 * @return
	 *//*
	public static EVCache getInstance() {
		if (instance == null) {
			throw new RuntimeException("EVCache object is NOT initialized!");
		}
		return instance;
	}*/

	/**
	 * Return a cache by name
	 * @param cacheName
	 * @return
	 */
	public Cache getCache(String cacheName) {
		if (instance == null) {
			throw new CacheException("Cache has not been initialized!");
		}
		return instance.cacheManager.getCache(cacheName);
	}

    /**
     * Return the CARS Template cache
     * @return
     */
    public static Cache getCarsTemplatesCache() {
        if (instance == null) {
            throw new CacheException("Cache has not been initialized!");
        }
        return instance.cacheManager.getCache(CARS_TEMPLATES_CACHE);
    }

    /**
     * Return the CARS I18N data cache
     * @return
     */
    public static Cache getCarsI18nDataCache() {
        if (instance == null) {
            throw new CacheException("Cache has not been initialized!");
        }
        return instance.cacheManager.getCache(CARS_I18N_DATA_CACHE);
    }

	/**
	 * Return the Platform Fences cache
	 * @return
	 */
	public static Cache getPlatformFencesCache() {
		if (instance == null) {
			throw new CacheException("Cache has not been initialized!");
		}
		return instance.cacheManager.getCache(PLATFORM_FENCES_CACHE);
	}
	
	
	public static Cache getPaltformTextZonesCache() {
		if (instance == null) {
			throw new CacheException("Cache has not been initialized!");
		}
		return instance.cacheManager.getCache(PLATFORM_TEXT_ZONES_CACHE);
	}
	
	public static Cache getPaltformEntitlementsCache() {
		if (instance == null) {
			throw new CacheException("Cache has not been initialized!");
		}
		return instance.cacheManager.getCache(PLATFORM_ENTITLEMENTS_CACHE);
	}

}
