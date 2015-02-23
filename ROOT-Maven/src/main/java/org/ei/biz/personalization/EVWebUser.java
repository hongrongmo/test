package org.ei.biz.personalization;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.personalization.cars.Account;
import org.ei.biz.personalization.cars.OpaqueInfo;
import org.ei.biz.personalization.cars.PathChoiceInfo;
import org.ei.biz.personalization.cars.QuickLinkInfo;
import org.ei.biz.personalization.cars.ScirusSourceInfo;
import org.ei.biz.security.SecurityAttribute;
import org.ei.books.collections.ReferexCollection;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.personalization.ProductInfo;
import org.ei.exception.InfrastructureException;
import org.ei.service.cars.security.authorization.UserAccessType;
import org.ei.session.CartridgeBuilder;
import org.ei.session.EntryToken;
import org.ei.session.UserPreferences;

import com.elsevier.edit.common.access.framework.IUserAuthentication;

public class EVWebUser implements IEVWebUser, Serializable {

    private static final long serialVersionUID = -5679318060607730450L;

    private transient Logger log4j = Logger.getLogger(EVWebUser.class);

    public static String USERNAME_IP_AUTH = "_IP";
    public static String USERNAME_AUTH_FAIL = "_FAIL";
    public static String USERNAME_INDIV_AUTH_FAIL = "_UN_FAIL";

    private String customerID = "-";
    private String username = "-";
    private String companyName = "-";
    private String contractID = "0";
    private String[] cartridge;
    private String startPage = "-";
    private String defaultDB = "-";
    private String refEmail = "-";
    private String ipAddress = "-";
    private EntryToken entryToken;
    private String firstName;
    private String lastName;

    // should be the profile id from the user info
    // private String userId;

    private List<PathChoiceInfo> pathChoiceInfo;
    private List<OpaqueInfo> opaqueInfos;
    private boolean isDeptSelectionRequired = false;
    private String authToken;
    private String webUserId;
    private String adminPrivileges;
    private String allowedRegType;
    private String associationType;
    private String dataPrivileges;
    private List<String> fenceIds = new ArrayList<String>();
    private String productId;
    private String pathChoice;
    private String profilePhotoURL;
    private String ssoKey;
    private List<String> ssoURLs = new ArrayList<String>();
    private String usagePathInfo;
    private String userAnonymity;
    private String accessTypeString;
    private String credType;
    private boolean isPathChoiceExists = false;
    private boolean isAthensFenceEnabled = false;
    private boolean isShibbolethFenceEnabled = false;
    private boolean isRememberPathFlagEnabled = false;
    private boolean isDeveloperFlagEnabled = false;
    private boolean isPasswordResetFlagEnabled = false;
    private boolean isEnableXtrnlSubscribedEntitlements = false;
    private boolean isMarkSubUnsubJournals = false;
    private boolean isUserAgreementAccepted = false;
    private boolean isWebProdAdmin = false;
    private String carsJSessionId;

    private UserAccessType accessType;
    private QuickLinkInfo quickLinkInfo;
    private ScirusSourceInfo scirusSourceInfo;
    private Account account;
    private UserPreferences userPreferences = new UserPreferences();
    private UserProfile contactInfo = new UserProfile();
    private ProductInfo productInfo;
    private boolean SSOURLInvoked = false;

    private UserPrefs userPrefs;

    public boolean isSSOURLInvoked() {
        return SSOURLInvoked;
    }

    public void setSSOURLInvoked(boolean isSSOURLInvoked) {
        this.SSOURLInvoked = isSSOURLInvoked;
    }

    public EVWebUser() {
        // Default Constructor.
    }

