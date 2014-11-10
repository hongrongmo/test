// package structure
package org.engvillage.biz.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.personalization.IUserSessionInfo;
import org.ei.util.StringUtil;

/**
 * This class used to get Customized Features for the user like the databases,year range,classification codes, local holdings and email alerts.
 */

public class ClientCustomizer {
    private static Logger log4j = Logger.getLogger(ClientCustomizer.class);
    private static final String CART_ALERT_CCLIST = "CCL";
    private static final String CART_GRAPH_DWNLD = "GAR";

    private static final int YEARS_ALL = -1;

    private boolean sDDS;
    private String startYear = "";
    private String sEasyDb;

    private IUserSessionInfo usersessioninfo;

    private final String OFF = "off";
    private final String ON = "on";
    private final String AUTOSTEM_DEFAULT = ON;

    /**
     * Constructor which takes the customer id of the user and gets all the features of that user.
     *
     * @param customerId
     *            description.
     * @exception ClientCustomizerException.
     */

    public ClientCustomizer(IUserSessionInfo usersessioninfo) throws ClientCustomizerException {
        // Ensure UserSession object is not empty
        if (usersessioninfo == null) {
            throw new ClientCustomizerException("User Session Info object is empty!");
        }
        this.usersessioninfo = usersessioninfo;

        try {
            /* set full text property according to customer's option */
            if (this.usersessioninfo.isCustomer()) {
                String carstr = this.usersessioninfo.getCartridgeString();
                String[] cartridges = this.usersessioninfo.getCartridge();
                if (!StringUtils.isBlank(carstr)) {
                    this.sDDS = carstr.indexOf("LHL") > -1;
                    this.startYear = getClientStartYears(carstr, cartridges);
                }
            }
        } catch (Exception e) {
            throw new ClientCustomizerException(e);
        }
    }

    /**
     * This method creates a string that will contain the start year for each database which the user subscribes to and also a selected start year for each
     * database. The start yeaar will be the furthest back a search can go and the selected start year will appear as the default in all search forms.
     *
     * i.e. CSY1960CST1884ISY1970IST1969NSY1980NST1899
     *
     * @return String which contains keys followed by year values for each database the user subscribes to
     * @throws ClientCustomizerException
     */
    private String getClientStartYears(String carstr, String[] cartridges) throws ClientCustomizerException {
        if (GenericValidator.isBlankOrNull(carstr) || cartridges == null || cartridges.length == 0) {
            throw new ClientCustomizerException("Empty cartridge string passed to getClientStartYears!");
        }

        DatabaseConfig dbconfig = DatabaseConfig.getInstance();
        StringBuffer buf = new StringBuffer();

        int mask = dbconfig.getScrubbedMask(cartridges);
        Database[] d = dbconfig.getDatabases(mask);
        for (int y = 0; y < d.length; y++) {
            if (d[y] != null) {
                log4j.debug(" LOG ID ==>" + d[y].getID());
                // System.out.println("databaseID "+d[y].getID());
                String firstChar = d[y].getSingleCharName().toUpperCase();

                String skey = firstChar.concat("SY");
                int dbStartYear = dbconfig.getStartYear(cartridges, d[y].getMask());

                buf.append(skey);
                if (carstr.indexOf(skey) > -1) {
                    buf.append(carstr.substring(carstr.indexOf(skey) + 3, carstr.indexOf(skey) + 7));
                } else {
                    buf.append(dbStartYear);
                }
                skey = firstChar.concat("ST");
                buf.append(skey).append(dbStartYear);
            }
        } // for

        log4j.debug(" LOG startYear ==>" + buf.toString());
        return buf.toString();
    }

    /**
     * This method checks for customized feature.
     *
     * @return true if customization present, false if not.
     */

    public boolean isCustomized() {
        return true;
    }

    /**
     * This method gets the customized logo.
     *
     * @return String - logo name.
     */

    public String getLogo() {
        if (this.usersessioninfo.getCartridgeString().indexOf("LGO") > -1) {
            return this.usersessioninfo.getCustomerid();
        } else {
            return StringUtil.EMPTY_STRING;
        }
    }

    /**
     * This method gets the Start Page when the user logged in.
     *
     * @return String.
     */
    public String getStartPage() {
        if (!this.usersessioninfo.getStartpage().equals("-")) {
            return this.usersessioninfo.getStartpage();
        } else {
            return null;
        }
    }

    public String getDefaultDB() {
        if (!"-".equals(this.usersessioninfo.getDefaultdb())) {
            return this.usersessioninfo.getDefaultdb();
        } else {
            DatabaseConfig dbconfig = DatabaseConfig.getInstance();
            String[] cartridges = this.usersessioninfo.getCartridge();
            int mask = dbconfig.getScrubbedMask(cartridges);
            Database[] d = dbconfig.getDatabases(mask);
            if (d.length >= 1) {
                return Integer.toString(d[0].getMask());
            } else {
                return "0";
            }
        }
    }

