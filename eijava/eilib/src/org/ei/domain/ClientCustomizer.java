// package structure
package org.ei.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.session.User;
import org.ei.session.UserSession;
import org.ei.util.StringUtil;

/**
 * This class used to get Customized Features for the
 * user like the databases,year range,classification codes,
 * local holdings and email alerts.
 */

public class ClientCustomizer
{
	private static Log log = LogFactory.getLog(ClientCustomizer.class);
    private static final String CART_ALERT_CCLIST = "CCL";
    private static final String CART_GRAPH_DWNLD = "GAR";

    private static final int YEARS_ALL = -1;

    private boolean sDDS;
    private String startYear = "";
	private String sEasyDb;

    private User user;

	private final String OFF = "off";
	private final String ON = "on";
	private final String AUTOSTEM_DEFAULT = ON;

    /**
     * Constructor which takes the customer id of the user and
     * gets all the features of that user.
     * @param customerId description.
     * @exception ClientCustomizerException.
     */

    public ClientCustomizer(UserSession userSes) throws ClientCustomizerException
    {
        try
        {
            /* set full text property according to customer's option */
            user = userSes.getUser();

            if (user.isCustomer())
            {
                String carstr = user.getCartridgeString();
                String[] cartridges = user.getCartridge();

                if (carstr.indexOf("LHL") > -1)
                {
					this.setDDS(true);
                }
                else
                {
					this.setDDS(false);
                }
				startYear = getClientStartYears(carstr, cartridges);
            }
        }
        catch (Exception e)
        {
            throw new ClientCustomizerException(e);
        }
    }

    /**
	 * This method creates a string that will contain the start year
	 * for each database which the user subscribes to and also a
	 * selected start year for each database. The start yeaar will be
	 * the furthest back a search can go and the selected start year
	 * will appear as the default in all search forms.
	 *
	 * i.e. CSY1960CST1884ISY1970IST1969NSY1980NST1899
	 *
	 * @return String which contains keys followed by year values for each database the user subscribes to
	 */
	private String getClientStartYears(String carstr, String[] cartridges)
	{

		DatabaseConfig dbconfig = DatabaseConfig.getInstance();

		// simulate backoffice selected start year settings
		// carstr = carstr + "CSY1960;ISY1970;NSY1980";

		StringBuffer buf = new StringBuffer();

		int mask = dbconfig.getScrubbedMask(cartridges);
		Database[] d = dbconfig.getDatabases(mask);
		for(int y = 0; y < d.length; y++)
		{
			if(d[y] != null)
			{
				log.debug(" LOG ID ==>" + d[y].getID());
				//System.out.println("databaseID "+d[y].getID());
				String firstChar = d[y].getSingleCharName().toUpperCase();

				String skey = firstChar.concat("SY");
				int dbStartYear = dbconfig.getStartYear(cartridges, d[y].getMask());

				buf.append(skey);
				if(carstr.indexOf(skey) > -1)
				{
					buf.append(carstr.substring(carstr.indexOf(skey)+3, carstr.indexOf(skey) + 7));
				}
				else
				{
					buf.append(dbStartYear);
				}
				skey = firstChar.concat("ST");
				buf
				.append(skey)
				.append(dbStartYear);
			}
		} // for

		log.debug(" LOG startYear ==>" + buf.toString());
		return buf.toString();
	}

	/**
	 * This method checks for customized feature.
	 * @return true if customization present, false if not.
	 */

    public boolean isCustomized()
    {
        return true;

    }

    /**
     * This method gets the customized logo.
     * @return String - logo name.
     */

    public String getLogo()
    {
        if(user.getCartridgeString().indexOf("LGO") > -1)
		{
            return user.getCustomerID();
        }
        else
        {
            return StringUtil.EMPTY_STRING;
        }
    }

    /**
     * This method gets the Start Page when the user logged in.
     * @return String.
     */
    public String getStartPage()
    {
        if (!user.getStartPage().equals("-"))
        {
            return user.getStartPage();
        }
        else
        {
            return null;
        }
    }

    public String getDefaultDB()
    {
        if (!user.getDefaultDB().equals("-"))
        {
            return user.getDefaultDB();
        }
        else
        {
            DatabaseConfig dbconfig = DatabaseConfig.getInstance();
            String[] cartridges = user.getCartridge();
			int mask = dbconfig.getScrubbedMask(cartridges);
			Database[] d = dbconfig.getDatabases(mask);
            if(d.length >= 1)
            {
                return Integer.toString(d[0].getMask());
            }
            else
            {
                return "0";
            }
        }
    }

    public String getRefEmail()
    {
        if (!user.getRefEmail().equals("-"))
        {
            return user.getRefEmail();
        }
        else
        {
            return null;
        }
    }

	public String getEasyDb()
	{
		sEasyDb = user.getCartridgeString();
		if(sEasyDb.indexOf("EBX") > -1)
		{
			sEasyDb = ON;
		}
		else
		{
			sEasyDb = OFF;
		}
		return sEasyDb;
	}

