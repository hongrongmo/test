package org.ei.service.cars.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ei.ane.fences.UserFencesHandler;
import org.ei.biz.personalization.EVWebUser;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.personalization.IEVWebUser.UserAnonymityType;
import org.ei.biz.personalization.cars.Account;
import org.ei.biz.personalization.cars.OpaqueInfo;
import org.ei.biz.personalization.cars.PathChoiceInfo;
import org.ei.biz.personalization.cars.QuickLinkInfo;
import org.ei.biz.personalization.cars.ScirusSourceInfo;
import org.ei.biz.personalization.cars.ShibbolethInfo;
import org.ei.domain.personalization.ProductInfo;
import org.ei.exception.ServiceException;
import org.ei.service.cars.CARSStringConstants;
import org.ei.service.cars.XPathEnum;
import org.ei.service.cars.rest.util.XMLUtil;
import org.ei.service.cars.security.authorization.UserAccessType;
import org.ei.service.cars.security.authorization.UserAccessTypeFactory;
import org.ei.session.UserBroker;

import com.elsevier.webservices.schemas.csas.constants.types.v7.AssociationType;
import com.elsevier.webservices.schemas.csas.constants.types.v7.RefworksAuthTypeCodeType;

public class UserDataProcessor {

	private final static Logger log4j = Logger.getLogger(UserDataProcessor.class);
	private static UserBroker uBroker = new UserBroker();