    public EVWebUser(String userString) {

        // Tokenize the string pairs
        StringTokenizer pairs = new StringTokenizer(userString, ":");
        while (pairs.hasMoreTokens()) {
            String token = pairs.nextToken();
            String kv[] = token.split("=");
            if (kv == null || kv.length < 2) {
                continue;
            }
            if ("C".equals(kv[0])) {
                this.customerID = kv[1];
            } else if ("U".equals(kv[0])) {
                this.username = kv[1];
            } else if ("N".equals(kv[0])) {
                this.companyName = kv[1];
            } else if ("D".equals(kv[0])) {
                // Cartridge string - first see if REFEREX should be appended
                String temp = kv[1];
                if (ReferexCollection.ALLCOLS_PATTERN.matcher(temp).find()) {
                    if (!Pattern.compile("PAG").matcher(temp).find()) {
                        temp = temp.concat(";PAG");
                    }
                }
                this.cartridge = temp.split(";");
            } else if ("CT".equals(kv[0])) {
                this.contractID = kv[1];
            } else if ("SP".equals(kv[0])) {
                this.startPage = kv[1];
            } else if ("DB".equals(kv[0])) {
                this.defaultDB = kv[1];
            } else if ("E".equals(kv[0])) {
                try {
                    this.refEmail = URLDecoder.decode(kv[1], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    this.log4j.error("Unable to decode email: '" + kv[1] + "'");
                    this.refEmail = "-";
                }
            } else if ("IP".equals(kv[0])) {
                this.ipAddress = kv[1];
            } else if ("UID".equals(kv[0])) {
                this.getUserInfo().setUserId(kv[1]);
                // this.userId = kv[1];
            } else if ("WUID".equals(kv[0])) {
                this.webUserId = kv[1];
            }

        }
        // this.m_userPreferences = new UserPreferences(this.cartridge);

        //
        // Build UserInfo from userid
        //
        if (!GenericValidator.isBlankOrNull(this.getUserId())) {
            try {
                this.contactInfo = new PersonalAccount().getUserProfile(this.getUserId());
            } catch (InfrastructureException e) {
                this.log4j.error("Unable to retrieve UserProfile information!", e);
            }
        }
    }

    /*
     * Return the default database int from current fence settings
     */
    public int getDefaultDB(int entitlementsmask) {
        int idatabase = 0;
        UserPreferences userPreferencs = this.getUserPreferences();
        if (userPreferencs == null) {
            log4j.warn("No user preferences!");
            return idatabase;
        }

        // To turn on a "default" database, the fence must be on ***AND*** user
        // must have an entitlement to it!
        if (userPreferencs.getBoolean(DatabaseConfig.CPX_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) {
            idatabase |= DatabaseConfig.CPX_MASK;
        }
        if (userPreferencs.getBoolean(DatabaseConfig.INS_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) {
            idatabase |= DatabaseConfig.INS_MASK;
        }
        if (userPreferencs.getBoolean(DatabaseConfig.NTI_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.NTI_MASK) == DatabaseConfig.NTI_MASK) {
            idatabase |= DatabaseConfig.NTI_MASK;
        }
        if (userPreferencs.getBoolean(DatabaseConfig.PCH_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.PCH_MASK) == DatabaseConfig.PCH_MASK) {
            idatabase |= DatabaseConfig.PCH_MASK;
        }
        if (userPreferencs.getBoolean(DatabaseConfig.CHM_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.CHM_MASK) == DatabaseConfig.CHM_MASK) {
            idatabase |= DatabaseConfig.CHM_MASK;
        }
        if (userPreferencs.getBoolean(DatabaseConfig.CBN_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.CBN_MASK) == DatabaseConfig.CBN_MASK) {
            idatabase |= DatabaseConfig.CBN_MASK;
        }
        if (userPreferencs.getBoolean(DatabaseConfig.ELT_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.ELT_MASK) == DatabaseConfig.ELT_MASK) {
            idatabase |= DatabaseConfig.ELT_MASK;
        }
        if (userPreferencs.getBoolean(DatabaseConfig.EPT_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.EPT_MASK) == DatabaseConfig.EPT_MASK) {
            idatabase |= DatabaseConfig.EPT_MASK;
        }
        if (userPreferencs.getBoolean(DatabaseConfig.GEO_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK) {
            idatabase |= DatabaseConfig.GEO_MASK;
        }
        if (userPreferencs.getBoolean(DatabaseConfig.GRF_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.GRF_MASK) == DatabaseConfig.GRF_MASK) {
            idatabase |= DatabaseConfig.GRF_MASK;
        }
        if (userPreferencs.getBoolean(DatabaseConfig.UPA_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.UPA_MASK) == DatabaseConfig.UPA_MASK) {
            idatabase |= DatabaseConfig.UPA_MASK;
        }
        if (userPreferencs.getBoolean(DatabaseConfig.EUP_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.EUP_MASK) == DatabaseConfig.EUP_MASK) {
            idatabase |= DatabaseConfig.EUP_MASK;
        }

        // Add Referex
        if (userPreferencs.getBoolean(UserPreferences.FENCE_EBOOK)) {
            String keys = getCartridgeString();
            if (ReferexCollection.ALLCOLS_PATTERN.matcher(keys).find()) {
                idatabase |= DatabaseConfig.PAG_MASK;
            }
        }
        return idatabase;
    }

    public List<String> getDefaultDBAsList(int entitlementsmask) {

        List<String> defaultDB = new ArrayList<String>();
        UserPreferences userPreferencs = this.getUserPreferences();

        if (userPreferencs == null) {
            log4j.warn("No user preferences!");
            return defaultDB;
        }

        // To turn on a "default" database, the fence must be on ***AND*** user
        // must have an entitlement to it!
        if (userPreferencs.getBoolean(DatabaseConfig.CPX_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.CPX_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.CBF_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.CBF_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.INS_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.INS_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.IBS_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.IBF_MASK) == DatabaseConfig.IBF_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.IBF_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.NTI_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.NTI_MASK) == DatabaseConfig.NTI_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.NTI_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.PCH_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.PCH_MASK) == DatabaseConfig.PCH_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.PCH_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.CHM_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.CHM_MASK) == DatabaseConfig.CHM_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.CHM_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.CBN_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.CBN_MASK) == DatabaseConfig.CBN_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.CBN_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.ELT_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.ELT_MASK) == DatabaseConfig.ELT_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.ELT_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.EPT_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.EPT_MASK) == DatabaseConfig.EPT_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.EPT_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.GEO_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.GEO_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.GRF_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.GRF_MASK) == DatabaseConfig.GRF_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.GRF_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.UPA_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.UPA_MASK) == DatabaseConfig.UPA_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.UPA_MASK));
        }
        if (userPreferencs.getBoolean(DatabaseConfig.EUP_PREF.toUpperCase()) && (entitlementsmask & DatabaseConfig.EUP_MASK) == DatabaseConfig.EUP_MASK) {
            defaultDB.add(Integer.toString(DatabaseConfig.EUP_MASK));
        }

        // Add Referex
        if (userPreferencs.getBoolean(UserPreferences.FENCE_EBOOK)) {
            String keys = getCartridgeString();
            if (ReferexCollection.ALLCOLS_PATTERN.matcher(keys).find()) {
                defaultDB.add(Integer.toString(DatabaseConfig.PAG_MASK));
            }
        }
        return defaultDB;
    }

    public UserAccessType getAccessType() {
        return this.accessType;
    }

    public String getAccessTypeString() {
        return this.accessTypeString;
    }

    public Account getAccount() {
        return this.account;
    }

    //
    // Overrides for IEVWebUser interface
    //
    @Override
    public String getAdminPrivileges() {
        return this.adminPrivileges;
    }

    @Override
    public String getAllListLastUpdateDate() {

        return null;
    }

    @Override
    public String getAllowedRegType() {
        return this.allowedRegType;
    }

    public String getAssociationType() {
        return this.associationType;
    }

    @Override
    public String getAthensFence() {

        return null;
    }

    @Override
    public String getAuthToken() {
        return this.authToken;
    }

    public String getBulkPassword() {

        return null;
    }

    @Override
    public String getCarsCookieKey() {

        return null;
    }

    @Override
    public String[] getCartridge() {
        return this.cartridge;
    }

    @Override
    public String getCartridgeString() {
        return CartridgeBuilder.toString(this.cartridge);
    }

    @Override
    public String getContractID() {
        return this.contractID;
    }

    @Override
    public String getCredType() {
        return this.credType;
    }

    @Override
    public String getCustomerID() {
        return this.customerID;
    }

    public String getDataPrivileges() {
        return this.dataPrivileges;
    }

    /*
     * public void setRefEmail(String refEmail) { this.refEmail = refEmail; }
     * 
     * public String getRefEmail() { return this.refEmail; }
     */

    @Override
    public String getDefaultDB() {
        return this.defaultDB;
    }

    @Override
    public String getDepartmentName() {

        return null;
    }

    @Override
    public String getEmail() {
        return this.refEmail;
    }

    @Override
    public EntryToken getEntryToken() {
        return this.entryToken;
    }

    @Override
    public List<String> getFenceIds() {
        return this.fenceIds;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public long getLastSuccessfulCarsAccessTime() {

        return 0;
    }

    public List<OpaqueInfo> getOpaqueInfos() {
        return this.opaqueInfos;
    }

    @Override
    public String getPathChoice() {
        return this.pathChoice;
    }

    public List<PathChoiceInfo> getPathChoiceInfo() {
        return this.pathChoiceInfo;
    }

    @Override
    public boolean getPreference(String key) {
        if (this.userPreferences == null) {
            this.log4j.warn("UserPreferences object has not been created!");
            return false;
        }
        return this.userPreferences.getBoolean(key);
    }

    public String getProductId() {
        return this.productId;
    }

    @Override
    public String getProfileId() {

        return null;
    }

    public String getProfilePhotoURL() {
        return this.profilePhotoURL;
    }

    public QuickLinkInfo getQuickLinkInfo() {
        return this.quickLinkInfo;
    }

    @Override
    public String getRememberPathFlag() {
        return null;
    }

    public ScirusSourceInfo getScirusSourceInfo() {
        return this.scirusSourceInfo;
    }

    @Override
    public SecurityAttribute[] getSecurityAttributes() {
        UserAccessType type = getAccessType();

        if (type == null) {
            return new SecurityAttribute[0];
        }

        List<SecurityAttribute> attributes = new ArrayList<SecurityAttribute>(5);

        if (UserAccessType.IPRANGE.equals(type) || UserAccessType.TICURL.equals(type)) {
            attributes.add(SecurityAttribute.ANONYMOUS);
        }

        if (UserAccessType.ATHENS.equals(type)) {
            attributes.add(SecurityAttribute.ATHENS);
        }

        if (UserAccessType.SHIBBOLETHANON.equals(type)) {
            attributes.add(SecurityAttribute.ANONYMOUS);
        }
        if (UserAccessType.SHIBBOLETHREG.equals(type)) {
            attributes.add(SecurityAttribute.SHIBBOLETH);
        }
        if (UserAccessType.GUEST.equals(type)) {
            attributes.add(SecurityAttribute.GUEST);
        }

        if (UserAccessType.BULKPEND.equals(type) || UserAccessType.IPRANGE.equals(type) || UserAccessType.ATHENSPEND.equals(type)
            || UserAccessType.SHIBBOLETHANON.equals(type) || UserAccessType.SHIBBOLETHPEND.equals(type) || UserAccessType.GUESTREG.equals(type)) {
            attributes.add(SecurityAttribute.CANREGISTER);
        }

        if (UserAccessType.isIndividualAuthType(type)) {
            attributes.add(SecurityAttribute.INDIVIDUAL);
        }

        if (attributes.size() > 0) {
            SecurityAttribute[] securityAttributes = new SecurityAttribute[attributes.size()];
            int attributeSize = attributes.size();
            for (int i = 0; i < attributeSize; i++) {
                securityAttributes[i] = attributes.get(i);
            }
            return securityAttributes;
        }
        return new SecurityAttribute[0];
    }

    @Override
    public String getShibbotlethFence() {

        return null;
    }

    @Override
    public String getSsoKey() {
        return this.ssoKey;
    }

    public List<String> getSsoURLs() {
        return this.ssoURLs;
    }

    @Override
    public String getStartPage() {
        return this.startPage;
    }

    @Override
    public String getUsagePathInfo() {
        return this.usagePathInfo;
    }

    @Override
    public String getUserAccess() {

        return null;
    }

    @Override
    public String getUserAnonymity() {
        return this.userAnonymity;
    }

    public IUserAuthentication getUserAuthInfo() {
        return null;
    }

    /**
     * The userid should be the legacy user profile id that the system uses for personalization. WebUserId is used for all other aspects of the user id
     */
    @Override
    public String getUserId() {
        return this.getUserInfo().getUserId();
    }

    @Override
    public UserProfile getUserInfo() {
        return this.contactInfo;
    }

    @Override
    public int getUsermask() {
        int mask = 0;
        if (this.cartridge != null && this.cartridge.length > 0) {
            mask = DatabaseConfig.getInstance().getMask(this.cartridge);
        }
        return mask;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getUserNameFL() {

        return null;
    }

    @Override
    public String getUserNameLF() {

        return null;
    }

    @Override
    public UserPreferences getUserPreferences() {
        return this.userPreferences;
    }

    @Override
    public String getUserPrivileges() {

        return null;
    }

    public String getWebUserId() {
        return this.webUserId;
    }

    public boolean isAthensFenceEnabled() {
        return this.isAthensFenceEnabled;
    }

    @Override
    public boolean isCustomer() {
        return !GenericValidator.isBlankOrNull(this.webUserId);
    }

    public boolean isDeptSelectionRequired() {
        return this.isDeptSelectionRequired;
    }

    public boolean isDeveloperFlagEnabled() {
        return this.isDeveloperFlagEnabled;
    }

    public boolean isEnableXtrnlSubscribedEntitlements() {
        return this.isEnableXtrnlSubscribedEntitlements;
    }

    @Override
    public boolean isIndividuallyAuthenticated() {
        String userType = this.getUserAnonymity();
        return (userType != null && SecurityAttribute.INDIVIDUAL.getValue().toLowerCase().equals(userType.toLowerCase()));
    }

    /**
     * for jsps
     * 
     * @return
     */
    public boolean getIndividuallyAuthenticated() {
        return isIndividuallyAuthenticated();
    }

    public boolean isMarkSubUnsubJournals() {
        return this.isMarkSubUnsubJournals;
    }

    public boolean isPasswordResetFlagEnabled() {
        return this.isPasswordResetFlagEnabled;
    }

    public boolean isPasswordResetRequired() {

        return false;
    }

    public boolean isPathChoiceExists() {
        return this.isPathChoiceExists;
    }

    public boolean isRememberPathFlagEnabled() {
        return this.isRememberPathFlagEnabled;
    }

    @Override
    public boolean isScopusAccessEnabled() {
        return false;
    }

    public boolean isShibbolethFenceEnabled() {
        return this.isShibbolethFenceEnabled;
    }

    public boolean isUserAgreementAccepted() {
        return this.isUserAgreementAccepted;
    }

    public boolean isWebProdAdmin() {
        return this.isWebProdAdmin;
    }

    public void setAccessType(UserAccessType accessType) {
        this.accessType = accessType;
    }

    public void setAccessTypeString(String accessTypeString) {
        this.accessTypeString = accessTypeString;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public void setAdminPrivileges(String adminPrivileges) {
        this.adminPrivileges = adminPrivileges;
    }

    @Override
    public void setAllListLastUpdateDate(String allListLastUpdateDate) {

    }

    @Override
    public void setAllowedRegType(String allowedRegType) {
        this.allowedRegType = allowedRegType;
    }

    public void setAssociationType(String associationType) {
        this.associationType = associationType;
    }

    @Override
    public void setAthensFence(String athensFence) {

    }

    public void setAthensFenceEnabled(boolean isAthensFenceEnabled) {
        this.isAthensFenceEnabled = isAthensFenceEnabled;
    }

    @Override
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public void setCarsCookieKey(String carsCookieKey) {

    }

    @Override
    public void setCartridge(String[] cartridge) {
        this.cartridge = cartridge;
    }

    @Override
    public void setContractID(String contractID) {
        this.contractID = contractID;
    }

    @Override
    public void setCredType(String credType) {
        this.credType = credType;
    }

    @Override
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setDataPrivileges(String dataPrivileges) {
        this.dataPrivileges = dataPrivileges;
    }

    @Override
    public void setDefaultDB(String defaultDB) {
        this.defaultDB = defaultDB;
    }

    @Override
    public void setDepartmentName(String departmentName) {

    }

    public void setDeptSelectionRequired(boolean isDeptSelectionRequired) {
        this.isDeptSelectionRequired = isDeptSelectionRequired;
    }

    public void setDeveloperFlagEnabled(boolean isDeveloperFlagEnabled) {
        this.isDeveloperFlagEnabled = isDeveloperFlagEnabled;
    }

    @Override
    public void setEmail(String email) {
        this.refEmail = email;

    }

    public void setEnableXtrnlSubscribedEntitlements(boolean isEnableXtrnlSubscribedEntitlements) {
        this.isEnableXtrnlSubscribedEntitlements = isEnableXtrnlSubscribedEntitlements;
    }

    @Override
    public void setEntryToken(EntryToken entryToken) {
        this.entryToken = entryToken;
    }

    @Override
    public void setFeature(String responseMime1) {

    }

    @Override
    public void setFenceIds(List<String> fenceIds) {
        this.fenceIds = fenceIds;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public void setLastSuccessfulCarsAccessTime(long lastSuccessCarsAccessTime) {

    }

    public void setMarkSubUnsubJournals(boolean isMarkSubUnsubJournals) {
        this.isMarkSubUnsubJournals = isMarkSubUnsubJournals;
    }

    public void setOpaqueInfos(List<OpaqueInfo> opaqueInfos) {
        this.opaqueInfos = opaqueInfos;
    }

    public void setPasswordResetFlagEnabled(boolean isPasswordResetFlagEnabled) {
        this.isPasswordResetFlagEnabled = isPasswordResetFlagEnabled;
    }

    @Override
    public void setPathChoice(String pathChoice) {
        this.pathChoice = pathChoice;
    }

    public void setPathChoiceExists(boolean isPathChoiceExists) {
        this.isPathChoiceExists = isPathChoiceExists;
    }

    public void setPathChoiceInfo(List<PathChoiceInfo> pathChoiceInfo) {
        this.pathChoiceInfo = pathChoiceInfo;
    }

    @Override
    public void setPreference(String key, boolean value) {
        if (this.userPreferences == null) {
            throw new IllegalStateException("UserPreferences object has not been created!");
        }
        this.userPreferences.setBoolean(key, value);
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public void setProfileId(String profileId) {

    }

    public void setProfilePhotoURL(String profilePhotoURL) {
        this.profilePhotoURL = profilePhotoURL;
    }

    public void setQuickLinkInfo(QuickLinkInfo quickLinkInfo) {
        this.quickLinkInfo = quickLinkInfo;
    }

    @Override
    public void setRememberPathFlag(String pathFlag) {

    }

    public void setRememberPathFlagEnabled(boolean isRememberPathFlagEnabled) {
        this.isRememberPathFlagEnabled = isRememberPathFlagEnabled;
    }

    public void setScirusSourceInfo(ScirusSourceInfo scirusSourceInfo) {
        this.scirusSourceInfo = scirusSourceInfo;
    }

    @Override
    public void setScopusAccessEnabled(boolean isScopusAccessEnabled) {

    }

    public void setShibbolethFenceEnabled(boolean isShibbolethFenceEnabled) {
        this.isShibbolethFenceEnabled = isShibbolethFenceEnabled;
    }

    @Override
    public void setShibbotlethFence(String shibbolethFence) {

    }

    @Override
    public void setSsoKey(String ssoKey) {
        this.ssoKey = ssoKey;
    }

    public void setSsoURLs(List<String> ssoURLs) {
        this.ssoURLs = ssoURLs;
    }

    @Override
    public void setStartPage(String startPage) {
        this.startPage = startPage;
    }

    @Override
    public void setUsagePathInfo(String usagePathInfo) {
        this.usagePathInfo = usagePathInfo;
    }

    @Override
    public void setUserAccess(String userAccess) {

    }

    public void setUserAgreementAccepted(boolean userAgreementAccepted) {
        this.isUserAgreementAccepted = userAgreementAccepted;
    }

    @Override
    public void setUserAnonymity(String userAnonymity) {
        this.userAnonymity = userAnonymity;
    }

    @Override
    public UserProfile setUserInfo(UserProfile contactInfo) {
        return this.contactInfo = contactInfo;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    @Override
    public void setUserPrivileges(String privileges) {

    }

    public void setWebProdAdmin(boolean isWebProdAdmin) {
        this.isWebProdAdmin = isWebProdAdmin;
    }

    public void setWebUserId(String webUserId) {
        this.webUserId = webUserId;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("C=" + this.customerID);
        buf.append(":U=" + this.username);
        buf.append(":N=" + this.companyName);
        buf.append(":D=" + getCartridgeString());
        buf.append(":CT=" + this.contractID);
        buf.append(":SP=" + this.startPage);
        buf.append(":DB=" + this.defaultDB);
        buf.append(":E=" + URLEncoder.encode(this.refEmail));
        buf.append(":IP=" + this.ipAddress);
        buf.append(":WUID=" + this.webUserId);

        if (null != getUserId()) {
            buf.append(":UID=" + this.getUserId());
        }
        return buf.toString();
    }

    public ProductInfo getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(ProductInfo productInfo) {
        this.productInfo = productInfo;
    }

    public String getCarsJSessionId() {
        return carsJSessionId;
    }

    public void setCarsJSessionId(String carsJSessionId) {
        this.carsJSessionId = carsJSessionId;
    }

    /**
     * Get the user's stored user prefs. This will return a default new instance of userpreferences if there is none. This is ok. We don't really want to store
     * default user preferences. Only when they make a change do we want to update it.
     */
    public UserPrefs getUserPrefs() {
        if (this.userPrefs == null) {
            this.userPrefs = UserPrefs.load(this.getWebUserId());
            if (this.userPrefs == null) {
                this.userPrefs = new UserPrefs(this.getWebUserId());
            }
        }
        return this.userPrefs;
    }

    public void setUserPrefs(UserPrefs userPrefs) {
        this.userPrefs = userPrefs;
    }
    public boolean isHighlightingEnabled(){
    	UserPreferences userPreferencs = this.getUserPreferences();
    	if (userPreferencs == null) {
            log4j.warn("No user preferences!");
            return false;
        }
    	boolean highlightOn = userPreferencs.getBoolean(UserPreferences.FENCE_HIGHLIGHT_V1);
    	boolean highlightRegOnly = userPreferencs.getBoolean(UserPreferences.FENCE_HIGHLIGHT_REG_ONLY);
    	
    	if(highlightOn){
    		if(!highlightRegOnly || (highlightRegOnly && this.isIndividuallyAuthenticated())){
    			return true;
    		}
    	}
    	return false;
    }
}