    public String getSortBy()
    {
        String sSortBy = user.getCartridgeString().toUpperCase();
		if(sSortBy.indexOf("SRTYR") > -1)
	    {
		    sSortBy = Sort.PUB_YEAR_FIELD;
	    }
		else if(sSortBy.indexOf("SRTRE") > -1)
		{
            sSortBy = Sort.RELEVANCE_FIELD;
        }
	    else
	    {
			sSortBy = Sort.DEFAULT_FIELD;
	    }
        return sSortBy;
    }

    public String getAutostem()
    {
		String sAutostem = user.getCartridgeString().toUpperCase();
		if(sAutostem.indexOf("STMON") > -1)
	    {
		    sAutostem = ON;
	    }
		else if(sAutostem.indexOf("STMOFF") > -1)
		{
		    sAutostem = OFF;
        }
	    else
	    {
		    sAutostem = AUTOSTEM_DEFAULT;
	    }
        return sAutostem;
    }

    /**
     * This method gets the start year from which the user can search.
     * @return int - start year value.
     * @exception ClientCustomizerException.
     */

    public int getStartYear() throws ClientCustomizerException
    {
        int nStartYear = -1;

        return nStartYear;
    }

    public String getSYear()
    {
        return startYear;
    }

    /**
     * This method gets the end year with in that only user can search.
     * @return int - end year value.
     * @exception ClientCustomizerException.
     */

    public int getEndYear() throws ClientCustomizerException
    {
        int nEndYear = -1;
        return nEndYear;
    }

    /**
     * This method checks whether the user has
     * rights to see full text or not.
     * @return true - if the user can access/false otherwise.
     * @exception ClientCustomizerException.
     */

    public boolean checkFullText()
    {
        return _checkFullText(null);
    }

    public boolean checkFullText(String context)
    {
        return _checkFullText(context);
    }

    private boolean _checkFullText(String context)
    {
        boolean flag = true;
        String fullText = user.getCartridgeString();
        if (context != null && context.equals("citationResults"))
        {
            if (fullText.indexOf("CFU") > -1)
            {
                flag = false;
            }

            return flag;
        }
        else
        {
            if (fullText.indexOf("FUL") > -1)
            {
                flag = false;
            }
        }

        return flag;
    }

		/**
		 * This method checks whether the user has
		 * rights to see rss link or not.
		 * @return true - if the user can access/false otherwise.
		 * @exception ClientCustomizerException.
		 */

	    public boolean checkRssLink()
	    {
			boolean flag = true;
			String rssLink = user.getCartridgeString();
			if(rssLink.indexOf("RSS") > -1)
			{
				flag = false;
			}

	    	return flag;
	    }

	    /**
		 * This method checks whether the user has
		 * rights to see blog link or not.
		 * @return true - if the user can not access/false otherwise.
		 * @exception ClientCustomizerException.
		 */

		public boolean checkBlogLink()
		{
			boolean flag = true;
			String bloLink = user.getCartridgeString();
			if(bloLink.indexOf("BLG") > -1)
			{
				flag = false;
			}

			return flag;
	    }

		/**
		 * This method checks whether the user has
		 * rights to access local holdings or not.
		 * @return true - if the user can access/false otherwise.
		 * @exception ClientCustomizerException.
		 */
	    public boolean checkLocalHolding()
	    {
	         return true;
	    }

		 public boolean checkLocalHolding(String context)
		 {
			 return _checkLocalHolding(context);
		 }


		private boolean _checkLocalHolding(String context)
		{
			boolean flag = false;
			String localHolding = user.getCartridgeString();
			if(context != null &&
			   context.equals("citationResults"))
			{
			   if(localHolding.indexOf("LHC") > -1)
			   {
				  flag = true;
			   }

			 }
		   return flag;

	    }


    /**
     * This method checks whether the user has
     * rights to access DDS.
     * @return true - if the user can access/false otherwise.
     * @exception ClientCustomizerException.
     */
    public boolean checkDDS() throws ClientCustomizerException
    {
        return sDDS;
    }
	private void setDDS(boolean b)
	{
		this.sDDS = b;
	}

    /**
     * This method checks for personalization feature.
     * @return true - if the user has personalization otherwise false.
     * @exception ClientCustomizerException.
     */

    public boolean checkPersonalization() throws ClientCustomizerException
    {
		boolean flag = true;
		String cartridge = user.getCartridgeString();
		if(cartridge.indexOf("NOP") > -1)
		{
			flag = false;
		}
        return flag;
    }

    /**
     * This method checks for email alerts for the user.
     * @return true - if the user has email alerts feature otherwise false.
     * @exception ClientCustomizerException.
     */
    public boolean checkEmailAlert() throws ClientCustomizerException
    {
        return true;
    }

    // default for this option is false - most users do not have it!
    // only for those with the "CCL" cartridge
    public boolean checkEmailccList() throws ClientCustomizerException
    {
        boolean flag = false;
        String cartridge = user.getCartridgeString();
        if(cartridge.indexOf(CART_ALERT_CCLIST) > -1)
        {
            flag = true;
        }

        return flag;
    }

    // default for this option is false - most users do not have it!
    // only for those with the "GAR" cartridge
    public boolean checkGraphDownload() throws ClientCustomizerException
    {
        boolean flag = true;
        String cartridge = user.getCartridgeString();
        if(cartridge.indexOf(CART_GRAPH_DWNLD) > -1)
        {
            flag = false;
        }

        return flag;
    }

}
