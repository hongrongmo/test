package org.ei.session;

import org.ei.books.collections.ReferexCollection;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class User
{

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

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("C="+customerID);
        buf.append(":U="+username);
        buf.append(":N="+companyName);
        buf.append(":D="+ getCartridgeString());
        buf.append(":CT="+contractID);
        buf.append(":SP="+startPage);
        buf.append(":DB="+defaultDB);
        buf.append(":E="+URLEncoder.encode(refEmail));
        buf.append(":IP="+ipAddress);
        return buf.toString();
    }

    public String getCartridgeString()
    {
        StringBuffer buf = new StringBuffer();
        if(this.cartridge != null)
        {

            for(int x=0; x<cartridge.length; ++x)
            {
                if(x > 0)
                {
                    buf.append(";");
                }
                buf.append(cartridge[x]);
            }

        }
        else
        {
            buf.append("-");
        }


        return buf.toString();
    }

    public User()
    {
        //Default Constructor.
    }

    /*
    *   Constructs a User from the Output of toString()
    *   @see toString()
    */

    public User(String userString)
    {

        StringTokenizer pairs = new StringTokenizer(userString, ":");
        String cid = pairs.nextToken();
        this.customerID = cid.substring(cid.indexOf("=")+1, cid.length());
        String u = pairs.nextToken();
        this.username = u.substring(u.indexOf("=")+1, u.length());
        String n = pairs.nextToken();
        this.companyName = n.substring(n.indexOf("=")+1, n.length());
        String d = pairs.nextToken();
        String dataContainerString = d.substring(n.indexOf("=")+1, d.length());
        if((dataContainerString != null) && (!dataContainerString.equals("-")))
        {
            if(ReferexCollection.ALLCOLS_PATTERN.matcher(dataContainerString).find())
            {
              if(!Pattern.compile("PAG").matcher(dataContainerString).find()) {
                dataContainerString = dataContainerString.concat(";PAG");
              }
            }

            cartridge = dataContainerString.split(";");
            /*StringTokenizer dTokens = new StringTokenizer(dataContainerString, ";");
            int x = 0;
            cartridge = new String[dTokens.countTokens()];
            while(dTokens.hasMoreTokens())
            {
                cartridge[x] = dTokens.nextToken();
                ++x;
            }*/
        }

        String ct = pairs.nextToken();
        this.contractID = ct.substring(ct.indexOf("=")+1, ct.length());
		String sp = pairs.nextToken();
		this.startPage = sp.substring(sp.indexOf("=")+1, sp.length());
		String db = pairs.nextToken();
		this.defaultDB = db.substring(db.indexOf("=")+1, db.length());
		String em = pairs.nextToken();
		this.refEmail = URLDecoder.decode(em.substring(em.indexOf("=")+1, em.length()));
        if(pairs.hasMoreTokens())
        {
			String ipt = pairs.nextToken();
			this.ipAddress = ipt.substring(ipt.indexOf("=")+1, ipt.length());
        }
    }


	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public String getIpAddress()
	{
		return this.ipAddress;
	}

	public EntryToken getEntryToken()
	{
		return this.entryToken;
	}

	public void setEntryToken(EntryToken entryToken)
	{
		this.entryToken = entryToken;
	}

    public void setStartPage(String startPage)
    {
        this.startPage = startPage;
    }

    public String getStartPage()
    {
        return this.startPage;
    }

    public void setDefaultDB(String defaultDB)
    {
        this.defaultDB = defaultDB;
    }

    public String getDefaultDB()
    {
        return this.defaultDB;
    }

    public void setRefEmail(String refEmail)
    {
        this.refEmail = refEmail;
    }

    public String getRefEmail()
    {
        return this.refEmail;
    }

    public String getContractID()
    {
        return this.contractID;
    }

    public void setContractID(String contractID)
    {
        this.contractID = contractID;
    }

    public void setCartridge(String[] cartridge)
    {
        this.cartridge = cartridge;
    }

    public String[] getCartridge()
    {
        return this.cartridge;
    }

    public void setCustomerID(String customerID)
    {
        this.customerID = customerID;
    }

    public String getCustomerID()
    {
        return this.customerID;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return this.username;
    }

    public boolean isCustomer()
    {
        boolean cust = false;

        if(!this.customerID.equals("-"))
        {
            cust = true;
        }

        return cust;
    }

}
