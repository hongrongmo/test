//package structure
package org.ei.domain.personalization;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.util.GUID;
import org.ei.util.StringUtil;
import org.jfree.util.Log;

/**
 * This class has the following functionality 1. creation of Account, 2. gets the Account Information, 3. updation of Account Information, 4. deletion of
 * Account, 5. check for Authentication, 6. email existance 7. gets the email based on the user id. and some private methods which are for opening , for closing
 * database connections and for replacing the string the strings.
 */
public class PersonalAccount {
    protected static final Logger log4j = Logger.getLogger(PersonalAccount.class);

    protected String strTodaysDate = StringUtil.EMPTY_STRING;
    protected static final String _SQL_CONTACT_TABLE = "USER_PROFILE_CONTRACT";

    protected static final StringBuffer strBuf_emailExistsQuery = (new StringBuffer()).append(" SELECT USER_PROFILE_ID FROM ").append(_SQL_CONTACT_TABLE)
        .append(" WHERE LOWER(EMAIL)=? AND CUSTOMER_ID=?");
    protected static final StringBuffer strBuf_emailExistsNoCustIdQuery = (new StringBuffer()).append(" SELECT * FROM ").append(_SQL_CONTACT_TABLE)
        .append(" WHERE LOWER(EMAIL)=?");
    protected static final StringBuffer strBuf_getProfileByWebUserIdQuery = (new StringBuffer()).append(" SELECT * FROM ").append(_SQL_CONTACT_TABLE)
        .append(" WHERE AE_WEB_USER_ID=?");
    protected static final StringBuffer strBuf_emailSelectQuery = (new StringBuffer()).append("SELECT EMAIL FROM ").append(_SQL_CONTACT_TABLE)
        .append(" WHERE USER_PROFILE_ID=?");
    protected static final StringBuffer strBuf_getProfileQuery = (new StringBuffer())
        .append(
            "SELECT EMAIL, INITCAP(FIRST_NAME) FIRST_NAME, INITCAP(LAST_NAME) LAST_NAME,TITLE,ANNOUNCE_FLAG,PASSWORD,AE_WEB_USER_ID,MERGED,MERGE_TOKEN  FROM ")
        .append(_SQL_CONTACT_TABLE).append(" WHERE USER_PROFILE_ID=?");
    protected static final StringBuffer strBuf_getAllProfiles = (new StringBuffer()).append(" SELECT USER_PROFILE_ID FROM ").append(_SQL_CONTACT_TABLE)
        .append(" WHERE LOWER(EMAIL)=?");
    protected static final StringBuffer strBuf_authQuery = (new StringBuffer()).append("SELECT USER_PROFILE_ID FROM ").append(_SQL_CONTACT_TABLE)
        .append(" WHERE LOWER(EMAIL)=? AND PASSWORD=?  AND  CUSTOMER_ID=? ORDER BY ACCESS_DATE DESC");
    protected static final StringBuffer strBuf_getPasswordQuery = (new StringBuffer()).append("SELECT PASSWORD, USER_PROFILE_ID FROM ")
        .append(_SQL_CONTACT_TABLE).append(" WHERE LOWER(EMAIL)=?");
    protected static final StringBuffer strBuf_deleteQuery = (new StringBuffer()).append("DELETE FROM ").append(_SQL_CONTACT_TABLE)
        .append(" WHERE USER_PROFILE_ID=?");
    protected static final StringBuffer strBuf_updateProfileQuery = new StringBuffer(" UPDATE ").append(_SQL_CONTACT_TABLE).append(
        " SET ANNOUNCE_FLAG=?, EMAIL=?, FIRST_NAME=?, LAST_NAME=?, PASSWORD=?, ACCESS_DATE=TO_DATE(?,'MM-DD-YYYY'), TITLE=? WHERE  USER_PROFILE_ID=?");

    protected static final StringBuffer strBuf_updateWebUserId = new StringBuffer(" UPDATE ").append(_SQL_CONTACT_TABLE).append(
        " SET AE_WEB_USER_ID=? WHERE  EMAIL=?");

    protected static final StringBuffer strBuf_updateAccessQuery = (new StringBuffer()).append("UPDATE ").append(_SQL_CONTACT_TABLE)
        .append(" SET ACCESS_DATE=TO_DATE(?,'MM-DD-YYYY') WHERE USER_PROFILE_ID=?");

