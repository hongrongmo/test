package org.ei.biz.personalization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.ei.domain.personalization.PersonalizationInfo;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.session.MergeUserInfo;
import org.ei.util.GUID;
import org.ei.util.MD5Digester;

public class MergePersonalAccount extends PersonalAccount {

    private static final String TAGS = "TAGS";
    private static final String TAG_GROUPS = "TAG_GROUPS";
    private static final String FOLDER = "FOLDER";
    private static final String SEARCHES_SAVED = "SEARCHES_SAVED";
    private static final String GET_ALERTS = " AND EMAIL_ALERT = 'On'";
    private static final String GET_SEARCHES = " AND EMAIL_ALERT = 'Off'";
    private static final String WHERE_CLAUSE = " WHERE USER_ID=? ";

    protected static final StringBuffer strBuf_MergeProfile = new StringBuffer(" UPDATE ").append(_SQL_CONTACT_TABLE).append(
        " SET MERGED='" + MergeUserInfo.MERGED_YES + "', MERGED_WEB_USER_ID=? WHERE  USER_PROFILE_ID=?");
    protected static final StringBuffer strBuf_UnMergeProfile = new StringBuffer(" UPDATE ").append(_SQL_CONTACT_TABLE).append(
        " SET MERGED='', MERGE_TOKEN='', MERGED_WEB_USER_ID='' WHERE  USER_PROFILE_ID=?");
    protected static final StringBuffer strBuf_MergeProfileAndID = new StringBuffer(" UPDATE ").append(_SQL_CONTACT_TABLE).append(
        " SET MERGED=? WHERE  AE_WEB_USER_ID=?");
    protected static final StringBuffer strBuf_updateMergeToken_webUser = new StringBuffer(" UPDATE ").append(_SQL_CONTACT_TABLE).append(
        " SET MERGE_TOKEN=? WHERE  AE_WEB_USER_ID=?");
    protected static final StringBuffer strBuf_updateMergeToken = new StringBuffer(" UPDATE ").append(_SQL_CONTACT_TABLE).append(
        " SET MERGE_TOKEN=? WHERE  USER_PROFILE_ID=?");
    protected static final StringBuffer strBuf_getProfileByToken = (new StringBuffer())
        .append(
            "SELECT USER_PROFILE_ID, EMAIL, INITCAP(FIRST_NAME) FIRST_NAME, INITCAP(LAST_NAME) LAST_NAME,TITLE,ANNOUNCE_FLAG,PASSWORD,AE_WEB_USER_ID,MERGED,MERGE_TOKEN  FROM ")
        .append(_SQL_CONTACT_TABLE).append(" WHERE MERGE_TOKEN=?");
    // (new StringBuffer()).append(" SELECT count(*) count FROM ");
    protected static final StringBuffer strBuf_countQuery = (new StringBuffer()).append("select * from ").append("(select count(*) searches from ")
        .append(SEARCHES_SAVED).append(WHERE_CLAUSE).append(GET_SEARCHES).append("),(select count(*) alerts from ").append(SEARCHES_SAVED).append(WHERE_CLAUSE)
        .append(GET_ALERTS).append("),(select count(*) tags from ").append(TAGS).append(WHERE_CLAUSE).append("),(select count(*) tag_groups from ")
        .append(TAG_GROUPS).append(WHERE_CLAUSE).append("),(select count(*) folders from ").append(FOLDER).append(WHERE_CLAUSE).append(")");

    protected static final StringBuffer strBuf_getFirmQuery = new StringBuffer(
        "select unique FIRM from (select pc.user_profile_id pid, ap_ev_access.contract_data.firm_name firm from user_profile_contract pc inner join AP_EV_ACCESS.Contract_DATA on pc.customer_id = ap_ev_access.contract_data.cust_id ) where PID =?");

    // all the profiles including merged ones
    ArrayList<UserProfile> allProfiles;

    // just the profiles that haven't been merged
    ArrayList<UserProfile> notMergedProfiles;
    String email;

    public MergePersonalAccount(String email) throws InfrastructureException {
        this.email = email;
        if (!GenericValidator.isBlankOrNull(email)) {
            allProfiles = getAllProfiles(email, true, true);
            notMergedProfiles = getAllProfiles(email, false, false);
        }
    }

    public MergePersonalAccount() {
    }

    public boolean hasMultipleProfiles() {
        if (allProfiles != null && allProfiles.size() > 1) {
            return true;
        }
        return false;
    }

