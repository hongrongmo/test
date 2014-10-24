package org.ei.evtools.db.services;

import java.util.List;

import javax.xml.ws.Holder;

import org.apache.log4j.Logger;
import org.ei.evtools.db.domain.Account;
import org.ei.evtools.exception.CSServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.elsevier.edit.common.pool.ITSPool;
import com.elsevier.edit.common.pool.PoolException;
import com.elsevier.webservices.schemas.csas.constants.types.v7.AuthenticationStatusCodeType;
import com.elsevier.webservices.schemas.csas.constants.types.v7.AuthenticationType;
import com.elsevier.webservices.schemas.csas.types.v13.AuthenticateUserReqPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.AuthenticateUserRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.AuthenticateUserResponseType;
import com.elsevier.webservices.schemas.csas.types.v13.AuthenticateUserType;
import com.elsevier.webservices.schemas.csas.types.v13.AuthenticationResponseStatusType;
import com.elsevier.webservices.schemas.csas.types.v13.ConnectionInfoType;
import com.elsevier.webservices.schemas.csas.types.v13.PathChoiceInfoType;
import com.elsevier.webservices.schemas.csas.types.v13.PlatformInfoType;
import com.elsevier.webservices.schemas.easi.headers.types.v1.LogLevelType;
import com.elsevier.webservices.schemas.easi.headers.types.v1.RequestHeaderType;
import com.elsevier.webservices.schemas.easi.headers.types.v1.ResponseHeaderType;
import com.elsevier.wsdls.csas.service.v13.CSApplicationServicePortTypeV13;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
@Service("CSWebService")
@PropertySource("classpath:application.properties")
public class CSWebServiceImpl implements CSWebService {
	
	static Logger logger = Logger.getLogger(CSWebServiceImpl.class);
	
	public static final String PLATFORM_CODE = "EV";
	public static final String SITE_IDENTIFIER = "engvil";
	public static final String CONSUMER_APP = "ENGVIL";
	public static final String CONSUMER_CLIENT = "ENGVIL";
	public static final String WEBSERVICE_LOG_LEVEL = "Default";
	public static final String WEBSERVICE_VERSION = "13";
	public static final String MAX_POOL_CSWS_PROXIES = "0";
	public static final String NOT_AVAILABLE = "N/A";
	public static final String CLONE_HOST_NAME = "EV_USER";
	
	private static ITSPool aneWSProxyPool = null;
	
	@Autowired
	private Environment environment;
	
	public AuthenticateUserRespPayloadType authenticateByIP(String IP) throws CSServiceException {
        CSApplicationServicePortTypeV13 port = null;

        ConnectionInfoType connectioninfo = new ConnectionInfoType();
        connectioninfo.setBrowserType("N/A");
        connectioninfo.setIpAddress(IP);

        final AuthenticateUserReqPayloadType payload = new AuthenticateUserReqPayloadType();
        payload.setPathChoiceNumber(1);
        payload.setPlatformInfo(CSWebServiceImpl.getEVPlatformInfo());
        payload.setAuthenticationType(AuthenticationType.IP);
        payload.setConnectionInfo(connectioninfo);
        payload.setIsSSODisabled(true);

        AuthenticateUserType authenticateuser = new AuthenticateUserType();
        authenticateuser.setAuthenticateUserReqPayload(payload);

        Holder<AuthenticateUserResponseType> authenticateResponseHolder = new Holder<AuthenticateUserResponseType>();

        try {
            RequestHeaderType reqHeader = CSWebServiceImpl.getRequestHeaderHolder();
            Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
            logger.info("Retrieving CSWS proxy...");
            String key = environment.getProperty("ENVIRONMENT")+".CSAS_BASE_URL";
            String csasbaseURL = environment.getProperty(key);
            logger.info("csas base url key='"+key+"', value='"+csasbaseURL+"'");
            port = CSWebServiceImpl.getANEService(csasbaseURL);
            port.authenticateUser(authenticateuser, reqHeader, authenticateResponseHolder, respHeaderHolder);
            logger.info("User info are fetched successfully using Customer System web service....");
        } catch(Exception e){
        	logger.error("Failed to fetch the user info for the ip="+IP,e);
        	throw new CSServiceException("Failed to fetch the user info for the ip="+IP, e);
        }finally {
        	CSWebServiceImpl.releasePort(port);
        }

        return authenticateResponseHolder.value.getAuthenticateUserRespPayload();
	}
	
