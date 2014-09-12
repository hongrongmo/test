package org.ei.ane.fences;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ei.biz.personalization.EVWebUser;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.exception.ServiceException;

/**
 * This class will parse and set Fences from A&E system into the User's
 * UserPreferences object.
 * 
 * NOTE: the eBook feature has a UserPreference entry but it's controlled by
 * entitlements NOT fences!
 * 
 */
public class UserFencesHandler {
	private static final Logger log4j = Logger.getLogger(EVWebUser.class);

	public static void processUserFences(IEVWebUser webUser) throws ServiceException {
		IPlatformFencesService defaultFencesSer = new PlatformFencesServiceImpl();
		Map<String, String> allFenceIds;
		allFenceIds = defaultFencesSer.getPlatformFences();

		log4j.info("Current feature set:  " + webUser.getUserPreferences().getFeatures().toString());
		webUser.getUserPreferences().getFeatures().clear();

		if (null != webUser.getFenceIds()) {
			for (String fenceId : webUser.getFenceIds()) {
				String fenceName = (String) allFenceIds.get(fenceId);

				//
				// The fence names from A&E *should* match the contants in the
				// UserPreferences object so just set them directly here
				//
				if (StringUtils.isNotBlank(fenceName)) {
					webUser.setPreference(fenceName, true);
				}
			}
		}

	}

}
