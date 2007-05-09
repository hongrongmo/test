//package structure
package org.ei.domain.personalization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;
import org.ei.util.GUID;
import org.ei.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
    * This class has the following functionality
    * 1. creation of Account,
    * 2. gets the Account Information,
    * 3. updation of Account Information,
    * 4. deletion of Account,
    * 5. check for Authentication,
    * 6. email existance
    * 7. gets the email based on the user id.
    * and some private methods which are for opening ,
    * for closing database connections  and
    * for replacing the string the strings.
    */
public class PersonalAccount
{
    //protected static Log log = LogFactory.getLog(PersonalAccount.class);

    private String strTodaysDate = StringUtil.EMPTY_STRING;
    private static final String _SQL_CONTACT_TABLE = "USER_PROFILE_CONTRACT";

    private static final StringBuffer strBuf_emailExistsQuery = (new StringBuffer()).append(" SELECT USER_PROFILE_ID FROM ").append(_SQL_CONTACT_TABLE).append(" WHERE LOWER(EMAIL)=? AND CUSTOMER_ID=?");
    private static final StringBuffer strBuf_emailSelectQuery = (new StringBuffer()).append("SELECT EMAIL FROM ").append(_SQL_CONTACT_TABLE).append(" WHERE USER_PROFILE_ID=?");
    private static final StringBuffer strBuf_getProfileQuery = (new StringBuffer()).append("SELECT EMAIL, INITCAP(FIRST_NAME) FIRST_NAME, INITCAP(LAST_NAME) LAST_NAME,TITLE,ANNOUNCE_FLAG,PASSWORD  FROM ").append(_SQL_CONTACT_TABLE).append(" WHERE USER_PROFILE_ID=?");
    private static final StringBuffer strBuf_authQuery = (new StringBuffer()).append("SELECT USER_PROFILE_ID FROM ").append(_SQL_CONTACT_TABLE).append(" WHERE LOWER(EMAIL)=? AND PASSWORD=?  AND  CUSTOMER_ID=? ORDER BY ACCESS_DATE DESC");
    private static final StringBuffer strBuf_getPasswordQuery = (new StringBuffer()).append("SELECT PASSWORD, USER_PROFILE_ID FROM ").append(_SQL_CONTACT_TABLE).append(" WHERE LOWER(EMAIL)=?");
    private static final StringBuffer strBuf_deleteQuery = (new StringBuffer()).append("DELETE FROM ").append(_SQL_CONTACT_TABLE).append(" WHERE USER_PROFILE_ID=?");
    private static final StringBuffer strBuf_updateProfileQuery =new StringBuffer(" UPDATE ").append(_SQL_CONTACT_TABLE).append(" SET ANNOUNCE_FLAG=?, EMAIL=?, FIRST_NAME=?, LAST_NAME=?, PASSWORD=?, ACCESS_DATE=TO_DATE(?,'MM-DD-YYYY'), TITLE=? WHERE  USER_PROFILE_ID=?");
    private static final StringBuffer strBuf_updateAccessQuery = (new StringBuffer()).append("UPDATE ").append(_SQL_CONTACT_TABLE).append(" SET ACCESS_DATE=TO_DATE(?,'MM-DD-YYYY') WHERE USER_PROFILE_ID=?");

    public PersonalAccount() {
        strTodaysDate = StringUtil.getFormattedDate("MM-dd-yyyy");
    }
    /**
    * This method creats the new Account for the person with the given user information.
    * @ params: UserProfile and Password.
    * @ return : User Id.
    */
    public void createUserProfile(UserProfile userProfile)
            throws PersonalAccountException
    {
        Connection con = null;
        CallableStatement proc = null;

        try
        {
            con = getConnection();
            proc = con.prepareCall("{ call PA_createUserProfile(?,?,?,?,?,?,?,?,?,?,?)}");
            proc.setString(1, new GUID().toString());
            proc.setString(2, userProfile.getContractId());
            proc.setString(3, userProfile.getCustomerId());
            proc.setString(4, userProfile.getTitle());
            proc.setString(5, userProfile.getFirstName());
            proc.setString(6, userProfile.getLastName());
            proc.setString(7, userProfile.getEmail().toLowerCase());
            proc.setString(8, userProfile.getPassword());
            proc.setString(9, strTodaysDate);
            proc.setString(10, strTodaysDate);
            proc.setString(11, userProfile.getAnnounceFlag());
            proc.executeUpdate();

        }
        catch(Exception e)
        {
            throw new PersonalAccountException(e);
        }
        finally
        {
            if(proc != null)
            {
                try
                {
                    proc.close();
                }
                catch(Exception e)
                {
                }
            }
            if(con != null)
            {
                replaceConnection(con);
            }
        }
    }