    protected static final StringBuffer strBuf_update_profile = new StringBuffer(" SET OLD_USER_ID=?, USER_ID=? WHERE  USER_ID=?");
    protected static final StringBuffer strBuf_unmerge_profile = new StringBuffer(" SET USER_ID=?, OLD_USER_ID='' WHERE  OLD_USER_ID=?");

    public PersonalAccount() {
        strTodaysDate = StringUtil.getFormattedDate("MM-dd-yyyy");
    }

    /**
     * This method creates the new Account for the person with the given user information. It will check to see if there is already an account with the same
     * webuserid as the one sent in if there is then it won't make one and will return the one that already exists @ params: UserProfile and Password. @ return
     * : User Id.
     */
    public UserProfile createUserProfile(UserProfile userProfile) throws InfrastructureException {
        Connection con = null;
        CallableStatement proc = null;

        if (userExists(userProfile.getsWebUserId())) {
            Log.info("User already exists in profile table");
            return getUserProfileByWebUserId(userProfile.getsWebUserId());
        }

        try {
            con = getConnection();
            proc = con.prepareCall("{ call PA_ae_createUserProfile(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            userProfile.setUserId(new GUID().toString());

            ArrayList<String> allUserIds = new ArrayList<String>();
            allUserIds.add(userProfile.getUserId());
            userProfile.setAllUserIds(allUserIds);

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
            proc.setString(12, userProfile.getsWebUserId());
            proc.setString(13, userProfile.getMerged());
            proc.executeUpdate();

        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {
            if (proc != null) {
                try {
                    proc.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                replaceConnection(con);
            }
        }
        return userProfile;

    }

    /**
     * This method gets the Account information based on the User Id of the User. @ param : User Id. @ return : UserProfile.
     */
    public UserProfile getUserProfile(String userProfileId) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        UserProfile userProfile = new UserProfile(userProfileId);
        try {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_getProfileQuery.toString());
            pstmt.setString(1, userProfileId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                userProfile.setEmail(rs.getString("EMAIL"));
                userProfile.setFirstName(rs.getString("FIRST_NAME"));
                userProfile.setLastName(rs.getString("LAST_NAME"));
                userProfile.setTitle(rs.getString("TITLE"));
                userProfile.setAnnounceFlag(rs.getString("ANNOUNCE_FLAG"));
                userProfile.setPassword(rs.getString("PASSWORD"));
                userProfile.setsWebUserId(rs.getString("AE_WEB_USER_ID"));
                userProfile.setMerged(rs.getString("MERGED"));
                userProfile.setMergeToken(rs.getString("MERGE_TOKEN"));
            }

        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (Exception e) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                    pstmt = null;
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }

        return userProfile;
    }

    /**
     * Upadtes the webuserid for a given email. Note: this could affect more than one row
     *
     * @param email
     * @param merged
     * @return
     * @throws InfrastructureException
     */
    public boolean addWebUserId(String email, String webUserId) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_updateWebUserId.toString());
            pstmt.setString(1, webUserId);
            pstmt.setString(2, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (Exception e) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                    pstmt = null;
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }

