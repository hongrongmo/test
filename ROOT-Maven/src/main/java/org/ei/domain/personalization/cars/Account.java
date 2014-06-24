package org.ei.domain.personalization.cars;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.config.RuntimeProperties;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.CSWebService;
import org.ei.service.CSWebServiceImpl;
import org.ei.service.cars.CARSStringConstants;

import com.elsevier.webservices.schemas.csas.constants.types.v6.AuthenticationStatusCodeType;
import com.elsevier.webservices.schemas.csas.constants.types.v6.RefworksAuthTypeCodeType;
import com.elsevier.webservices.schemas.csas.types.v12.AuthenticateUserRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v12.AuthenticationResponseStatusType;
import com.elsevier.webservices.schemas.csas.types.v12.PathChoiceInfoType;


/**
 * This class holds information about a user's account.
 * All constructors are package scope because only classes
 * in this package can create an Account.
 *
 * @author naikn1
 * @version 1.0
 *
 */
public class Account implements Serializable {

    private static final Logger log4j = Logger.getLogger(Account.class);

	private static final long serialVersionUID = 751606458963201496L;

	private String accountName;
	private String departmentId;
	private String departmentName;
	private String accountId;
	private String accountNumber;
	private String docDeliveryMessage;
	private String docDeliveryEmail;
	private String athensOrgId;
	private String refworksServerBaseURL;
	private String rwHighestLvlGroupId;
	private String refworksUsrLvlGroupId;
	private String refworksUserName;
	private String refworksUserPassword;
	private String athensPUID;
	private boolean athensEnabled = false;
	private boolean allowViewRWGroupId = false;
	private boolean crossFireAccessEnabled = false;
	private boolean discoveryGateAccessEnabled = false;

	private RefworksAuthTypeCodeType refworksAuthenticationType;
    private ShibbolethInfo shibbolethInfo;



	public String getDocDeliveryEmail() {
		return this.docDeliveryEmail;
	}

	public String getDocDeliveryMessage() {
		return this.docDeliveryMessage;
	}

	public String getAccountId() {
		return this.accountId;
	}

	public void setAccountId(String newAccountId) {
		this.accountId = newAccountId;
	}

	public String getDepartmentId() {
		return this.departmentId;
	}