    public ArrayList<UserProfile> getAllProfiles() throws InfrastructureException {
        if ((!GenericValidator.isBlankOrNull(email)) && (allProfiles == null)) {
            allProfiles = getAllProfiles(email, true, true);
        }
        return allProfiles;
    }

    public void setAllProfiles(ArrayList<UserProfile> allProfiles) {
        this.allProfiles = allProfiles;
    }

    public ArrayList<UserProfile> getNotMergedProfiles() throws InfrastructureException {
        if ((!GenericValidator.isBlankOrNull(email)) && (notMergedProfiles == null)) {
            notMergedProfiles = getAllProfiles(email, false, false);
        }
        return notMergedProfiles;
    }

    public void setNotMergedProfiles(ArrayList<UserProfile> notMergedProfiles) {
        this.notMergedProfiles = notMergedProfiles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws InfrastructureException {
        if (!GenericValidator.isBlankOrNull(email) && !email.equals(this.email)) {
            this.email = email;
            getAllProfiles();
            getNotMergedProfiles();

        }

    }

    private String createTokenID() throws Exception {
        MD5Digester dig = new MD5Digester();
        GUID guid = new GUID();
        return dig.asHex(dig.digest(guid.toString()));
    }

    public String addTokenToUserByWebUserId(String webUserId) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String token = createTokenID();
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_updateMergeToken_webUser.toString());
            pstmt.setString(1, token);
            pstmt.setString(2, webUserId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return token;
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

        return null;
    }

