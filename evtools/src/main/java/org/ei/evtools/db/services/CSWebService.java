package org.ei.evtools.db.services;

import org.ei.evtools.db.domain.Account;
import org.ei.evtools.exception.CSServiceException;

import com.elsevier.webservices.schemas.csas.types.v13.AuthenticateUserRespPayloadType;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public interface CSWebService {
	public Account getAccountInfo(String IP) throws CSServiceException;
	public AuthenticateUserRespPayloadType authenticateByIP(String IP) throws CSServiceException;
}
