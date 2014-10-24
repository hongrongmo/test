package org.ei.ane.entitlements;

import java.util.Set;

import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.service.CSWebService;
import org.ei.service.CSWebServiceImpl;

import com.elsevier.webservices.schemas.csas.types.v13.GetContentEntitlementsRespPayloadType;

public class EntitlementsServiceImpl implements EntitlementsService {

	private final static Logger log4j = Logger.getLogger(EntitlementsServiceImpl.class);

	public static String CONTENT_TYPE_DATABASE = "contentTypeCode=DB";
	public static String CONTENT_TYPE_BULLETIN = "contentTypeCode=BLT";

	public Set<UserEntitlement> getUserEntitlements(String authToken, String allListID, String carsSessionId) throws ServiceException {

		log4j.info("call to get the a user's entitlement list: START");
		CSWebService cs = new CSWebServiceImpl();
		GetContentEntitlementsRespPayloadType payload = cs.getUserEntitelments(authToken, carsSessionId);
		log4j.info("call to get the a user's entitlement list: END");
		return UserEntitlementsBuilder.fromEntitlementsPayload(payload);

	}

}
