package org.ei.session;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.access.IUserAuthentication;
import org.ei.biz.access.IUserInfo;
import org.ei.biz.access.IUserPreferences;
import org.ei.biz.security.UserAccessType;
import org.ei.books.collections.ReferexCollection;
import org.ei.domain.personalization.PersonalAccount;
import org.ei.domain.personalization.PersonalAccountException;
import org.ei.domain.personalization.UserProfile;
import org.ei.exception.InfrastructureException;

public class User {

    private Logger log4j = Logger.getLogger(User.class);

    public static String USERNAME_AUTH_FAIL = "_FAIL";
    public static String USERNAME_ENTRY_TOKEN = "_ET";
    public static String USERNAME_INDIV_AUTH_FAIL = "_UN_FAIL";
    public static String USERNAME_REFERRER_URL = "_REF";
    public static String USERNAME_IP_AUTH = "_IP";
    
    private String customerID = "-";
	private String userid = "-";
	private String username = "-";
	private String companyName = "-";
	private String contractID = "0";
	private String[] cartridge;
	private String startPage = "-";
	private String defaultDB = "-";
	private String refEmail = "-";
	private String ipAddress = "-";
	private EntryToken entryToken;

    /** holds information about a user's account.  See the Customer System 
     * Web Service UserAccountInfo object*/
    private Account m_account;
    /** Holds the personal/contact information about a user. */
    private IUserInfo m_contactInfo = new UserProfile();
	/** A user access type object for the type returned from the service. */
    private UserAccessType m_accessType;
    /** Holds the web user id. */
    private String m_webUserId;
    /** Holds the user's access and entitlement rights.*/
    private final IUserAuthentication m_userAuthInfo= null;
    /** Holds the user's preferences (feature constraints).*/
    private IUserPreferences m_userPreferences= null;

	protected final void initialize (String userid) {
		
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("C=" + customerID);
		buf.append(":U=" + username);
		buf.append(":N=" + companyName);
		buf.append(":D=" + getCartridgeString());
		buf.append(":CT=" + contractID);
		buf.append(":SP=" + startPage);
		buf.append(":DB=" + defaultDB);
		buf.append(":E=" + URLEncoder.encode(refEmail));
		buf.append(":IP=" + ipAddress);
		buf.append(":UID=" + userid);
		return buf.toString();
	}

	public String getCartridgeString() {
		StringBuffer buf = new StringBuffer();
		if (this.cartridge != null) {

			for (int x = 0; x < cartridge.length; ++x) {
				if (x > 0) {
					buf.append(";");
				}
				buf.append(cartridge[x]);
			}

		} else {
			buf.append("-");
		}

		return buf.toString();
	}

	public User() {
		// Default Constructor.
	}

	/*
	 * Constructs a User from the Output of toString()
	 * 
	 * @see toString()
	 */

	public User(String userString) throws InfrastructureException {

		// Tokenize the string pairs
		StringTokenizer pairs = new StringTokenizer(userString, ":");
		while (pairs.hasMoreTokens()) {
			String token = pairs.nextToken();
			String kv[] = token.split("=");
			if (kv == null || kv.length < 2) continue;
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
					log4j.error("Unable to decode email: '" + kv[1] + "'");
					this.refEmail = "-";
				}
			} else if ("IP".equals(kv[0])) {
				this.ipAddress = kv[1];
			} else if ("UID".equals(kv[0])) {
				this.userid = kv[1];
			}

		}
