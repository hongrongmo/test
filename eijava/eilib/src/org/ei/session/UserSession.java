package org.ei.session;


import java.util.Enumeration;
import java.util.Properties;

public class UserSession
{

	private static final String SESSIONID_KEY = "SESSION.SK";
	private static final String USER_KEY = "SESSION.UK";
	private static final String LASTTOUCHED_KEY = "SESSION.LK";
	private static final String EXPIREIN_KEY ="SESSION.EX";
	private static final String STATUS_KEY = "SESSION.ST";

	public static final String LOCAL_HOLDING_KEY = "ENV_LHK";

	public static final String CUSTOM_CUSTOMIZED_KEY = "ENV_CK";
	public static final String CUSTOM_CUSTOMIZED_VALUE= "CUSTOMIZED";
	public static final String CUSTOM_CARTRIDGE_KEY = "ENV_CCK";
	public static final String CUSTOM_LOGO_KEY = "ENV_CLK";
	public static final String CUSTOM_START_PAGE_KEY = "ENV_CSPK";
	public static final String CUSTOM_CLASSIFICATION_KEY = "ENV_CLASSK";
	public static final String CUSTOM_YEARS_KEY = "ENV_CYK";
	public static final String CUSTOM_DISPLAYED_PAGES_KEY = "ENV_CDPK";
	public static final String CUSTOM_FULL_TEXT_KEY = "ENV_CFTK";
	public static final String CUSTOM_LOCAL_HOLDING_FLAG_KEY = "ENV_CLHK";
	public static final String CUSTOM_DDS_KEY = "ENV_CDK";
	public static final String CUSTOM_PERSONALIZATION_KEY = "ENV_CPK";
	public static final String CUSTOM_EMAIL_ALERT_KEY = "ENV_CEAK";

	private User user;
	private Properties sessionProperties = new Properties();
	private SessionID sessionID;
	private long lastTouched = System.currentTimeMillis();
	private boolean touched = false;
	private long expireIn;
	private String status;

	public void loadFromProperties(Properties props)
	{
		sessionProperties = new Properties();


		Enumeration en = props.keys();
		while(en.hasMoreElements())
		{
			String key = (String)en.nextElement();

			if(key.equals(SESSIONID_KEY))
			{
				this.sessionID = new SessionID(props.getProperty(key));
			}
			else if(key.equals(USER_KEY))
			{
				this.user = new User(props.getProperty(key));
			}
			else if(key.equals(LASTTOUCHED_KEY))
			{
				this.lastTouched = Long.parseLong(props.getProperty(key));
			}
			else if(key.equals(EXPIREIN_KEY))
			{
				this.expireIn = Long.parseLong(props.getProperty(key));
			}
			else if(key.equals(STATUS_KEY))
			{
				this.status = props.getProperty(key);
			}
			else
			{
				sessionProperties.put(key.substring(key.indexOf(".")+1,key.length()),
									  props.get(key));
			}
		}
	}

	public void setTouched(boolean touched)
	{
		this.touched = touched;
	}

	public boolean getTouched()
	{
		return this.touched;
	}


	public Properties unloadToProperties()
	{
		Properties props = new Properties();
		props.put(SESSIONID_KEY, this.sessionID.toString());
		props.put(USER_KEY, this.user.toString());
		props.put(LASTTOUCHED_KEY, Long.toString(this.lastTouched));
		props.put(EXPIREIN_KEY, Long.toString(this.expireIn));
		props.put(STATUS_KEY, this.status);
		Enumeration en = sessionProperties.keys();
		while(en.hasMoreElements())
		{
			String key = (String) en.nextElement();
			props.put("SESSION."+key, sessionProperties.get(key));
		}

		return props;
	}

	public SessionID getSessionID()
	{
		return this.sessionID;
	}

	public String getID()
	{
		return this.sessionID.getID();
	}

	public void setSessionID(SessionID sessionID)
	{
		this.sessionID = sessionID;
	}

	public long getLastTouched()
	{
		return this.lastTouched;
	}

	public void setLastTouched(long lastTouched)
	{
		this.lastTouched = lastTouched;
	}

	public long getExpireIn()
	{
		return this.expireIn;
	}

	void setExpireIn(long expireIn)
	{
		this.expireIn = expireIn;
	}

	public String getStatus()
	{
		return this.status;
	}

	void setStatus(String status)
	{
		this.status = status;
	}

	public User getUser()
	{
		return this.user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public String getProperty(String prop)
	{
		return this.sessionProperties.getProperty(prop);
	}

	public void setProperty(String propKey, String propVal)
	{
		this.sessionProperties.put(propKey, propVal);
	}

	public Properties getProperties()
	{
		return this.sessionProperties;
	}

	public void setProperties(Properties sessionProperties)
	{
		this.sessionProperties = sessionProperties;
	}



}