        return false;
    }

    /**
     * Update the profile with new timestamp
     * @param profileid
     * @throws InfrastructureException
     */
    public void touchUserProfile(String profileid) throws InfrastructureException {

        Connection con = null;
        CallableStatement proc = null;

        try {
            con = getConnection();
            proc = con.prepareCall("{ call PA_touch(?,?)}");
            proc.setString(1, profileid);
            proc.setString(2, strTodaysDate);
            proc.executeUpdate();
        } catch (Exception e) {
            log4j.error("Unable to touch profile with id '" + profileid + "'", e);
            throw new InfrastructureException(SystemErrorCodes.UNKNOWN_INFRASTRUCTURE_ERROR, e.getMessage());
        }
        finally {
            if (proc != null) {
                try {
                    proc.close();
                    proc = null;
                } catch (Exception e) {
                }
            }
            if (con != null) {
                replaceConnection(con);
            }
        }
    }

    /**
     * This method updates the Account Information of the user. @ param : UserProfile and Password. @ return : true if update is success otherwise false.
     */
    public boolean updateUserProfile(UserProfile userProfile) throws InfrastructureException {
        Connection con = null;
        CallableStatement proc = null;
        boolean flag = false;

        try {
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
            proc.setString(12, userProfile.getsWebUserId());
            proc.executeUpdate();
            flag = true;

        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {
            if (proc != null) {
                try {
                    proc.close();
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }
        return flag;
    }

    /**
     * This method removes the complete Account Information of the User. @ param : userid @ return : true if the Account Successfully removed otherwise false.
     */
    public boolean removeUserProfile(String userid) throws InfrastructureException {
        Connection con = null;
        CallableStatement proc = null;
        boolean flag = false;

        try {
            con = getConnection();
            proc = con.prepareCall("{ call PA_removeUserProfile(?)}");
            proc.setString(1, userid);
            proc.executeUpdate();
            flag = true;
        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {
            if (proc != null) {
                try {
                    proc.close();
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }

        return flag;
    }

    /**
     * This method expects the user name and password,if the authentication is success returns user id. @ param : email as user name and password. @ return :
     * userid or null on failure
     */

    /** added customer id for autheticating user for personal accounts ***/
    /* CHANGED BACK TO RETURN NULL ON FAILURE */
    @Deprecated
    public String authenticateUser(String strEmail, String strPassword, String strCustomerID, String strContractID) throws InfrastructureException {
        if (strEmail == null) {
            return null;
        }

        Connection con = null;
        PreparedStatement pstmt = null;

        ResultSet rs = null;
        String userProfileId = null; // StringUtil.EMPTY_STRING;
        try {
            con = getConnection();

            // log.info(" Logging in user " + strEmail.toLowerCase());

            pstmt = con.prepareStatement(strBuf_authQuery.toString());
            pstmt.setString(1, strEmail.toLowerCase());
            pstmt.setString(2, strPassword);
            pstmt.setString(3, strCustomerID);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                userProfileId = rs.getString("USER_PROFILE_ID");
            }

        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (Exception e) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                    pstmt = null;
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }

        if (userProfileId != null) {
            try {
                touchUserProfile(userProfileId);
            } catch (Exception e) {
                throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
            }
        }

        return userProfileId;
    }

    /**
     * This method returns the password for the username. @ param : email as user name. @ return : password.
     */
    @Deprecated
    public String getPassword(String email) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sPassword = StringUtil.EMPTY_STRING;
        String userProfileId = StringUtil.EMPTY_STRING;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_getPasswordQuery.toString());
            pstmt.setString(1, email.toLowerCase());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                sPassword = rs.getString("PASSWORD");
                userProfileId = rs.getString("USER_PROFILE_ID");
            }

        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (Exception e) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                    pstmt = null;
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }
        return sPassword;
    }

    /**
     * This method checks for the existance of the user. @ param : email as user name. @ return : returns true if the user exists otherwise false.
     */
    public boolean emailExists(String strEmail, String strCustomerID) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean flag = false;
        try {
            con = getConnection();
            if (GenericValidator.isBlankOrNull(strCustomerID)) {
                pstmt = con.prepareStatement(strBuf_emailExistsNoCustIdQuery.toString());
                pstmt.setString(1, strEmail.toLowerCase());
            } else {
                pstmt = con.prepareStatement(strBuf_emailExistsQuery.toString());
                pstmt.setString(1, strEmail.toLowerCase());
                pstmt.setString(2, strCustomerID);
            }
            rs = pstmt.executeQuery();
            flag = rs.next();
        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }
        return flag;
    }

    /**
     * See if this webuserid is already in the table
     *
     * @param webUserId
     * @return
     * @throws InfrastructureException
     */
    public boolean userExists(String webUserId) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean flag = false;
        try {
            con = getConnection();
            if (!GenericValidator.isBlankOrNull(webUserId)) {
                pstmt = con.prepareStatement(strBuf_getProfileByWebUserIdQuery.toString());
                pstmt.setString(1, webUserId.toLowerCase());
            }
            rs = pstmt.executeQuery();
            flag = rs.next();
        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }
        return flag;
    }

    /**
     * Get the token for a given email. Used to resend email without regenerating a token.
     *
     * @param strEmail
     * @param includeMerged
     * @return
     * @throws InfrastructureException
     */
    public String getUserProfileToken(String strEmail) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String token = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_emailExistsNoCustIdQuery.toString()
                + " and ( merged is null or merged = '' or merged = 'N') and (MERGE_TOKEN is not null) ");
            pstmt.setString(1, strEmail.toLowerCase());

            rs = pstmt.executeQuery();

            while (rs.next()) {
                token = rs.getString("MERGE_TOKEN");
                break;
            }
        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }
        return token;
    }

    /**
     * Get all the profile ids for a given email
     *
     * @param strEmail
     * @param checkedMerged
     *            - true if you want to include merged profiles. false to exclude them
     * @return
     * @throws InfrastructureException
     */
    public ArrayList<String> getAllProfileIds(String strEmail, boolean includeMerged) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<String> profiles = new ArrayList<String>();
        try {
            con = getConnection();
            if (!includeMerged) {
                pstmt = con.prepareStatement(strBuf_emailExistsNoCustIdQuery.toString() + " and ( merged is null or merged = '' or merged = 'N') ");
            } else {
                pstmt = con.prepareStatement(strBuf_emailExistsNoCustIdQuery.toString());
            }
            pstmt.setString(1, strEmail.toLowerCase());

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String userProfileId = rs.getString("USER_PROFILE_ID");
                profiles.add(userProfileId);

            }
        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }
        return profiles;
    }

    /**
     * Get all the profiles for a given email
     *
     * @param strEmail
     * @param checkedMerged
     *            - true if you want to include merged profiles. false to exclude them
     * @param includeNew
     *            - include profiles with webuserid
     * @return
     * @throws InfrastructureException
     */
    public ArrayList<UserProfile> getAllProfiles(String strEmail, boolean includeMerged, boolean includeNew) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<UserProfile> profiles = new ArrayList<UserProfile>();
        try {
            con = getConnection();
            StringBuffer stmt = new StringBuffer(strBuf_emailExistsNoCustIdQuery);

            if (!includeMerged) {
                stmt.append(" and ( merged is null or merged = '')");
            }

            if (!includeNew) {
                stmt.append(" and ( ae_web_user_id is null or ae_web_user_id = '')");
            }
            pstmt = con.prepareStatement(stmt.toString());
            pstmt.setString(1, strEmail.toLowerCase());

            rs = pstmt.executeQuery();
            UserProfile userProfile;
            while (rs.next()) {
                userProfile = new UserProfile(rs.getString("USER_PROFILE_ID"));
                userProfile.setEmail(rs.getString("EMAIL"));
                userProfile.setFirstName(rs.getString("FIRST_NAME"));
                userProfile.setLastName(rs.getString("LAST_NAME"));
                userProfile.setTitle(rs.getString("TITLE"));
                userProfile.setAnnounceFlag(rs.getString("ANNOUNCE_FLAG"));
                userProfile.setPassword(rs.getString("PASSWORD"));
                userProfile.setsWebUserId(rs.getString("AE_WEB_USER_ID"));
                userProfile.setMerged(rs.getString("MERGED"));
                userProfile.setMergeToken(rs.getString("MERGE_TOKEN"));
                profiles.add(userProfile);
            }
        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }
        return profiles;
    }

    /**
     * Get all the profiles for a given email
     *
     * @param strEmail
     * @param strCustomerID
     * @return
     * @throws InfrastructureException
     */
    public UserProfile getUserProfileByWebUserId(String webUserId) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserProfile userProfile = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_getProfileByWebUserIdQuery.toString());
            pstmt.setString(1, webUserId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                userProfile = new UserProfile(rs.getString("USER_PROFILE_ID"));
                userProfile.setEmail(rs.getString("EMAIL"));
                userProfile.setFirstName(rs.getString("FIRST_NAME"));
                userProfile.setLastName(rs.getString("LAST_NAME"));
                userProfile.setTitle(rs.getString("TITLE"));
                userProfile.setAnnounceFlag(rs.getString("ANNOUNCE_FLAG"));
                userProfile.setPassword(rs.getString("PASSWORD"));
                userProfile.setsWebUserId(rs.getString("AE_WEB_USER_ID"));
                userProfile.setMerged(rs.getString("MERGED"));
                userProfile.setMergeToken(rs.getString("MERGE_TOKEN"));

            }

        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }
        return userProfile;
    }

    /**
     * This method returns the user name based on the user id. @ param : user id. @ return : email (user name).
     */
    public String getEmail(String userProfileId) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sEmail = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_emailSelectQuery.toString());
            pstmt.setString(1, userProfileId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                sEmail = rs.getString("EMAIL");
            }

        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (Exception e) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                    pstmt = null;
                } catch (Exception e) {
                }
            }

            if (con != null) {
                replaceConnection(con);
            }
        }
        return sEmail;
    }

    /**
     * This method for getting the connection from the connection pool. @ return : connection.
     */
    protected Connection getConnection() throws InfrastructureException {
        ConnectionBroker broker = null;
        Connection con = null;
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        }
        return con;
    }

    /**
     * This method is for replacing the connection into the connection pool. @ param : connection.
     */
    protected void replaceConnection(Connection con) throws InfrastructureException {
        ConnectionBroker broker = null;
        try {
            broker = ConnectionBroker.getInstance();
            broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
        }
    }

}
