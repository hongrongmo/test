package org.ei.session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.ei.util.LRUTable;


public final class SessionCache
{

    private String authURL;
    private String appName;
    private static  SessionCache instance;
    private LRUTable cache = new LRUTable(100);


    private SessionCache(String authURL,
                         String appName)
    {
        this.authURL = authURL;
        this.appName = appName;
    }

    //This method should be called once by the controller during
    //initialization.

    public static synchronized SessionCache getInstance(String authURL,
                                                        String appName)
    {
        if(instance == null)
        {
            instance = new SessionCache(authURL, appName);
        }

        return instance;
    }

    // This method is called anytime after the singleton is
    // initialized.

    public static SessionCache getInstance()
    {
        return instance;
    }



    public UserSession logout(UserSession userSession)
        throws SessionException
    {
		HttpURLConnection httpConn = null;
		InputStream inStream = null;
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append(authURL).append("?rt=l&si=").append(userSession.getSessionID().getID());
			URL url = new URL(buf.toString());
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setDoOutput(true);

			inStream = httpConn.getInputStream();
			while(inStream.read() != -1)
			{
			}

		}
		catch(Exception e)
		{
			throw new SessionException(e);
		}
		finally
		{
			try
			{
				if(inStream != null)
				{
					inStream.close();
				}
			}
			catch(Exception e)
			{

			}

			if(httpConn != null)
			{
				httpConn.disconnect();
			}
		}

        userSession.setUser(new User());
        userSession.setProperties(new Properties());
        (userSession.getSessionID()).incrementVersion();
        if(cache.containsKey(userSession.getSessionID().getID()))
		{
			cache.put(userSession.getSessionID().getID(), userSession);
		}

        return userSession;
    }

    public void touch(SessionID sessionID)
        throws SessionException
    {
        HttpURLConnection httpConn = null;
        InputStream inStream = null;
        try
        {

            long time = System.currentTimeMillis();
            if(cache.containsKey(sessionID.getID()))
            {
                UserSession usession = (UserSession)cache.get(sessionID.getID());
                usession.setLastTouched(time);
            }

            StringBuffer buf = new StringBuffer();
            buf.append(authURL).append("?rt=t&si=").append(sessionID.getID());
            URL url = new URL(buf.toString());
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setDoOutput(true);

            inStream = httpConn.getInputStream();
            while(inStream.read() != -1) {

            }

        }
        catch(Exception e)
        {
            throw new SessionException(e);
        }
        finally {
            try {
                if(inStream != null) {
                    inStream.close();
                }
            }
            catch(Exception e) {

            }

            if(httpConn != null) {
                httpConn.disconnect();
            }
        }
    }


    public UserSession updateUserSession(UserSession uSession)
        throws SessionException
    {

        InputStream in = null;
        HttpURLConnection ucon = null;

        SessionID sesID = uSession.getSessionID();
        int newVersion = sesID.incrementVersion();
        uSession.setSessionID(sesID);
        uSession.setTouched(true);
        uSession.setLastTouched(System.currentTimeMillis());

        if(cache.containsKey(sesID.getID()))
        {
        	cache.put(sesID.getID(), uSession);
		}

        try
        {
            StringBuffer buf = new StringBuffer();
            buf.append(authURL).append("?");
            buf.append("rt=s&ap=").append(this.appName);
            URL url = new URL(buf.toString());
            ucon = (HttpURLConnection)url.openConnection();
            ucon.setRequestMethod("GET");
            ucon.setDoOutput(true);

            Properties props = uSession.unloadToProperties();
            Enumeration en = props.keys();
            while(en.hasMoreElements())
            {
                String key = (String)en.nextElement();
                ucon.setRequestProperty(key, props.getProperty(key));
            }

            in = ucon.getInputStream();
            while(in.read() != -1) {

            }

        }
        catch(Exception e)
        {
            throw new SessionException(e);
        }
        finally
        {
            try
            {
                if(in != null)
                {
                    in.close();
                }
            }
            catch(Exception e1)
            {
            	e1.printStackTrace();
            }

            try
            {
                if(ucon != null)
                {
                    ucon.disconnect();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return uSession;
    }



    public UserSession getUserSession(SessionID sessionID,
                          			  String ipAddress,
                          	          String refURL,
                                  	  String username,
                                  	  String password,
                                  	  String entryToken)
        throws SessionException
    {
        UserSession nsession = null;

        if(username == null &&
           entryToken == null &&
           sessionID != null &&
           cache.containsKey(sessionID.getID()))
        {
            String id = sessionID.getID();
            int version = sessionID.getVersionNumber();
            long currentTime = System.currentTimeMillis();
            nsession = (UserSession)cache.get(id);
            int version2 = (nsession.getSessionID()).getVersionNumber();
            long lastTouched = nsession.getLastTouched();
            long expireTime = nsession.getExpireIn();

            if(version == version2 &&
               (currentTime - lastTouched) < expireTime)
            {
//				System.out.println("Got session from cache");
                nsession.setStatus(SessionStatus.OLD_FROM_CACHE);
                return nsession;
            }
            else
            {
                nsession = null;
            }
        }

//        System.out.println("Getting session from session service.");
        HttpURLConnection  ucon = null;
        BufferedReader in = null;
        User user = null;
        try
        {
            StringBuffer buf = new StringBuffer();
            buf.append(authURL).append("?");
            buf.append("rt=g&ap=").append(this.appName).append("&");
            buf.append("ip=").append(ipAddress);

            if(refURL != null)
            {
                buf.append("&rf=").append(java.net.URLEncoder.encode(refURL));
            }

            if(username != null)
            {
                username = username.trim();
                if(username.length() > 0)
                {
                    buf.append("&un=").append(username);
                }
            }

            if(entryToken != null)
			{
				entryToken = entryToken.trim();
				if(entryToken.length() > 0)
				{
					buf.append("&et=").append(entryToken);
				}
			}

            if(password != null)
            {
                password = password.trim();
                if(password.length() > 0)
                {
                    buf.append("&pa=").append(password);
                }
            }

            if(sessionID != null)
            {
                buf.append("&si=").append(sessionID.getID()).append("&sv=").append(sessionID.getVersionNumber());
            }

            URL url = new URL(buf.toString());
            ucon = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
            int j = 1;
            Properties sessionProps = new Properties();
            while(true)
            {
                String hkey = ucon.getHeaderFieldKey(j);

                if(hkey == null)
                {
                    break;
                }

                    // Get session information.
                if(hkey.indexOf("SESSION") > -1)
                {
                    sessionProps.put(hkey, ucon.getHeaderField(j));
                }

                ++j;
            }
            nsession = new UserSession();
            nsession.loadFromProperties(sessionProps);

            /*
            * Don't cache the if there is and entry token.
            */
            if(nsession.getProperty("ENTRY_TOKEN")==null)
            {
            	cache.put(nsession.getSessionID().getID(), nsession);
			}
        }
        catch(Exception e)
        {
            throw new SessionException(e);
        }
        finally
        {

            try
            {
                if(in != null)
                {
                    in.close();
                }
            }
            catch(Exception e1)
            {
            	e1.printStackTrace();
            }

            try
            {
                if(ucon != null)
                {
                    ucon.disconnect();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return nsession;
    }

}