    public String addTokenToUser(String profileId) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String token = createTokenID();
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_updateMergeToken.toString());
            pstmt.setString(1, token);
            pstmt.setString(2, profileId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return token;
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

        return null;
    }

    /**
     * This method gets the Account information based on the Merge Tokenof the User. @ param : Token ID. @ return : UserProfile.
     */
    public UserProfile getUserProfileByToken(String token) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        UserProfile userProfile = new UserProfile();
        try {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_getProfileByToken.toString());
            pstmt.setString(1, token);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                userProfile.setUserId(rs.getString("USER_PROFILE_ID"));
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

    public String getFirm(String profileId) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String firm = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(strBuf_getFirmQuery.toString());
            pstmt.setString(1, profileId);

            rs = pstmt.executeQuery();
            if (rs.next()) {

                firm = rs.getString("FIRM");
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

        return firm;
    }

    /**
     * get all the personalization counts for all of this users profiles.
     *
     * @param email
     * @return
     * @throws InfrastructureException
     */
    public ArrayList<UserProfile> populateNonMergedPersonalizationInfo() throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            getNotMergedProfiles();

            for (UserProfile up : notMergedProfiles) {

                // only get the info for the profiles that aren't new
                if (GenericValidator.isBlankOrNull(up.getsWebUserId())) {
                    PersonalizationInfo pi = new PersonalizationInfo();
                    String uid = up.getUserId();
                    // for each of the profiles we need to update the tables with the new one and add the old one to the old field
                    pstmt = con.prepareStatement(strBuf_countQuery.toString());
                    pstmt.setString(1, uid);
                    pstmt.setString(2, uid);
                    pstmt.setString(3, uid);
                    pstmt.setString(4, uid);
                    pstmt.setString(5, uid);
                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        pi.setSavedSearches(rs.getInt("searches"));
                        pi.setAlerts(rs.getInt("alerts"));
                        pi.setTags(rs.getInt("tags"));
                        pi.setTagGroups(rs.getInt("tag_groups"));
                        pi.setFolders(rs.getInt("folders"));
                    }

                    // we should get the instuition
                    String firm = getFirm(uid);
                    pi.setFirm(firm);
                    up.setPersonalizationInfo(pi);
                }
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

        return notMergedProfiles;
    }

    /**
     *
     * @param webUserId
     * @param mergeState
     *            - MergUserInfo has all the merge states to use
     * @return
     * @throws InfrastructureException
     */
    public boolean setMainProfileMergeState(String webUserId, String mergeState) throws InfrastructureException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean success = true;
        try {
            con = getConnection();
            // now set the main profile in the profile table to merged

            pstmt = con.prepareStatement(strBuf_MergeProfileAndID.toString());
            pstmt.setString(1, mergeState);
            pstmt.setString(2, webUserId);
            rs = pstmt.executeQuery();
        } catch (Exception e) {
            success = false;
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
        return success;

    }

    /**
     * This function is going to need to update all the personalization tables (TAGS, TAG_GROUPS, SEARCHES_SAVED, FOLDERS) with the new PersonalProfile ID and
     * move the old profile Ids to the OLD_USER_ID field in each of these tables
     *
     * @param profilesToMerge
     * @param newProfile
     * @return
     * @throws InfrastructureException
     */
    public boolean mergeUserPersonalization(String webUserId, List<String> profilesToMerge, UserProfile newProfile) throws InfrastructureException {
        // first we need to update the old_user_id field
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean success = true;

        try {
            con = getConnection();
            for (String toMerge : profilesToMerge) {
                // for each of the profiles we need to update the tables with the new one and add the old one to the old field
                pstmt = con.prepareStatement("UPDATE " + TAGS + strBuf_update_profile.toString());
                pstmt.setString(1, toMerge);
                pstmt.setString(2, newProfile.getUserId());
                pstmt.setString(3, toMerge);
                rs = pstmt.executeQuery();

                pstmt = con.prepareStatement("UPDATE " + TAG_GROUPS + strBuf_update_profile.toString());
                pstmt.setString(1, toMerge);
                pstmt.setString(2, newProfile.getUserId());
                pstmt.setString(3, toMerge);
                rs = pstmt.executeQuery();

                pstmt = con.prepareStatement("UPDATE " + SEARCHES_SAVED + strBuf_update_profile.toString());
                pstmt.setString(1, toMerge);
                pstmt.setString(2, newProfile.getUserId());
                pstmt.setString(3, toMerge);
                rs = pstmt.executeQuery();

                pstmt = con.prepareStatement("UPDATE " + FOLDER + strBuf_update_profile.toString());
                pstmt.setString(1, toMerge);
                pstmt.setString(2, newProfile.getUserId());
                pstmt.setString(3, toMerge);
                rs = pstmt.executeQuery();

                // now set the profile to merged in the contact table and the merged_web_user_id
                pstmt = con.prepareStatement(strBuf_MergeProfile.toString());
                pstmt.setString(1, webUserId);
                pstmt.setString(2, toMerge);
                rs = pstmt.executeQuery();
            }

            // now set the main profile in the profile table to merged
            pstmt = con.prepareStatement(strBuf_MergeProfile.toString());
            pstmt.setString(1, "");
            pstmt.setString(2, newProfile.getUserId());
            rs = pstmt.executeQuery();
            con.commit();
        } catch (Exception e) {
            success = false;
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
        return success;
    }

    public boolean unMergeUser(String email) throws InfrastructureException {
        // first we need to update the old_user_id field
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            ArrayList<UserProfile> allProfiles = getAllProfiles(email, true, true);
            con = getConnection();
            for (UserProfile unMerge : allProfiles) {
                if (MergeUserInfo.MERGED_YES.equalsIgnoreCase(unMerge.getMerged())) {
                    String toMerge = unMerge.getUserId();
                    // for each of the profiles we need to update the tables with the new one and add the old one to the old field
                    pstmt = con.prepareStatement("UPDATE " + TAGS + strBuf_unmerge_profile.toString());
                    pstmt.setString(1, toMerge);
                    pstmt.setString(2, toMerge);
                    rs = pstmt.executeQuery();

                    pstmt = con.prepareStatement("UPDATE " + TAG_GROUPS + strBuf_unmerge_profile.toString());
                    pstmt.setString(1, toMerge);
                    pstmt.setString(2, toMerge);
                    rs = pstmt.executeQuery();

                    pstmt = con.prepareStatement("UPDATE " + SEARCHES_SAVED + strBuf_unmerge_profile.toString());
                    pstmt.setString(1, toMerge);
                    pstmt.setString(2, toMerge);
                    rs = pstmt.executeQuery();

                    pstmt = con.prepareStatement("UPDATE " + FOLDER + strBuf_unmerge_profile.toString());
                    pstmt.setString(1, toMerge);
                    pstmt.setString(2, toMerge);
                    rs = pstmt.executeQuery();

                    // now set the profile to merged in the contact table
                    pstmt = con.prepareStatement(strBuf_UnMergeProfile.toString());
                    pstmt.setString(1, toMerge);
                    rs = pstmt.executeQuery();
                    con.commit();

                }
            }
            success = true;
        } catch (Exception e) {
            success = false;
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
        return success;
    }

}