/*		String cid = pairs.nextToken();
		this.customerID = cid.substring(cid.indexOf("=") + 1, cid.length());
		String u = pairs.nextToken();
		this.username = u.substring(u.indexOf("=") + 1, u.length());
		String n = pairs.nextToken();
		this.companyName = n.substring(n.indexOf("=") + 1, n.length());
		String d = pairs.nextToken();
		String dataContainerString = d
				.substring(n.indexOf("=") + 1, d.length());

		log4j.debug("username: " + username + " dataContainerString: "
				+ dataContainerString);

		if ((dataContainerString != null) && (!dataContainerString.equals("-"))) {
			if (ReferexCollection.ALLCOLS_PATTERN.matcher(dataContainerString)
					.find()) {
				if (!Pattern.compile("PAG").matcher(dataContainerString).find()) {
					dataContainerString = dataContainerString.concat(";PAG");
				}
			}

			cartridge = dataContainerString.split(";");
		}
		String ct = pairs.nextToken();
		this.contractID = ct.substring(ct.indexOf("=") + 1, ct.length());
		String sp = pairs.nextToken();
		this.startPage = sp.substring(sp.indexOf("=") + 1, sp.length());
		String db = pairs.nextToken();
		this.defaultDB = db.substring(db.indexOf("=") + 1, db.length());
		String em = pairs.nextToken();
		this.refEmail = URLDecoder.decode(em.substring(em.indexOf("=") + 1,
				em.length()));
		if (pairs.hasMoreTokens()) {
			String ipt = pairs.nextToken();
			this.ipAddress = ipt.substring(ipt.indexOf("=") + 1, ipt.length());
		}
		*/
		
		
		// 
		// Build UserPreferences from cartridge
		//
		// this.m_userPreferences = new UserPreferences(this.cartridge);
		
		// 
		// Build UserInfo from userid
		//
		if (!("-".equals(this.userid)) && !GenericValidator.isBlankOrNull(this.userid)) {
			this.m_contactInfo = new PersonalAccount().getUserProfile(userid);
		}
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public EntryToken getEntryToken() {
		return this.entryToken;
	}

	public void setEntryToken(EntryToken entryToken) {
		this.entryToken = entryToken;
	}

	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}

	public String getStartPage() {
		return this.startPage;
	}

	public void setDefaultDB(String defaultDB) {
		this.defaultDB = defaultDB;
	}

	public String getDefaultDB() {
		return this.defaultDB;
	}

	public void setRefEmail(String refEmail) {
		this.refEmail = refEmail;
	}

	public String getRefEmail() {
		return this.refEmail;
	}

	public String getContractID() {
		return this.contractID;
	}

	public void setContractID(String contractID) {
		this.contractID = contractID;
	}

	public void setCartridge(String[] cartridge) {
		this.cartridge = cartridge;
		//this.m_userPreferences = new UserPreferences(this.cartridge);
	}

	public String[] getCartridge() {
		return this.cartridge;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getCustomerID() {
		return this.customerID;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}

	public boolean isCustomer() {
		boolean cust = false;

		if (!this.customerID.equals("-")) {
			cust = true;
		}

		return cust;
	}

	/*@Override
	public IUserAuthentication getUserAuthInfo() {
		return m_userAuthInfo;
	}

	@Override
	public IUserPreferences getUserPreferences() throws AccessException {
		return m_userPreferences;
	}

	@Override
	public IUserInfo getUserInfo() throws AccessException {
		return m_contactInfo;
	}

	public void setUserInfo(IUserInfo userinfo) {
		this.m_contactInfo = userinfo;
	}
	
	@Override
	public UserAccessType getUserAccessType() {
		//
		// Create the UserAccessType from current info
		//
		if (isIndividuallyAuthenticated()) {
			this.m_accessType = UserAccessType.ACTIVREG;
		} else if (USERNAME_IP_AUTH.equals(this.username)) {
			this.m_accessType = UserAccessType.IPRANGE; 
		} else if (!(USERNAME_AUTH_FAIL.equals(this.username)) && !(USERNAME_INDIV_AUTH_FAIL.equals(this.username))){
			this.m_accessType = UserAccessType.GUESTREG;
		} else {
			this.m_accessType = UserAccessType.GUEST;
		} 
		return m_accessType;
	}

	@Override
	public IUserPreferences getFeatureConstraints() {
		return m_userPreferences;
	}
*/
	/*@Override
	public boolean getFeatureConstraint(String name) throws AccessException {
		return m_userPreferences.getBoolean(name);
	}

	@Override
	public IEVAccount getAccount() {
		return m_account;
	}

	@Override
	public String getUserId() {
		return userid;
	}

	public void setUserId(String userid) {
		this.userid = userid;
	}

	@Override
	public String getBulkPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPasswordResetRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIndividuallyAuthenticated() {
		// When user ID is not empty and not default value user is 
		// individually authenticated
		return !GenericValidator.isBlankOrNull(this.userid) && !("-".equals(this.userid));
	}

	@Override
	public String getWebUserId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SecurityAttribute[] getSecurityAttributes() {
        UserAccessType type = getUserAccessType();
        
        if (type == null) {
            return new SecurityAttribute[0];
        }

        List<SecurityAttribute> attributes = new ArrayList<SecurityAttribute>(5);

        if (UserAccessType.IPRANGE.equals(type)) {
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

        if (UserAccessType.BULKPEND.equals(type) || 
            UserAccessType.IPRANGE.equals(type) ||
            UserAccessType.ATHENSPEND.equals(type) ||
            UserAccessType.SHIBBOLETHANON.equals(type) ||
            UserAccessType.SHIBBOLETHPEND.equals(type) ||
            UserAccessType.GUESTREG.equals(type)) {
            attributes.add(SecurityAttribute.CANREGISTER);
        }

        if (UserAccessType.isIndividualAuthType(type)) {
            attributes.add(SecurityAttribute.INDIVIDUAL);
        }
        
        if (attributes.size() > 0) {            
            SecurityAttribute[] securityAttributes = 
                new SecurityAttribute[attributes.size()];
            int attributeSize = attributes.size();
            for (int i = 0; i < attributeSize ; i ++) {
                securityAttributes[i] = (SecurityAttribute) attributes.get(i);
            }
            return securityAttributes;
        }
		return new SecurityAttribute[0];
	}*/

}
