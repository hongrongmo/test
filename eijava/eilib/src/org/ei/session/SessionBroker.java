package org.ei.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Properties;

import org.ei.connectionpool.ConnectionBroker;

public class SessionBroker
{

    private static SessionBroker instance;
    private long expireIn;
    private static final String SESSION_POOL = "SessionPool";

    public static synchronized SessionBroker getInstance(long expireIn)
    {
        if(instance == null)
        {
            instance = new SessionBroker(expireIn);
        }

        return instance;
    }

    public static SessionBroker getInstance()
    {
        return instance;
    }

    private SessionBroker(long expireIn)
    {
        this.expireIn = expireIn;
    }


    public void updateSession(UserSession userSession)
        throws SessionException
    {
        Connection con = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;

        ResultSet rs = null;
        String sessionID = (userSession.getSessionID()).getID();


        try
        {
            User user = userSession.getUser();
            ConnectionBroker broker = ConnectionBroker.getInstance();
            con = broker.getConnection(SESSION_POOL);
            con.setAutoCommit(false);
            pstmt1 = con.prepareStatement("update user_session set last_touched = ?, user_object = ?, contract_id = ?, version_no = ? where session_id = ?");
            pstmt1.setLong(1, userSession.getLastTouched());
            pstmt1.setString(2, user.toString());
            pstmt1.setLong(3, Long.parseLong(user.getContractID()));
            pstmt1.setInt(4, (userSession.getSessionID()).getVersionNumber());
            pstmt1.setString(5, sessionID);
            pstmt1.executeUpdate();

            pstmt2 = con.prepareStatement("delete from session_props where session_id = ?");
            pstmt2.setString(1, sessionID);
            pstmt2.executeUpdate();


            Properties props = userSession.getProperties();
            Enumeration en = props.keys();
            while(en.hasMoreElements())
            {
                String key = (String)en.nextElement();
                if(key.indexOf("ENV_") < 0)
                {
                        String value = props.getProperty(key);
                        insertSessionProp(con,sessionID,key,value);
                }
            }

            con.commit();
        }
        catch(Exception e)
        {
            try
            {
                con.rollback();
            }
            catch(Exception e1)
            {
                e1.printStackTrace();
            }

            throw new SessionException(e);
        }
        finally
        {
            if(pstmt1 != null)
            {
                try
                {
                    pstmt1.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }

            if(pstmt2 != null)
            {
                try
                {
                    pstmt2.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }

            if(con != null)
            {
                try
                {
                    con.setAutoCommit(true);
                    ConnectionBroker broker = ConnectionBroker.getInstance();
                    broker.replaceConnection(con, SESSION_POOL);
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }


    private void insertSessionProp(Connection con,
                                   String sessionID,
                                   String key,
                                   String value)
        throws Exception
    {

        PreparedStatement pstmt = null;

        try
        {
            pstmt = con.prepareStatement("insert into session_props values (?,?,?)");
            pstmt.setString(1, sessionID);
            pstmt.setString(2, key);
            pstmt.setString(3, value);
            pstmt.executeUpdate();
        }
        finally
        {
            if(pstmt != null)
            {
                pstmt.close();
            }
        }

    }

    public UserSession getUserSession(SessionID sessionID)
        throws SessionException
    {
        String sesID = sessionID.getID();
        int version = sessionID.getVersionNumber();
        Connection con = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        UserSession u = null;

        try
        {
            ConnectionBroker broker = ConnectionBroker.getInstance();
            con = broker.getConnection(SESSION_POOL);
            try
            {
            	pstmt = con.prepareStatement("select * from user_session where session_id = ?");
            	pstmt.setString(1, sesID);
            	rs = pstmt.executeQuery();
            	String entryToken = null;
            	if(rs.next())
            	{
					entryToken = rs.getString("token_id");
					if(entryToken != null)
					{
						int status = rs.getInt("status");
						if(status == 0)
						{
							return null;
						}
					}

            	    int vn = rs.getInt("VERSION_NO");
            	    long lastTouched = rs.getLong("LAST_TOUCHED");
            	    long currentTime = System.currentTimeMillis();

            	    if(vn >= version)
            	    {

            	        if((currentTime - lastTouched)<expireIn)
            	        {

            	            String userString = rs.getString("USER_OBJECT");
            	            User user = new User(userString);
            	            u = new UserSession();
            	            u.setUser(user);
            	            u.setSessionID(new SessionID(sesID, vn));
            	            u.setLastTouched(currentTime);
            	            u.setExpireIn(expireIn);
            	            if(vn > 1 || entryToken != null)
            	            {
								try
								{
            	                	pstmt2 = con.prepareStatement("select * from session_props where session_id = ?");
            	                	pstmt2.setString(1,sesID);
            	                	rs2 = pstmt2.executeQuery();

            	                	while(rs2.next())
            	                	{
            	                	    String key = rs2.getString("KEY");
            	                	    String value = rs2.getString("VALUE");
            	                	    u.setProperty(key, value);
            	                	}
								}
								finally
								{
									if(rs2 != null)
									{
										try
										{
											rs2.close();
										}
										catch(Exception e)
										{
											e.printStackTrace();
										}
									}

									if(pstmt2 != null)
									{
										try
										{
											pstmt2.close();
										}
										catch(Exception e)
										{
											e.printStackTrace();
										}
									}
								}
            	            }
            	        }
            	    }
            	}
			}
			finally
			{
				if(rs != null)
				{
					try
					{
						rs.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}

				if(pstmt != null)
				{
					try
					{
						pstmt.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
        }
        catch(Exception e)
        {
            throw new SessionException(e);
        }
        finally
        {
            if(con != null)
            {
                try
                {
                    ConnectionBroker broker = ConnectionBroker.getInstance();
                    broker.replaceConnection(con, SESSION_POOL);
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        return u;
    }

	public String getActiveSession(String tokenID)
		throws Exception
	{
		Connection con = null;
		ConnectionBroker broker = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long currentTime = System.currentTimeMillis();
		long testTime = currentTime-expireIn;

		try
		{
			broker = ConnectionBroker.getInstance();
            con = broker.getConnection(SESSION_POOL);
			pstmt = con.prepareStatement("select * from user_session where token_id = ? and (last_touched >= ? AND status = 1)");
			pstmt.setString(1,tokenID);
			pstmt.setLong(2,testTime);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				return rs.getString("session_id");
			}
		}
		finally
		{
			if(rs != null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(pstmt != null)
			{
				try
				{
					pstmt.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(con != null)
			{
				try
				{
					broker.replaceConnection(con, SESSION_POOL);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	private boolean tokenConcurrent(Connection con,
									EntryToken entryToken)
		throws Exception
	{
		boolean concurrent = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long currentTime = System.currentTimeMillis();
		long testTime = currentTime-expireIn;
		String tokenID = entryToken.getTokenID();

		try
		{
			pstmt = con.prepareStatement("select * from user_session where token_id = ? and (last_touched >= ? AND status = 1) for update");
			pstmt.setString(1,tokenID);
			pstmt.setLong(2,testTime);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				return true;
			}
		}
		finally
		{
			if(rs != null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(pstmt != null)
			{
				try
				{
					pstmt.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return concurrent;
	}

	private UserSession createSessionToken(User u)
        throws SessionException
    {
		EntryToken entryToken = u.getEntryToken();
		Connection con = null;
        PreparedStatement pstmt = null;
        ConnectionBroker broker = null;
        String tokenID = entryToken.getTokenID();
        UserSession userSession = new UserSession();
        userSession.setSessionID(new SessionID());
        SessionID sessionID = userSession.getSessionID();

		try
		{
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(SESSION_POOL);
        	con.setAutoCommit(false);

			if(!entryToken.allowEntry())
			{
				userSession.setProperty("token_expired",
										 entryToken.getTokenID());
				u = new User();
				tokenID = "";
			}
			else if(tokenConcurrent(con, entryToken))
			{
				userSession.setProperty("token_concurrent",
										 entryToken.getTokenID());
				u = new User();
				tokenID = "";
			}
			else
			{
				userSession.setProperty("P_USER_ID", entryToken.getAccountID());
				userSession.setProperty("ENTRY_TOKEN", entryToken.getTokenID());
				insertSessionProp(con, sessionID.getID(), "P_USER_ID", entryToken.getAccountID());
				insertSessionProp(con, sessionID.getID(), "ENTRY_TOKEN", entryToken.getTokenID());
			}

        	long currentTime = System.currentTimeMillis();
        	userSession.setUser(u);
        	userSession.setExpireIn(expireIn);
        	String contractID = u.getContractID();
        	pstmt = con.prepareStatement("insert into user_session values (?,?,?,?,?,?,?)");
        	pstmt.setString(1,sessionID.getID());
        	pstmt.setInt(2, sessionID.getVersionNumber());
			pstmt.setLong(3, Long.parseLong(contractID));
			pstmt.setLong(4, System.currentTimeMillis());
			pstmt.setString(5, u.toString());
			pstmt.setInt(6, 1);
			pstmt.setString(7, tokenID);
			pstmt.executeUpdate();
			con.commit();
		}
		catch(Exception e)
		{
			try
			{
				con.rollback();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			throw new SessionException(e);
		}
		finally
		{
			if(pstmt != null)
			{
				try
				{
					pstmt.close();
				}
					catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}

			if(con != null)
			{
				try
				{
					con.setAutoCommit(true);
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}

				try
				{
					broker.replaceConnection(con, SESSION_POOL);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return userSession;
    }

    /**
    *
    *
    **/

    public UserSession createSession(User u)
        throws SessionException
    {
		/*
			If the user has a EntryToken then
			we send them to different method for
			creating a session.
		*/

		if(u.getEntryToken() != null)
		{
			System.out.println("Creating session with user token");
			return createSessionToken(u);
		}

        Connection con = null;
        PreparedStatement pstmt = null;
        long currentTime = System.currentTimeMillis();
        UserSession userSession = new UserSession();
        userSession.setUser(u);
        userSession.setSessionID(new SessionID());
        userSession.setExpireIn(expireIn);
        try
        {
            ConnectionBroker broker = ConnectionBroker.getInstance();
            con = broker.getConnection(SESSION_POOL);
            SessionID  sessionID = userSession.getSessionID();
            String contractID = u.getContractID();
            pstmt = con.prepareStatement("insert into user_session (session_id, version_no, contract_id, last_touched, user_object, status) values (?,?,?,?,?,?)");
            pstmt.setString(1,sessionID.getID());
            pstmt.setInt(2, sessionID.getVersionNumber());
            pstmt.setLong(3, Long.parseLong(contractID));
            pstmt.setLong(4, System.currentTimeMillis());
            pstmt.setString(5, u.toString());
            pstmt.setInt(6,1);
            pstmt.executeUpdate();
        }
        catch(Exception e)
        {
            throw new SessionException(e);
        }
        finally
        {
            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }

            if(con != null)
            {
                try
                {
                    ConnectionBroker broker = ConnectionBroker.getInstance();
                    broker.replaceConnection(con, SESSION_POOL);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return userSession;
    }


    /**
    *
    *
    **/

    public void touch(String sessionID)
        throws SessionException
    {

        Connection con = null;
        PreparedStatement pstmt = null;

        try
        {
            long time = System.currentTimeMillis();
            ConnectionBroker broker = ConnectionBroker.getInstance();
            con = broker.getConnection(SESSION_POOL);
            pstmt = con.prepareStatement("update user_session set last_touched = ? where session_id = ?");
            pstmt.setLong(1,time);
            pstmt.setString(2, sessionID);
            pstmt.executeUpdate();
        }
        catch(Exception e)
        {
            throw new SessionException(e);
        }
        finally
        {
            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }

            if(con != null)
            {
                try
                {
                    ConnectionBroker broker = ConnectionBroker.getInstance();
                    broker.replaceConnection(con, SESSION_POOL);
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }

        }
    }


	public void logout(String sessionID)
		throws SessionException
	{
		Connection con = null;
		PreparedStatement pstmt = null;

		try
		{
			long time = System.currentTimeMillis();
			ConnectionBroker broker = ConnectionBroker.getInstance();
			con = broker.getConnection(SESSION_POOL);
			pstmt = con.prepareStatement("update user_session set status = 0, contract_id = 0, version_no = version_no+1, user_object = ?  where session_id = ?");
			pstmt.setString(1, (new User()).toString());
			pstmt.setString(2, sessionID);
			pstmt.executeUpdate();
		}
		catch(Exception e)
		{
			throw new SessionException(e);
		}
		finally
		{
			if(pstmt != null)
			{
				try
				{
					pstmt.close();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}

			if(con != null)
			{
				try
				{
					ConnectionBroker broker = ConnectionBroker.getInstance();
					broker.replaceConnection(con, SESSION_POOL);
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}
}