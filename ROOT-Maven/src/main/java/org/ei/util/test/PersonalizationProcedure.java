package org.ei.util.test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.ei.domain.personalization.UserProfile;
import org.ei.util.GUID;
import org.ei.util.StringUtil;

public class PersonalizationProcedure
{
    private int intCustomerid = 923358;
    private int intContractid = 20923639;

    private String filter = "TEST";
    private String strUser = "JOHN";

    private String userProfileID = null;
    private UserProfile userProfile = null;
    private String strTodaysDate = StringUtil.getFormattedDate("MM-dd-yyyy");
    
    public String getUserProfileID()
    {
        return userProfileID;
    }

    public PersonalizationProcedure()
        throws Exception
    {
        userProfileID = filter.concat(new GUID().toString());
        userProfile = new UserProfile(userProfileID);

        userProfile.setTitle("MR.");
        userProfile.setFirstName("JOE");
        userProfile.setLastName("SCHMOE");
        userProfile.setEmail(userProfileID.concat("@TEST.COM"));
        userProfile.setPassword("PASSWD");
        userProfile.setAnnounceFlag("No");

    }
    public void runAll()
    {
        createUserProfile();
        touch();
        updateUserProfile();
        removeUserProfile();
    }
    
    public void createUserProfile()
    {
        new PersonalizationProcedure_createUserProfile().run();
    }

    public void touch()
    {
        new PersonalizationProcedure_touch().run();
    }
    
    public void updateUserProfile()
    {
        new PersonalizationProcedure_updateUserProfile().run();
    }

    public void removeUserProfile()
    {
        new PersonalizationProcedure_removeUserProfile().run();
    }

    class PersonalizationProcedure_removeUserProfile extends BaseStoredProcedure
    {
    
        public CallableStatement getStatement(Connection con)
        {
            CallableStatement pstmt = null;
            int idx = 1;
            
            try
            {
                pstmt = con.prepareCall("{ call PA_removeUserProfile(?)}");
                pstmt.setString(idx++, userProfileID);
            }
            catch(SQLException sqle)
            {
                log.error("PersonalizationProcedure_removeUserProfile - SQLException ", sqle);
            }
            finally
            {
            }
    
            return pstmt;
        }
    }
    
    class PersonalizationProcedure_touch extends BaseStoredProcedure
    {
    
        public CallableStatement getStatement(Connection con)
        {
            CallableStatement pstmt = null;
            int idx = 1;
            
            try
            {
                pstmt = con.prepareCall("{ call PA_touch(?,?)}");
                pstmt.setString(idx++, userProfileID);
                pstmt.setString(idx++, strTodaysDate);
            }
            catch(SQLException sqle)
            {
                log.error("PersonalizationProcedure_touch - SQLException ", sqle);
            }
            finally
            {
            }
    
            return pstmt;
        }
    }

    class PersonalizationProcedure_createUserProfile extends BaseStoredProcedure
    {
    
        public CallableStatement getStatement(Connection con)
        {
            CallableStatement pstmt = null;
            int idx = 1;
            
            try
            {
                pstmt = con.prepareCall("{ call PA_createUserProfile(?,?,?,?,?,?,?,?,?,?,?)}");
                pstmt.setString(idx++, userProfileID);
                pstmt.setString(idx++, Integer.toString(intContractid));
                pstmt.setString(idx++, Integer.toString(intCustomerid));
                pstmt.setString(idx++, userProfile.getTitle());
                pstmt.setString(idx++, userProfile.getFirstName());
                pstmt.setString(idx++, userProfile.getLastName());
                pstmt.setString(idx++, userProfile.getEmail().toLowerCase());
                pstmt.setString(idx++, userProfile.getPassword());
                pstmt.setString(idx++, strTodaysDate);
                pstmt.setString(idx++, strTodaysDate);
                pstmt.setString(idx++, userProfile.getAnnounceFlag());
            }
            catch(SQLException sqle)
            {
                log.error("PersonalizationProcedure_createUserProfile - SQLException ", sqle);
            }
            finally
            {
            }
    
            return pstmt;
        }
    }

    class PersonalizationProcedure_updateUserProfile extends BaseStoredProcedure
    {
    
        public CallableStatement getStatement(Connection con)
        {
            CallableStatement pstmt = null;
            int idx = 1;
            
            try
            {

                pstmt = con.prepareCall("{ call PA_updateUserProfile(?,?,?,?,?,?,?,?,?,?,?)}");
                pstmt.setString(idx++, userProfileID);
                pstmt.setString(idx++, Integer.toString(intContractid));
                pstmt.setString(idx++, Integer.toString(intCustomerid));
                pstmt.setString(idx++, userProfile.getTitle());
                pstmt.setString(idx++, userProfile.getFirstName());
                pstmt.setString(idx++, userProfile.getLastName());
                pstmt.setString(idx++, userProfile.getEmail().toLowerCase());
                pstmt.setString(idx++, userProfile.getPassword());
                pstmt.setString(idx++, strTodaysDate);
                pstmt.setString(idx++, strTodaysDate);
                pstmt.setString(idx++, userProfile.getAnnounceFlag());
                
            }
            catch(SQLException sqle)
            {
                log.error("PersonalizationProcedure_updateUserProfile - SQLException ", sqle);
            }
            finally
            {
            }
    
            return pstmt;
        }
    }
                

}
