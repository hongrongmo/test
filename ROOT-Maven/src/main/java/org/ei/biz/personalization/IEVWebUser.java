package org.ei.biz.personalization;

import java.io.Serializable;
import java.util.List;

import org.ei.biz.personalization.cars.Account;
import org.ei.biz.security.SecurityAttribute;
import org.ei.domain.personalization.ProductInfo;
import org.ei.session.EntryToken;
import org.ei.session.UserPreferences;

public interface IEVWebUser extends Serializable {
	public enum UserAnonymityType {
		// An anonymous Web User; specifically, an anonymous Guest Web User
		ANON_GUEST,
		// An anonymous Web User; specifically, an anonymous IP-Restricted Web
		// User
		ANON_IP,
		// An anonymous Web User; specifically, an anonymous Shibboleth Web User
		ANON_SHIBBOLETH,
		// An anonymous Web User; specifically, an anonymous TicUrl Web User
		ANON_TICURL,
		// An individual Web User, representing a single end user
		INDIVIDUAL;

		public String value() {
			return name();
		}

		public static UserAnonymityType fromValue(String v) {
			return valueOf(v);
		}

	}

	public int getUsermask();

	public void setIpAddress(String ipAddress);

	public String getIpAddress();

	public EntryToken getEntryToken();

	public void setEntryToken(EntryToken entryToken);

	public void setStartPage(String startPage);

	public String getStartPage();

	public void setDefaultDB(String defaultDB);

	public String getDefaultDB();

	public int getDefaultDB(int entitlementsmask);

	public List<String> getDefaultDBAsList(int entitlementsmask);

	public String getContractID();

	public void setContractID(String contractID);

	public void setCartridge(String[] cartridge);

	public String[] getCartridge();

	public void setCustomerID(String customerID);

	public String getCustomerID();

	public void setUsername(String username);

	public String getUsername();

	public boolean isCustomer();

	public String getCartridgeString();

	public String toString();

	/**
	 * Returns the user First name
	 *
	 * @return String firstname
	 */
	String getFirstName();

	/**
	 * sets the user's first name
	 *
	 * @param firstName
	 */
	void setFirstName(String firstName);

	/**
	 * Returns the user Last name
	 *
	 * @return String lastname
	 */
	String getLastName();

	/**
	 * sets the user's last name
	 *
	 * @param lastName
	 */
	void setLastName(String lastName);

	/**
	 * Returns the Email Id of the User
	 *
	 * @return String email id
	 */
	String getEmail();

	/**
	 * sets the Email Id of the User
	 *
	 * @param String
	 *            email
	 */
	void setEmail(String email);

	/**
	 * Gets the UserID
	 *
	 * @return UserID
	 */
	String getUserId();

	/**
	 * Returns the scopus access of the user
	 *
	 * @return boolean scopus access
	 */
	boolean isScopusAccessEnabled();

	/**
	 * Sets the Scopus Access flag
	 *
	 * @param isScopusAccessEnabled
	 */
	void setScopusAccessEnabled(boolean isScopusAccessEnabled);

	/**
	 * Gets the Auth Token
	 *
	 * @return Auth Token
	 */
	String getAuthToken();

	/**
	 * Sets the Auth Token
	 *
	 * @param authToken
	 */
	void setAuthToken(String authToken);

	/**
	 * Gets the Account Name
	 *
	 * @return accountName
	 */
	// IAccount getAccount();

	/**
	 * Sets the Account Name
	 *
	 * @param accountName
	 */
	// void setAccount(IAccount accountName);

	/**
	 * Gets the Department Name
	 *
	 * @return departmentName
	 */
	String getDepartmentName();

	/**
	 * Sets the Department Name
	 *
	 * @param departmentName
	 */
	void setDepartmentName(String departmentName);

	/**
	 * Gets the CarsCookieKey
	 *
	 * @return CarsCookieKey
	 */
	String getCarsCookieKey();

	/**
	 * Sets the CarsCookieKey
	 *
	 * @param CarsCookieKey
	 */
	void setCarsCookieKey(String carsCookieKey);

	/**
	 * Gets the ShibbotlethFence
	 *
	 * @return ShibbotlethFence
	 */

	String getShibbotlethFence();

	/**
	 * Sets the ShibbotlethFence
	 *
	 * @param ShibbotlethFence
	 */
	void setShibbotlethFence(String shibbolethFence);

	/**
	 * Gets the AthensFence
	 *
	 * @return AthensFence
	 */

	String getAthensFence();

	/**
	 * Sets the AthensFence
	 *
	 * @param AthensFence
	 */
	void setAthensFence(String athensFence);

	/**
	 * Gets the pathChoice
	 *
	 * @return the pathChoice
	 */

	String getPathChoice();

	/**
	 * sets the pathChoice
	 *
	 * @param pathChoice
	 */
	void setPathChoice(String choice);