	public Account getAccountInfo(String IP) throws CSServiceException{
		AuthenticationResponseStatusType status;
        try {
            AuthenticateUserRespPayloadType payload = authenticateByIP(IP);
            status = payload.getStatus();

            if (AuthenticationStatusCodeType.OK.equals(status.getStatusCode())) {
                // If there is Path Choice info something is wrong in account setup!
                List<PathChoiceInfoType> paths = payload.getPathChoiceInfo();
                if (!paths.isEmpty()) {
                    logger.error("Path Choice encountered trying to authenticate by IP: ");
                    for (PathChoiceInfoType choice : paths) {
                        logger.error("Choice: " + choice.getPathChoiceNumber() + ", Description: " + choice.getDescription());
                    }
                    return null;
                }

                // Now get the account info from payload
                Account account = new Account();
                account.setAccountId(payload.getUserInfo().getAccountId());
                account.setAccountName(payload.getUserInfo().getAccountName());
                account.setAccountNumber(payload.getUserInfo().getAccountNumber());
                account.setDepartmentId(payload.getUserInfo().getDepartmentId());
                account.setDepartmentName(payload.getUserInfo().getDepartmentName());
                return account;
            }else{
            	 logger.warn("Authentication error occured for the IP="+IP+" so empty account object is being returned");
            }
        } catch (Exception e) {
           logger.error("Unable to lookup Account by IP!="+IP+" so empty account object is being returned",e);
           return null;
           //throw new CSServiceException("Failed to fetch the user info for the ip="+IP, e);
        }
        return null;
	}
	
	private static PlatformInfoType getEVPlatformInfo() {
		PlatformInfoType platformInfo = new PlatformInfoType();
		platformInfo.setPlatformCode(PLATFORM_CODE);
		platformInfo.setSiteIdentifier(SITE_IDENTIFIER);
		return platformInfo;
	}
	
	public static RequestHeaderType getRequestHeaderHolder() {
		RequestHeaderType reqHeader = new RequestHeaderType();

		reqHeader.setConsumer(CONSUMER_APP);
		reqHeader.setConsumerClient(CONSUMER_CLIENT);
		reqHeader.setLogLevel(LogLevelType.fromValue(WEBSERVICE_LOG_LEVEL));

		String ipAddress;
		String transId;
		String transGroupId;
		ipAddress = NOT_AVAILABLE;
		transId = NOT_AVAILABLE;
		transGroupId = NOT_AVAILABLE;
		reqHeader.setOpaqueInfo("Clone=" + CLONE_HOST_NAME + ";IP=" + ipAddress + ";transGroupId=" + transGroupId);
		reqHeader.setReqId(transId);

		reqHeader.setVer(WEBSERVICE_VERSION);
		return reqHeader;
	}
	
	public static CSApplicationServicePortTypeV13 getANEService(String baseUrl) throws CSServiceException {
		logger.info("Getting proxy pool for ANE service...");
		CSApplicationServicePortTypeV13 port = null;
		aneWSProxyPool = ANEServiceProxy.getProxyPool(baseUrl);
		try {
			port = (CSApplicationServicePortTypeV13) aneWSProxyPool.get(0);
		} catch (PoolException e) {
			logger.error("Error in creating proxy for ANE service", e);
			throw new CSServiceException("Error in creating proxy for ANE service", e);
		}
		return port;
	}

	public static void releasePort(CSApplicationServicePortTypeV13 port) {
		aneWSProxyPool.release(port);
	}
}