    /**
    * This method gets the Account information based on the User Id of the User.
    * @ param : User Id.
    * @ return : UserProfile.
    */
    public UserProfile getUserProfile(String userProfileId)
        throws PersonalAccountException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        UserProfile userProfile = new UserProfile(userProfileId);
        try
        {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_getProfileQuery.toString());
            pstmt.setString(1, userProfileId);
            rs = pstmt.executeQuery();

            if(rs.next())
            {
                userProfile.setEmail(rs.getString("EMAIL"));
                userProfile.setFirstName(rs.getString("FIRST_NAME"));
                userProfile.setLastName(rs.getString("LAST_NAME"));
                userProfile.setTitle(rs.getString("TITLE"));
                userProfile.setAnnounceFlag(rs.getString("ANNOUNCE_FLAG"));
                userProfile.setPassword(rs.getString("PASSWORD"));
            }

        }
        catch(Exception e)
        {
            throw new PersonalAccountException(e);
        }
        finally
        {

            if(rs != null)
            {
                try
                {
                    rs.close();
                    rs = null;
                }
                catch(Exception e)
                {
                }
            }

            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                    pstmt = null;
                }
                catch(Exception e)
                {
                }
            }

            if(con != null)
            {
                replaceConnection(con);
            }
        }

        return userProfile;
    }


    private void touch(String pid)
            throws Exception
    {

        Connection con = null;
        CallableStatement proc = null;

        try
        {
            con = getConnection();
            proc = con.prepareCall("{ call PA_touch(?,?)}");
            proc.setString(1, pid);
            proc.setString(2, strTodaysDate);
            proc.executeUpdate();
        }
        finally
        {
            if(proc != null)
            {
                try
                {
                    proc.close();
                    proc = null;
                }
                catch(Exception e)
                {
                }
            }
            if(con != null)
            {
                replaceConnection(con);
            }
        }
    }





    /**
    * This method updates the Account Information of the user.
    * @ param : UserProfile and Password.
    * @ return : true if update is success otherwise false.
    */
    public boolean updateUserProfile(UserProfile userProfile)
            throws PersonalAccountException
    {
        Connection con = null;
        CallableStatement proc = null;
        boolean flag = false;

        try
        {
            con = getConnection();
            proc = con.prepareCall("{ call PA_updateUserProfile(?,?,?,?,?,?,?,?,?,?,?)}");
            proc.setString(1, userProfile.getUserId());
            proc.setString(2, userProfile.getContractId());
            proc.setString(3, userProfile.getCustomerId());
            proc.setString(4, userProfile.getTitle());
            proc.setString(5, userProfile.getFirstName());
            proc.setString(6, userProfile.getLastName());
            proc.setString(7, userProfile.getEmail().toLowerCase());
            proc.setString(8, userProfile.getPassword());
            proc.setString(9, strTodaysDate);
            proc.setString(10, strTodaysDate);
            proc.setString(11, userProfile.getAnnounceFlag());
            proc.executeUpdate();
            flag = true;

        }
        catch(Exception e)
        {
            throw new PersonalAccountException(e);
        }
        finally
        {
            if(proc != null)
            {
                try
                {
                    proc.close();
                }
                catch(Exception e)
                {
                }
            }

            if(con != null)
            {
                replaceConnection(con);
            }
        }
        return flag;
    }


    /**
    * This method removes the complete Account Information of the User.
    * @ param : userid
    * @ return : true if the Account Successfully removed otherwise false.
    */
    public boolean removeUserProfile(String userid)
        throws PersonalAccountException
    {
        Connection con = null;
        CallableStatement proc = null;
        boolean flag = false;

        try
        {
            con = getConnection();
            proc = con.prepareCall("{ call PA_removeUserProfile(?)}");
            proc.setString(1, userid);
            proc.executeUpdate();
            flag = true;
        }
        catch(Exception e)
        {
            throw new PersonalAccountException(e);
        }
        finally
        {
            if(proc != null)
            {
                try
                {
                    proc.close();
                }
                catch(Exception e)
                {
                }
            }

            if(con != null)
            {
                replaceConnection(con);
            }
        }

        return flag;
    }

    /**
    * This method expects the user name and password,if the authentication is success returns user id.
    * @ param : email as user name and password.
    * @ return : userid or null on failure
    */

    /** added customer id for autheticating user for personal accounts ***/
    /* CHANGED BACK TO RETURN NULL ON FAILURE */
    public String authenticateUser(String strEmail,
    							   String strPassword,
    							   String strCustomerID,
    							   String strContractID)
        throws PersonalAccountException
    {
        if(strEmail == null)
        {
            return null;
        }

        Connection con = null;
        PreparedStatement pstmt = null;

        ResultSet rs = null;
        String userProfileId = null; //StringUtil.EMPTY_STRING;
        try
        {
            con = getConnection();

            //log.info(" Logging in user " + strEmail.toLowerCase());

            pstmt = con.prepareStatement(strBuf_authQuery.toString());
            pstmt.setString(1,strEmail.toLowerCase());
            pstmt.setString(2,strPassword);
            pstmt.setString(3,strCustomerID);

            rs = pstmt.executeQuery();
            if(rs.next())
            {
                userProfileId = rs.getString("USER_PROFILE_ID");
            }

        }
        catch(Exception e)
        {
            throw new PersonalAccountException(e);
        }
        finally
        {

            if(rs != null)
            {
                try
                {
                    rs.close();
                    rs = null;
                }
                catch(Exception e)
                {
                }
            }

            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                    pstmt = null;
                }
                catch(Exception e)
                {
                }
            }

            if(con != null)
            {
                replaceConnection(con);
            }
        }

        if(userProfileId != null)
        {
        	try
        	{
				touch(userProfileId);
			}
			catch(Exception e)
			{
				throw new PersonalAccountException(e);
			}
		}

        return userProfileId;
    }


    /**
    * This method returns the password for the username.
    * @ param : email as user name.
    * @ return : password.
    */
    public String getPassword(String email)
        throws PersonalAccountException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sPassword = StringUtil.EMPTY_STRING;
        String userProfileId = StringUtil.EMPTY_STRING;
        try
        {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_getPasswordQuery.toString());
            pstmt.setString(1,email.toLowerCase());
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                sPassword = rs.getString("PASSWORD");
                userProfileId = rs.getString("USER_PROFILE_ID");
            }

        }
        catch(Exception e)
        {
            throw new PersonalAccountException(e);
        }
        finally
        {

            if(rs != null)
            {
                try
                {
                    rs.close();
                    rs = null;
                }
                catch(Exception e)
                {
                }
            }

            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                    pstmt = null;
                }
                catch(Exception e)
                {
                }
            }

            if(con != null)
            {
                replaceConnection(con);
            }
        }
        return sPassword;
    }

    /**
    * This method checks for the existance of the user.
    * @ param : email as user name.
    * @ return : returns true if the user exists otherwise false.
    */
    public boolean emailExists(String strEmail, String strCustomerID)
        throws PersonalAccountException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean flag = false;
        try
        {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_emailExistsQuery.toString());
            pstmt.setString(1, strEmail.toLowerCase());
            pstmt.setString(2, strCustomerID);
            rs = pstmt.executeQuery();
            flag = rs.next();
        }
        catch(Exception e)
        {
                throw new PersonalAccountException(e);
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
                }
            }

            if(pstmt!= null)
            {
                try
                {
                    pstmt.close();
                }
                catch(Exception e)
                {
                }
            }

            if(con != null)
            {
                replaceConnection(con);
            }
        }
        return flag;
    }
    /**
    * This method returns the user name based on the user id.
    * @ param : user id.
    * @ return : email (user name).
    */
    public String getEmail(String userProfileId)
        throws PersonalAccountException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sEmail = null;
        try
        {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_emailSelectQuery.toString());
            pstmt.setString(1, userProfileId);
            rs = pstmt.executeQuery();

            if(rs.next())
            {
                sEmail = rs.getString("EMAIL");
            }

        }
        catch(Exception e)
        {
            throw new PersonalAccountException(e);
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                    rs = null;
                }
                catch(Exception e)
                {
                }
            }

            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                    pstmt = null;
                }
                catch(Exception e)
                {
                }
            }

            if(con != null)
            {
                replaceConnection(con);
            }
        }
        return sEmail;
    }


    /**
    * This method for getting the connection from the connection pool.
    * @ return : connection.
    */
    private Connection getConnection() throws PersonalAccountException
    {
        ConnectionBroker broker = null;
        Connection con = null;
        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
        }
        catch(Exception e)
        {
            throw new PersonalAccountException(e);
        }
        return con;
    }
    /**
    * This method is for replacing the connection into the connection pool.
    * @ param : connection.
    */
    private void replaceConnection(Connection con) throws PersonalAccountException
    {
        ConnectionBroker broker = null;
        try
        {
            broker = ConnectionBroker.getInstance();
            broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
        }
        catch(Exception e)
        {
            throw new PersonalAccountException(e);
        }
    }
}