	public String getDepartmentName() {
		return this.departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getAccountNumber() {
		return this.accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setDepartmentId(String newDepartmentId) {
		this.departmentId = newDepartmentId;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String newAccountName) {
		this.accountName = newAccountName;
	}

	public boolean isAthensEnabled() {
		return this.athensEnabled;
	}

	public String toString() {
        StringBuffer buffer = new StringBuffer(512);
        buffer.append("Account = [");
        buffer.append("AccountId=[");
        buffer.append(getAccountId());
        buffer.append("], DepartmentId=[");
        buffer.append(getDepartmentId());
        buffer.append("], AccountName=[");
        buffer.append(getAccountName());
        buffer.append("], DocDeliveryEmail=[");
        buffer.append(getDocDeliveryEmail());
        buffer.append("], DocDeliveryMessage=[");
        buffer.append(getDocDeliveryMessage());
        buffer.append("], IsAthensEnabled=[");
        buffer.append(isAthensEnabled());
        buffer.append("], AthensOrgId=[");
        buffer.append(getAthensOrgId());
        buffer.append("], AthensPUID=[");
        buffer.append(getAthensPUID());
        buffer.append("], RefworksServerBaseURL=[");
        buffer.append(getRefworksServerBaseURL());
        buffer.append("], RefworksAuthType=[");
        buffer.append(getRefworksAuthenticationType());
        buffer.append("], RefworksHighestLevelGroupId=[");
        buffer.append(getRwHighestLvlGroupId());
        buffer.append("], IsAllowedViewRWGroupId=[");
        buffer.append(isAllowViewRWGroupId());
        buffer.append("]]");
        return buffer.toString();
    }

	public void setDocDeliveryMessage(String docDeliveryMessage) {
		this.docDeliveryMessage = docDeliveryMessage;
	}

	public void setDocDeliveryEmail(String docDeliveryEmail) {
		this.docDeliveryEmail = docDeliveryEmail;
	}

	public void setAthensEnabled(boolean athensEnabled) {
		this.athensEnabled = athensEnabled;
	}

	public String getAthensOrgId() {
		return this.athensOrgId;
	}

	public void setAthensOrgId(String athensOrgId) {
		this.athensOrgId = athensOrgId;
	}

    public String getRwHighestLvlGroupId() {
        return rwHighestLvlGroupId;
    }

    public String getRefworksServerBaseURL() {
        return refworksServerBaseURL;
    }

    public void setRwHighestLvlGroupId(String string) {
        rwHighestLvlGroupId = string;
    }

    static public String parseRefworksServerBaseURL(String url) {
		String parsedURL = "";
		if (StringUtils.isNotBlank(url)) {
			if (url.length() > 1&& url.endsWith(CARSStringConstants.PATH_SEPARATOR.value())) {
				parsedURL = url.substring(0, url.length() - 1);
			} else {
				parsedURL = url;
			}
		}

		return parsedURL;
	}

    public void setRefworksServerBaseURL(String string) {
        refworksServerBaseURL = parseRefworksServerBaseURL(string);
    }

    public boolean isAllowViewRWGroupId() {
        return allowViewRWGroupId;
    }

    public void setAllowViewRWGroupId(boolean b) {
        allowViewRWGroupId = b;
    }

    public void setCrossFireAccessEnabled(boolean cfAccessEnabled) {
        crossFireAccessEnabled = cfAccessEnabled;
    }

    public boolean isCrossFireAccessEnabled() {
        return crossFireAccessEnabled;
    }

    public void setDiscoveryGateAccessEnabled(boolean dgAccessEnabled) {
        discoveryGateAccessEnabled = dgAccessEnabled;
    }

    public boolean isDiscoveryGateAccessEnabled() {
        return discoveryGateAccessEnabled;
    }

    public String getAthensPUID() {
        return athensPUID;
    }

    public void setAthensPUID(String string) {
        athensPUID = string;
    }

    public RefworksAuthTypeCodeType getRefworksAuthenticationType() {
        return refworksAuthenticationType;
    }

    public void setRefworksAuthenticationType(RefworksAuthTypeCodeType type) {
        refworksAuthenticationType = type;
    }

	public String getRefworksUsrLvlGroupId(){
		return refworksUsrLvlGroupId;
	}

	public String getRefworksUserName(){
		return refworksUserName;
	}

	public String getRefworksUserPassword(){
		return refworksUserPassword;
	}

	public void setRefworksUserName(String refworksUserName) {
        this.refworksUserName = refworksUserName;
    }

    public void setRefworksUserPassword(String refworksUserPassword) {
        this.refworksUserPassword = refworksUserPassword;
    }

    public void setRefworksUsrLvlGroupId(String refworksUsrLvlGroupId) {
        this.refworksUsrLvlGroupId = refworksUsrLvlGroupId;
    }

	public ShibbolethInfo getShibbolethInfo() {
		return shibbolethInfo;
	}

	public void setShibbolethInfo(ShibbolethInfo shibbolethInfo) {
		if (null != shibbolethInfo){
			this.shibbolethInfo = shibbolethInfo;
		}
	}

	public String getShibTargetedId(){
		return this.shibbolethInfo.getTargetId();
	}

	public String getShibProviderId(){
		return this.shibbolethInfo.getProviderID();
	}

	public String getShibEntitlements() {
		return this.shibbolethInfo.getEntitlements();
	}

    public static Account getAccountInfo(String IP) throws ServiceException {
        AuthenticationResponseStatusType status;
        CSWebService service = new CSWebServiceImpl();
        try {
            AuthenticateUserRespPayloadType payload = service.authenticateByIP(IP);
            status = payload.getStatus();

            if (AuthenticationStatusCodeType.OK.equals(status.getStatusCode())) {
                // If there is Path Choice info something is wrong in account setup!
                List<PathChoiceInfoType> paths = payload.getPathChoiceInfo();
                if (!paths.isEmpty()) {
                    log4j.error("Path Choice encountered trying to authenticate by IP: ");
                    for (PathChoiceInfoType choice : paths) {
                        log4j.error("Choice: " + choice.getPathChoiceNumber() + ", Description: " + choice.getDescription());
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
            }
        } catch (Exception e) {
            log4j.error("Unable to lookup Account by IP!", e);
        }

        return null;
    }

}
