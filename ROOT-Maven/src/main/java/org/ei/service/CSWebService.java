package org.ei.service;

import org.ei.exception.ServiceException;

import com.elsevier.webservices.schemas.csas.types.v13.AuthenticateUserRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.GetContentEntitlementsRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.GetNonCombEntValueDefaultsRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.GetNonCombEntValuesForSitesRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.GetPlatformFenceInfoRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.NonCombEntValuesForSessionRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.PasswordReminderRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.UserProfileRespPayloadType;




public interface CSWebService {

    public PasswordReminderRespPayloadType getPasswordReminder(String email, String ipaddress) throws ServiceException;

    public AuthenticateUserRespPayloadType authenticateLindaHall(String username, String password) throws ServiceException;

    public AuthenticateUserRespPayloadType authenticateByIP(String IP) throws ServiceException;

    public void terminate(String authtoken) throws ServiceException;

    public UserProfileRespPayloadType getProfile(int webUserID) throws ServiceException;

	public GetNonCombEntValueDefaultsRespPayloadType getNonCombEntValueDefaults() throws ServiceException ;

	public NonCombEntValuesForSessionRespPayloadType getNonCombEntValueForSession(String authToken, String carsSessionId) throws ServiceException;

	GetNonCombEntValuesForSitesRespPayloadType getNonCombEntValuesForSites (String nceTypeName) throws ServiceException ;

	GetPlatformFenceInfoRespPayloadType getPlatformFenceInfo() throws ServiceException;

	GetContentEntitlementsRespPayloadType getUserEntitelments(String authToken, String carsSessionId) throws ServiceException;
}