    public String getRefEmail() {
        if (!"-".equals(this.usersessioninfo.getRefemail())) {
            return this.usersessioninfo.getRefemail();
        } else {
            return null;
        }
    }

    @Deprecated
    public String getEasyDb() {
        this.sEasyDb = this.usersessioninfo.getCartridgeString();
        if (this.sEasyDb.indexOf("EBX") > -1) {
            this.sEasyDb = ON;
        } else {
            this.sEasyDb = OFF;
        }
        return this.sEasyDb;
    }

    public String getSortBy() {
        return this.usersessioninfo.getSortBy();
    }

    public String getAutostem() {
        return this.usersessioninfo.getAutostem();
    }

    /**
     * This method gets the start year from which the user can search.
     *
     * @return int - start year value.
     * @exception ClientCustomizerException.
     */

    public int getStartYear() {
        int nStartYear = -1;

        return nStartYear;
    }

    public String getSYear() {
        return startYear;
    }

    /**
     * This method gets the end year with in that only user can search.
     *
     * @return int - end year value.
     * @exception ClientCustomizerException.
     */

    public int getEndYear() throws ClientCustomizerException {
        int nEndYear = -1;
        return nEndYear;
    }

    /**
     * This method checks whether the user has rights to see full text or not.
     *
     * @return true - if the user can access/false otherwise.
     * @exception ClientCustomizerException.
     */
    public boolean checkFullText(String context) {
        boolean flag = true;
        String fullText = this.usersessioninfo.getCartridgeString();
        if ("citationResults".equals(context)) {
            if (fullText.indexOf("CFU") > -1) {
                flag = false;
            }
            return flag;
        } else {
            if (fullText.indexOf("FUL") > -1) {
                flag = false;
            }
        }

        return flag;
    }

    public boolean checkFullText() {
        return checkFullText(null);
    }

    /**
     * This method checks whether the user has rights to see rss link or not.
     *
     * @return true - if the user can access/false otherwise.
     * @exception ClientCustomizerException.
     */

    public boolean checkRssLink() {
        boolean flag = true;
        String rssLink = this.usersessioninfo.getCartridgeString();
        if (rssLink.indexOf("RSS") > -1) {
            flag = false;
        }

        return flag;
    }

    /**
     * This method checks whether the user has rights to see blog link or not.
     *
     * @return true - if the user can not access/false otherwise.
     * @exception ClientCustomizerException.
     */

    public boolean checkBlogLink() {
        boolean flag = true;
        String bloLink = this.usersessioninfo.getCartridgeString();
        if (bloLink.indexOf("BLG") > -1) {
            flag = false;
        }

        return flag;
    }

    /**
     * This method checks whether the user has rights to access local holdings or not.
     *
     * @return true - if the user can access/false otherwise.
     * @exception ClientCustomizerException.
     */
    public boolean checkLocalHolding() {
        return true;
    }

    public boolean checkLocalHolding(String context) {
        return _checkLocalHolding(context);
    }

    private boolean _checkLocalHolding(String context) {
        boolean flag = false;
        String localHolding = this.usersessioninfo.getCartridgeString();
        if (context != null && context.equals("citationResults")) {
            if (localHolding.indexOf("LHC") > -1) {
                flag = true;
            }

        }
        return flag;

    }

    /**
     * This method checks whether the user has rights to access DDS.
     *
     * @return true - if the user can access/false otherwise.
     * @exception ClientCustomizerException.
     */
    public boolean checkDDS() throws ClientCustomizerException {
        return sDDS;
    }

    private void setDDS(boolean b) {
        this.sDDS = b;
    }

    /**
     * This method checks for personalization feature.
     *
     * @return true - if the user has personalization otherwise false.
     * @exception ClientCustomizerException.
     */

    public boolean checkPersonalization() throws ClientCustomizerException {
        boolean flag = true;
        String cartridge = this.usersessioninfo.getCartridgeString();
        if (cartridge.indexOf("NOP") > -1) {
            flag = false;
        }
        return flag;
    }

    /**
     * This method checks for email alerts for the user.
     *
     * @return true - if the user has email alerts feature otherwise false.
     * @exception ClientCustomizerException.
     */
    public boolean checkEmailAlert() throws ClientCustomizerException {
        return true;
    }

    // default for this option is false - most users do not have it!
    // only for those with the "CCL" cartridge
    public boolean checkEmailccList() throws ClientCustomizerException {
        boolean flag = false;
        String cartridge = this.usersessioninfo.getCartridgeString();
        if (cartridge.indexOf(CART_ALERT_CCLIST) > -1) {
            flag = true;
        }

        return flag;
    }

    // default for this option is false - most users do not have it!
    // only for those with the "GAR" cartridge
    public boolean checkGraphDownload() throws ClientCustomizerException {
        boolean flag = true;
        String cartridge = this.usersessioninfo.getCartridgeString();
        if (cartridge.indexOf(CART_GRAPH_DWNLD) > -1) {
            flag = false;
        }

        return flag;
    }

}
