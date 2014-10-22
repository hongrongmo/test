package org.ei.ane.entitlements;

import java.util.Set;

import org.ei.exception.ServiceException;

public interface EntitlementsService {

	Set<UserEntitlement> getUserEntitlements(String authToken, String allListID, String carsSessionId) throws ServiceException;
}