	@SuppressWarnings("unchecked")
	public static IEVWebUser processUserData(EVWebUser webUser, String usrMimeResp, String ssoKeyFromSession) throws ServiceException {

		String email = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.EMAIL.value());
		String firstName = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.FIRST_NAME.value());
		String lastName = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.LAST_NAME.value());

		if (StringUtils.isNotBlank(firstName)) {
			webUser.setFirstName(firstName);
		}
		if (StringUtils.isNotBlank(lastName)) {
			webUser.setLastName(lastName);
		}
		if (StringUtils.isNotBlank(email)) {
			webUser.setEmail(email);
		}

		// add path choice information to user object
		String pathChoiceInfo = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.PATH_CHOICE_INFO.value());

		if (null != pathChoiceInfo && pathChoiceInfo.length() > 0) {
			List<String> pathChoiceNum = (List<String>) XMLUtil.fetchXpathValueFromXML(usrMimeResp, XPathEnum.PATH_CHOICE_NUMBER.value(), true);
			List<String> pathChoiceDesc = (List<String>) XMLUtil.fetchXpathValueFromXML(usrMimeResp, XPathEnum.PATH_CHOICE_DESC.value(), true);

			if (null != pathChoiceNum && !pathChoiceNum.isEmpty() && null != pathChoiceDesc && !pathChoiceDesc.isEmpty()
					&& pathChoiceNum.size() == pathChoiceDesc.size()) {
				List<PathChoiceInfo> pathChoiceInfos = new ArrayList<PathChoiceInfo>();

				for (int i = 0; i < pathChoiceNum.size(); i++) {
					PathChoiceInfo choiceInfo = new PathChoiceInfo();
					choiceInfo.setPathChoiceNumber(pathChoiceNum.get(i));
					choiceInfo.setPathChoiceNumber(pathChoiceDesc.get(i));

					pathChoiceInfos.add(choiceInfo);
				}
				webUser.setPathChoiceInfo(pathChoiceInfos);
			}
		}

		// fetch the opaque information and populate to user object
		List<String> opqRespInfoName = (List<String>) XMLUtil.fetchXpathValueFromXML(usrMimeResp, XPathEnum.OPAQUE_INFO_NAME.value(), true);
		List<String> opqRespInfoValue = (List<String>) XMLUtil.fetchXpathValueFromXML(usrMimeResp, XPathEnum.OPAQUE_INFO_VALUE.value(), true);

		if (null != opqRespInfoName && !opqRespInfoName.isEmpty() && null != opqRespInfoValue && !opqRespInfoValue.isEmpty()
				&& opqRespInfoName.size() == opqRespInfoValue.size()) {
			List<OpaqueInfo> opaqueInfos = new ArrayList<OpaqueInfo>();

			for (int i = 0; i < opqRespInfoName.size(); i++) {
				OpaqueInfo opaqueInfo = new OpaqueInfo();
				opaqueInfo.setName(opqRespInfoName.get(i));
				opaqueInfo.setValue(opqRespInfoValue.get(i));
				opaqueInfos.add(opaqueInfo);
			}
			webUser.setOpaqueInfos(opaqueInfos);
		}
		// verify if this user needs to choose the dept for this session
		processDepartmentSelection(webUser);
		// TODO (Info not coming from CARS while reg)
		/*
		 * m_contactInfo.setCountry(""); m_contactInfo.setFax("");
		 * m_contactInfo.setPhone(""); m_contactInfo.setPostalCode("");
		 * m_contactInfo.setStateProvince("");
		 */

		String authToken = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.AUTH_TOKEN.value());
		if (StringUtils.isNotBlank(authToken)) {
			webUser.setAuthToken(authToken);
		}

		String webUserId = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.WEB_USER_ID.value());
		if (StringUtils.isNotBlank(webUserId)) {
			webUser.setWebUserId(webUserId);

		}

		String userId = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.USERID.value());
		if (StringUtils.isNotBlank(userId)) {
			// Compatibility with legacy EV, write user ID to username field!
			webUser.setUsername(userId);
		}

		String adminPrivileges = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.ADMIN_PRIVILEGE.value());
		if (StringUtils.isNotBlank(adminPrivileges)) {
			webUser.setAdminPrivileges(adminPrivileges);
		}

		String allowedRegType = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.ALLOWED_REG_TYPE.value());
		if (StringUtils.isNotBlank(allowedRegType)) {
			webUser.setAllowedRegType(allowedRegType);
		}

		String associationType = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.ASSOCIATION_TYPE.value());
		if (StringUtils.isNotBlank(associationType)) {
			webUser.setAssociationType(associationType);
		}

		String dataPrivileges = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.DATA_PRIVILEGE.value());
		if (StringUtils.isNotBlank(dataPrivileges)) {
			webUser.setDataPrivileges(dataPrivileges);
		}

		List<String> fenceIds = (List<String>) XMLUtil.fetchXpathValueFromXML(usrMimeResp, XPathEnum.FENCE_IDS.value(), true);
		if (null != fenceIds && fenceIds.size() > 0) {
			//
			// Set fences data for current user
			//
			webUser.setFenceIds(fenceIds);
			UserFencesHandler.processUserFences(webUser);
		}

		if (null == webUser.getProductInfo()) {
			webUser.setProductInfo(new ProductInfo());
		}

		setUserProductInfo(webUser, usrMimeResp);

		String pathChoice = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.PATH_CHOICE.value());
		if (StringUtils.isNotBlank(pathChoice)) {
			webUser.setPathChoice(pathChoice);
		}

		boolean pathChoiceExists = XMLUtil.fetchXPathValAsBoolean(usrMimeResp, XPathEnum.PATH_CHOICE_EXISTS.value());
		webUser.setPathChoiceExists(pathChoiceExists);

		String profilePhotoURL = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.PROFILE_PHOTO_URL.value());
		if (StringUtils.isNotBlank(profilePhotoURL)) {
			webUser.setProfilePhotoURL(profilePhotoURL);
		}

		String ssoKey = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.SSO_KEY.value());
		if (StringUtils.isNotBlank(ssoKey)) {
			webUser.setSsoKey(ssoKey);
		} else {
			webUser.setSsoKey(ssoKeyFromSession);
		}

		List<String> ssoURLs = (List<String>) XMLUtil.fetchXpathValueFromXML(usrMimeResp, XPathEnum.SSO_URL.value(), true);
		if (null != ssoURLs && ssoURLs.size() > 0) {
			webUser.setSsoURLs(ssoURLs);
			webUser.setSSOURLInvoked(true);
		}

		String usagePathInfo = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.USAGE_PATH_INFO.value());
		if (StringUtils.isNotBlank(usagePathInfo)) {
			webUser.setUsagePathInfo(usagePathInfo);
		}

		String userAnonymity = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.USER_ANONYMITY.value());
		if (StringUtils.isNotBlank(userAnonymity)) {
			webUser.setUserAnonymity(userAnonymity);
		}

		if (null != webUser.getAssociationType() && null != webUser.getUserAnonymity()) {
			webUser.setAccessTypeString(calculateUserAccessType(getAssociationTypeValue(webUser), getUserAnonymityTypeValue(webUser)));
		} else {
			log4j.warn("Unknown user access type found for user id " + webUser.getWebUserId());
		}

		if (null != webUser.getAccessTypeString()) {
			webUser.setAccessType(UserAccessTypeFactory.getInstance().getUserAccessType(webUser.getAccessTypeString()));

			if (null == webUser.getAccessType()) {
				log4j.warn("Unknown user access type '" + webUser.getAccessTypeString() + "' found for user id " + webUser.getWebUserId());
			}
		}

		boolean athensFence = XMLUtil.fetchXPathValAsBoolean(usrMimeResp, XPathEnum.ATHENS_FENCE.value());
		webUser.setAthensFenceEnabled(athensFence);

		boolean shibFence = XMLUtil.fetchXPathValAsBoolean(usrMimeResp, XPathEnum.SHIBBOLETH_FENCE.value());
		webUser.setShibbolethFenceEnabled(shibFence);

		boolean remeberPath = XMLUtil.fetchXPathValAsBoolean(usrMimeResp, XPathEnum.REMEMBER_PATH_FLAG.value());
		webUser.setRememberPathFlagEnabled(remeberPath);

		String developerFlag = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.DEVELOPER_FLAG.value());
		if (StringUtils.isNotBlank(developerFlag)) {
			if (CARSStringConstants.NO.value().equals(developerFlag)) {
				webUser.setDeveloperFlagEnabled(false);
			} else {
				webUser.setDeveloperFlagEnabled(true);
			}
		}
		boolean passwordResetReq = XMLUtil.fetchXPathValAsBoolean(usrMimeResp, XPathEnum.USER_PASSWORD_RESET.value());
		webUser.setPasswordResetFlagEnabled(passwordResetReq);

		if (CARSCommonUtil.isUserInfoAvailable(usrMimeResp)) {
			webUser.setAccount(processAccountData(usrMimeResp));
		}

		boolean enableXtrnlSubscribedEntitlements = XMLUtil.fetchXPathValAsBoolean(usrMimeResp, XPathEnum.EXTERNAL_SUBSCRIBED_ENT.value());
		webUser.setEnableXtrnlSubscribedEntitlements(enableXtrnlSubscribedEntitlements);

		boolean markSubUnsubJournals = XMLUtil.fetchXPathValAsBoolean(usrMimeResp, XPathEnum.MARK_SUB_UNSUB_JOURNAL.value());
		webUser.setMarkSubUnsubJournals(markSubUnsubJournals);

		String quickLinkInfo = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.QUICK_LINK_INFO.value());
		if (StringUtils.isNotBlank(quickLinkInfo)) {
			QuickLinkInfo quInfo = new QuickLinkInfo();

			quInfo.setLinkLabel(XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.LINK_LABEL.value()));
			quInfo.setLinkMouseOverText(XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.LINK_MOUSEOVER_TEXT.value()));
			quInfo.setLinkURL(XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.LINK_URL.value()));

			webUser.setQuickLinkInfo(quInfo);
		}

		String scirusSourceInfo = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.SCIRUS_SOURCE_INFO.value());
		if (StringUtils.isNotBlank(scirusSourceInfo)) {
			ScirusSourceInfo sourceInfo = new ScirusSourceInfo();
			sourceInfo.setTabText(XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.TAB_TEXT.value()));
			List<String> abbreviations = (List<String>) XMLUtil.fetchXpathValueFromXML(usrMimeResp, XPathEnum.ABBREVIATION.value(), true);
			if (null != abbreviations && abbreviations.size() > 0) {
				sourceInfo.setAbbreviation(abbreviations);
			}

			webUser.setScirusSourceInfo(sourceInfo);
		}

		boolean userAgrmntAccepted = XMLUtil.fetchXPathValAsBoolean(usrMimeResp, XPathEnum.USER_AGRMNT_ACCEPTED.value());
		if (userAgrmntAccepted) {
			webUser.setUserAgreementAccepted(userAgrmntAccepted);
		}

		boolean webProdAdmin = XMLUtil.fetchXPathValAsBoolean(usrMimeResp, XPathEnum.WEB_PROD_ADMIN.value());
		if (webProdAdmin) {
			webUser.setWebProdAdmin(webProdAdmin);
		}

		String credType = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.CRED_TYPE.value());
		if (StringUtils.isNotBlank(credType)) {
			webUser.setCredType(credType);
		}

		String accountId = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.ACCOUNT_NUMBER.value());
		if (StringUtils.isNotBlank(accountId)) {
			// Compatibility with legacy EV, write accountId to CustomerID
			// field!
			webUser.setCustomerID(accountId);
		}

		return webUser;
	}

	private static void setUserProductInfo(EVWebUser webUser, String usrMimeResp) throws ServiceException {
		String productId = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.PRODUCT_ID.value());
		if (StringUtils.isNotBlank(productId)) {
			webUser.getProductInfo().setProductId(productId);
		}

		String allListId = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.ALL_LIST_ID.value());
		if (StringUtils.isNotBlank(allListId)) {
			webUser.getProductInfo().setAllListId(allListId);
		}

		String allListUpdateDate = XMLUtil.fetchXPathValAsString(usrMimeResp, XPathEnum.ALL_LIST_UPDATE_DATE.value());
		if (StringUtils.isNotBlank(allListUpdateDate)) {
			webUser.getProductInfo().setAllListUpdateDate(allListUpdateDate);
		}
	}

	private static Account processAccountData(String userMimeResp) throws ServiceException {

		Account account = new Account();
		account.setAccountName(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.ACCOUNT_NAME.value()));
		account.setAccountId(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.ACCOUNT_ID.value()));
		account.setAccountNumber(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.ACCOUNT_NUMBER.value()));

		account.setDepartmentId(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.DEPARTMENT_ID.value()));
		account.setDepartmentName(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.DEPARTMENT_NAME.value()));

		// start TODO - Not coming from CARS mime. Need to do after CARS give
		// response
		account.setDocDeliveryMessage(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.DOC_DELIVERY_MESSAGE.value()));
		account.setDocDeliveryEmail(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.DOC_DELIVERY_EMAIL.value()));
		account.setAthensOrgId("");
		account.setAthensPUID("");
		// end TODO

		account.setAthensEnabled(XMLUtil.fetchXPathValAsBoolean(userMimeResp, XPathEnum.ATHENS_ENABLED_FLAG.value()));
		account.setCrossFireAccessEnabled(XMLUtil.fetchXPathValAsBoolean(userMimeResp, XPathEnum.CROSSFIRE_ACCESS_ENABLED.value()));
		account.setDiscoveryGateAccessEnabled(XMLUtil.fetchXPathValAsBoolean(userMimeResp, XPathEnum.DISCOVERY_GATE_ACCESS_ENABLED.value()));

		String refworkData = XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.RFW_DATA.value());

		if (StringUtils.isNotBlank(refworkData)) {
			account.setRwHighestLvlGroupId(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.RFW_HIGHLEVEL_GRP_ID.value()));
			account.setRefworksServerBaseURL(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.RFW_SRV_SETTING.value()));
			account.setAllowViewRWGroupId(XMLUtil.fetchXPathValAsBoolean(userMimeResp, XPathEnum.RFW_ALLOW_VIEW_GRP_ID.value()));

			String refworkUsrData = XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.RFW_USER_DATA.value());

			if (StringUtils.isNotBlank(refworkUsrData)) {
				account.setRefworksUsrLvlGroupId(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.RFW_USR_LVL_GRP_ID.value()));
				account.setRefworksUserName(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.RFW_USER_NAME.value()));
				account.setRefworksUserPassword(XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.RFW_USER_PASSWORD.value()));
				String rfwAuthType = XMLUtil.fetchXPathValAsString(userMimeResp, XPathEnum.RFW_AUTH_TYPE.value());
				if (StringUtils.isNotBlank(rfwAuthType)) {
					account.setRefworksAuthenticationType(RefworksAuthTypeCodeType.fromValue(rfwAuthType));
				}
			}

			ShibbolethInfo shibbolethInfo = new ShibbolethInfo();
			shibbolethInfo.setProviderID("");
			shibbolethInfo.setEntitlements("");
			shibbolethInfo.setTargetedId("");
			account.setShibbolethInfo(shibbolethInfo);
		}

		return account;
	}

	private static UserAnonymityType getUserAnonymityTypeValue(EVWebUser webUser) {
		return UserAnonymityType.fromValue(webUser.getUserAnonymity());
	}

	private static AssociationType getAssociationTypeValue(EVWebUser webUser) {
		return AssociationType.fromValue(webUser.getAssociationType());
	}

	public static String calculateUserAccessType(AssociationType assocType, UserAnonymityType userAnon) {

		UserAccessType accessType = null;

		if (null != userAnon && UserAnonymityType.INDIVIDUAL.value().equals(userAnon.value())) {
			if (null != assocType && null != assocType.value()) {
				if (AssociationType.GUEST.value().equals(assocType.value())) {
					accessType = UserAccessType.GUEST;
				} else if (AssociationType.CUSTOMER_SERVICE.value().equals(assocType.value())) {
					accessType = UserAccessType.INTERNAL;
				} else if (AssociationType.ADMIN_TOOL.value().equals(assocType.value())) {
					accessType = UserAccessType.ADMINTOOL;
				} else if (AssociationType.BULK.value().equals(assocType.value())) {
					accessType = UserAccessType.BULKREG;
				} else if (AssociationType.ACTIVATION_CODE.value().equals(assocType.value())) {
					accessType = UserAccessType.ACTIVREG;
				} else if (AssociationType.SHIBBOLETH.value().equals(assocType.value())) {
					accessType = UserAccessType.SHIBBOLETHREG;
				} else if (AssociationType.ONLINE_REGISTERED.value().equals(assocType.value())) {
					accessType = UserAccessType.IPPROFILE;
				} else if (AssociationType.TICURL.value().equals(assocType.value())) {
					accessType = UserAccessType.TICURL;
				} else if (AssociationType.SELF_MANAGED.value().equals(assocType.value())) {
					accessType = UserAccessType.SELF_MANAGED;
				}
			}
		} else if (null != userAnon && UserAnonymityType.ANON_SHIBBOLETH.value().equals(userAnon.value())) {
			accessType = UserAccessType.SHIBBOLETHANON;
		} else if (null != userAnon && UserAnonymityType.ANON_IP.value().equals(userAnon.value())) {
			accessType = UserAccessType.IPRANGE;
		} else if (null != userAnon && UserAnonymityType.ANON_GUEST.value().equals(userAnon.value())) {
			accessType = UserAccessType.GUEST;
		} else if (null != userAnon && UserAnonymityType.ANON_TICURL.value().equals(userAnon.value())) {
			accessType = UserAccessType.TICURL;
		}
		return (null != accessType) ? accessType.getName() : null;
	}

	private static void processDepartmentSelection(EVWebUser webUser) {
		List<PathChoiceInfo> pathChoiceList = webUser.getPathChoiceInfo();

		if (pathChoiceList != null && pathChoiceList.size() > 0) {
			webUser.setDeptSelectionRequired(true);
		}

	}

}
