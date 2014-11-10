package org.ei.ane.fences;

import java.util.Map;

import org.ei.exception.ServiceException;

public interface IPlatformFencesService {

	public Map<String, String> getPlatformFences() throws ServiceException;

}
