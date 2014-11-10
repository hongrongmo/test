package org.ei.ane.textzones;

import java.util.Map;

import org.ei.exception.ServiceException;

public interface TextZonesService {

	public Map<String, Map<String, Object>> getPlatformTextZones() throws ServiceException;

	public Map<String, String> getUserTextZones(String authToken, String carsSessionId) throws ServiceException;

}
