/**
 *
 */
package org.engvillage.biz.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.books.collections.ReferexCollection;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Sort;
import org.ei.domain.personalization.IUserSessionInfo;

/**
 * @author harovetm
 *
 */
public class UserSession implements IUserSessionInfo {
    public static final Logger log4j = Logger.getLogger(UserSession.class);

    private String sessionid = "";
    private String userid = "";
    private String customerid = "";
    private String companyName = "";
    private String username = "";
    private String[] cartridge = { "" };
    private String contractid = "";
    private long lastTouched;
    private long expireIn;
    private String startpage = "";
    private String defaultdb = "";
    private String refemail = "";
    private String ipaddress = "";
    private String webuserid = "";
    private String status = "";

    private Properties sessionProperties = new Properties();

    public String getProperty(String prop) {
        return this.sessionProperties.getProperty(prop);
    }

    public void setProperty(String propKey, String propVal) {
        this.sessionProperties.put(propKey, propVal);
    }

    public void removeProperty(String propKey) {
        this.sessionProperties.remove(propKey);
    }

    public Properties getProperties() {
        return this.sessionProperties;
    }

    /**
     * Loads session information from a properties object. This is only used in the /engvillage project to restore the session values from incoming request.
     *
     * @param props
     */
    public void loadFromProperties(Properties props) {
        Enumeration<Object> en = props.keys();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();

            if (key.equals(SESSIONID_KEY)) {
                this.sessionid = props.getProperty(key);
                // If the session ID includes version (i.e. 1_<sessiondid>) split and use sessiond id part
                String[] versioned = this.sessionid.split("_");
                if (versioned != null && versioned.length >=2) {
                    this.sessionid = versioned[1];
                }
            } else if (key.equals(USER_KEY)) {
                processUserString(props.getProperty(key));
            } else if (key.equals(LASTTOUCHED_KEY)) {
                this.lastTouched = Long.parseLong(props.getProperty(key));
            } else if (key.equals(EXPIREIN_KEY)) {
                this.expireIn = Long.parseLong(props.getProperty(key));
            } else if (key.equals(STATUS_KEY)) {
                this.status = props.getProperty(key);
            } else {
                sessionProperties.put(key.substring(key.indexOf(".") + 1, key.length()), props.get(key));
            }
        }
    }

    /**
     * This is the counterpart to loadFromProperties. This is used before transferring session information to the /engvillage application
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public Properties unloadToProperties() throws UnsupportedEncodingException {
        Properties props = new Properties();
        if (this.sessionid != null)
            props.put(SESSIONID_KEY, this.sessionid);
        props.put(USER_KEY, outputUserString());
        props.put(LASTTOUCHED_KEY, Long.toString(this.lastTouched));
        props.put(EXPIREIN_KEY, Long.toString(this.expireIn));
        if (this.status != null)
            props.put(STATUS_KEY, this.status);
        if (sessionProperties != null) {
            Enumeration<Object> en = sessionProperties.keys();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                props.put("SESSION." + key, sessionProperties.get(key));
            }
        }

        return props;
    }

    /**
     * Parse User information from user string passed via request
     *
     * @param userString
     */
    private void processUserString(String userString) {

        // Tokenize the string pairs
        StringTokenizer pairs = new StringTokenizer(userString, ":");
        while (pairs.hasMoreTokens()) {
            String token = pairs.nextToken();
            String kv[] = token.split("=");
            if (kv == null || kv.length < 2) {
                continue;
            }
            if ("C".equals(kv[0])) {
                this.customerid = kv[1];
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
                this.contractid = kv[1];
            } else if ("SP".equals(kv[0])) {
                this.startpage = kv[1];
            } else if ("DB".equals(kv[0])) {
                this.defaultdb = kv[1];
            } else if ("E".equals(kv[0])) {
                try {
                    this.refemail = URLDecoder.decode(kv[1], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    this.log4j.error("Unable to decode email: '" + kv[1] + "'");
                    this.refemail = "-";
                }
            } else if ("IP".equals(kv[0])) {
                this.ipaddress = kv[1];
            } else if ("UID".equals(kv[0])) {
                this.userid = kv[1];
            } else if ("WUID".equals(kv[0])) {
                this.webuserid = kv[1];
            }

        }

    }

    /**
     * Create User string from current input
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public String outputUserString() throws UnsupportedEncodingException {
        StringBuffer buf = new StringBuffer();
        buf.append("C=" + this.customerid);
        buf.append(":U=" + this.username);
        buf.append(":N=" + this.companyName);
        buf.append(":D=" + getCartridgeString());
        buf.append(":CT=" + this.contractid);
        buf.append(":SP=" + this.startpage);
        buf.append(":DB=" + this.defaultdb);
        buf.append(":E=" + URLEncoder.encode(this.refemail, "UTF-8"));
        buf.append(":IP=" + this.ipaddress);
        buf.append(":WUID=" + this.webuserid);

        if (null != this.userid) {
            buf.append(":UID=" + this.userid);
        }
        return buf.toString();
    }

    /**
     * Determine if user credentials are sufficient to access content
     *
     * @param requestMask
     * @param credentialMask
     * @return
     */
    public static boolean hasCredentials(int requestMask, int credentialMask) {

        return ((requestMask & credentialMask) == requestMask);

    }

    /**
     * This method takes a database mask from a previously created RSS feed and converts it to a set of credentials.
     *
     * @param dbmask
     * @return
     */
    public static List<String> buildUserCartridgeForRSS(int dbmask) {
        if (dbmask <= 0) {
            throw new IllegalArgumentException("DB mask is incorrect!");
        }

        List<String> cartridge = new ArrayList<String>();
        if ((dbmask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) {
            cartridge.add(DatabaseConfig.CPX_PREF);
        }
        if ((dbmask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) {
            cartridge.add(DatabaseConfig.INS_PREF);
        }
        if ((dbmask & DatabaseConfig.CBN_MASK) == DatabaseConfig.CBN_MASK) {
            cartridge.add(DatabaseConfig.CBN_PREF);
        }
        if ((dbmask & DatabaseConfig.CHM_MASK) == DatabaseConfig.CHM_MASK) {
            cartridge.add(DatabaseConfig.CHM_PREF);
        }
        if ((dbmask & DatabaseConfig.ELT_MASK) == DatabaseConfig.ELT_MASK) {
            cartridge.add(DatabaseConfig.ELT_PREF);
        }
        if ((dbmask & DatabaseConfig.EPT_MASK) == DatabaseConfig.EPT_MASK) {
            cartridge.add(DatabaseConfig.EPT_PREF);
        }
        if ((dbmask & DatabaseConfig.EUP_MASK) == DatabaseConfig.EUP_MASK) {
            cartridge.add(DatabaseConfig.EUP_PREF);
        }
        if ((dbmask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK) {
            cartridge.add(DatabaseConfig.GEO_PREF);
        }
        if ((dbmask & DatabaseConfig.GRF_MASK) == DatabaseConfig.GRF_MASK) {
            cartridge.add(DatabaseConfig.GRF_PREF);
        }
        if ((dbmask & DatabaseConfig.NTI_MASK) == DatabaseConfig.NTI_MASK) {
            cartridge.add(DatabaseConfig.NTI_PREF);
        }
        if ((dbmask & DatabaseConfig.PCH_MASK) == DatabaseConfig.PCH_MASK) {
            cartridge.add(DatabaseConfig.PCH_PREF);
        }
        if ((dbmask & DatabaseConfig.UPA_MASK) == DatabaseConfig.UPA_MASK) {
            cartridge.add(DatabaseConfig.UPT_PREF);
        }
        return cartridge;
    }

    @Override
    public String getSessionid() {
        return this.sessionid;
    }

    @Override
    public String getUserid() {
        return this.userid;
    }

    @Override
    public String getCustomerid() {
        return this.customerid;
    }

    @Override
    public long getLastTouched() {
        return this.lastTouched;
    }

    @Override
    public long getExpireIn() {
        return this.expireIn;
    }

    @Override
    public String getStartpage() {
        return this.startpage;
    }

    @Override
    public String getDefaultdb() {
        return this.defaultdb;
    }

    @Override
    public String getRecordsPerPage() {
        String pagesize = sessionProperties.getProperty(RECORDS_PER_PAGE);
        if (GenericValidator.isBlankOrNull(pagesize)) {
            pagesize = "25";
        }
        return pagesize;
    }

    @Override
    public String getSortBy() {
        String sSortBy = getCartridgeString().toUpperCase();
        if (sSortBy.indexOf("SRTYR") > -1) {
            sSortBy = Sort.PUB_YEAR_FIELD;
        } else if (sSortBy.indexOf("SRTRE") > -1) {
            sSortBy = Sort.RELEVANCE_FIELD;
        } else {
            sSortBy = Sort.DEFAULT_FIELD;
        }
        return sSortBy;
    }

    @Override
    public String getAutostem() {
        String sAutostem = getCartridgeString().toUpperCase();
        if (sAutostem.indexOf("STMON") > -1) {
            sAutostem = ON;
        } else if (sAutostem.indexOf("STMOFF") > -1) {
            sAutostem = OFF;
        } else {
            sAutostem = AUTOSTEM_DEFAULT;
        }
        return sAutostem;
    }

    @Override
    public String getRefemail() {
        return null;
    }

    @Override
    public String getCartridgeString() {
        StringBuffer buf = new StringBuffer();
        if (this.cartridge == null) {
            return "-";
        } else {

            for (int x = 0; x < this.cartridge.length; ++x) {
                if (x > 0) {
                    buf.append(";");
                }
                buf.append(this.cartridge[x]);
            }

        }

        return buf.toString();
    }

    @Override
    public String[] getCartridge() {
        return this.cartridge;
    }

    @Override
    public String getLogo() {
        return null;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public boolean isCustomer() {
        return !GenericValidator.isBlankOrNull(this.webuserid);
    }

    public String getContractid() {
        return this.contractid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getIpAddress() {
        return this.ipaddress;
    }
}