	String getRememberPathFlag();

	/**
	 * sets the rememberPathFlag
	 *
	 * @param rememberPathFlag
	 */
	public void setRememberPathFlag(String pathFlag);

	/**
	 * Gets the CredType
	 *
	 * @return CredType
	 */

	String getCredType();

	/**
	 * Sets the CredType
	 *
	 * @param CredType
	 */
	void setCredType(String credType);

	/**
	 * Gets the SSoKey
	 *
	 * @return SSoKey
	 */
	String getSsoKey();

	/**
	 * Sets the SSoKey
	 *
	 * @param ssoKey
	 */
	void setSsoKey(String ssoKey);

	/**
	 * Gets the LastSuccessfulCarsAccessTime
	 *
	 * @return long LastSuccessfulCarsAccessTime
	 */
	long getLastSuccessfulCarsAccessTime();

	/**
	 * Sets the lastSuccessfulCarsAccessTime
	 *
	 * @param lastSuccessfulCarsAccessTime
	 */
	void setLastSuccessfulCarsAccessTime(long lastSuccessCarsAccessTime);

	/**
	 * Sets the various attributes of the User to the user object
	 *
	 * @param responseMime1
	 */
	void setFeature(String responseMime1);

	/**
	 * Gets the Fence Ids
	 *
	 * @return List<String>
	 */
	List<String> getFenceIds();

	/**
	 * Sets the Fence Ids
	 *
	 * @param fenceIds
	 */
	void setFenceIds(List<String> fenceIds);

	/**
	 * gets the useranonymity
	 *
	 * @return useranonymity
	 */
	String getUserAnonymity();

	/**
	 * gets the useranonymity
	 *
	 * @param useranonymity
	 */
	void setUserAnonymity(String useranonymity);

	/**
	 * gets the usagePathInfo
	 *
	 * @return usagePathInfo
	 */
	String getUsagePathInfo();

	/**
	 * gets the usagePathInfo
	 *
	 * @param usagePathInfo
	 */
	void setUsagePathInfo(String usagePathInfo);

	/**
	 * gets the profile id
	 *
	 * @return profileId
	 */
	String getProfileId();

	/**
	 * Sets the profile id
	 *
	 * @param profileId
	 */
	void setProfileId(String profileId);

	/**
	 * Gets the user privileges
	 *
	 * @return
	 */
	String getUserPrivileges();

	/**
	 * Sets the user privileges
	 *
	 * @param privileges
	 */
	void setUserPrivileges(String privileges);

	/**
	 * Gets the admin privileges
	 *
	 * @return adminPrivileges
	 */
	String getAdminPrivileges();

	/**
	 * Sets the admin privileges
	 *
	 * @param adminPrivileges
	 */
	void setAdminPrivileges(String adminPrivileges);

	/**
	 * Gets FirstName,LastName appended
	 *
	 * @param userNameFL
	 */
	String getUserNameFL();

	/**
	 ** Gets LastName,FirstName appended
	 *
	 * @param userNameLF
	 */
	String getUserNameLF();

	/**
	 * gets the userAccess
	 *
	 * @return userAccess
	 */
	String getUserAccess();

	/**
	 * gets the userAccess
	 *
	 * @param userAccess
	 */
	void setUserAccess(String userAccess);

	/**
	 * Gets the allListLastUpdateDate
	 *
	 * @return the allListLastUpdateDate
	 */
	String getAllListLastUpdateDate();

	/**
	 * the allListLastUpdateDate to set
	 *
	 * @param allListLastUpdateDate
	 */
	void setAllListLastUpdateDate(String allListLastUpdateDate);

	/**
	 * Gets the allowedRegType
	 *
	 * @return allowedRegType
	 */
	String getAllowedRegType();

	/**
	 * Sets the allowedRegType
	 *
	 * @param allowedRegType
	 */
	void setAllowedRegType(String allowedRegType);

	boolean isIndividuallyAuthenticated();
	boolean getIndividuallyAuthenticated();

	UserProfile getUserInfo();

	UserPreferences getUserPreferences();

	void setUserPreferences(UserPreferences userPreferences);

	boolean getPreference(String key);

	void setPreference(String key, boolean value);

	UserProfile setUserInfo(UserProfile contactInfo);

	SecurityAttribute[] getSecurityAttributes();

	boolean isPathChoiceExists();

	boolean isRememberPathFlagEnabled();

	String getAssociationType();

	String getWebUserId();

	boolean isAthensFenceEnabled();

	Account getAccount();

	ProductInfo getProductInfo();

	String getCarsJSessionId();

	void setCarsJSessionId(String carsJSessionId);

	boolean isSSOURLInvoked();

	void setSSOURLInvoked(boolean isSSOURLInvoked);

	boolean isShibbolethFenceEnabled();

	public UserPrefs getUserPrefs();
	public void setUserPrefs(UserPrefs userPrefs);

}
