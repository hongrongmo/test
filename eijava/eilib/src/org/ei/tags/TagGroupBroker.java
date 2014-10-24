/*
 * Created on 17.10.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.tags;

import java.sql.*;
import java.util.*;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;
import org.ei.util.*;

/**
 * @author Tsolovye
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TagGroupBroker
{
    private Map groupCache;
    private String userID;

    public void cacheGroups(String userID,
                            boolean deep)
        throws Exception
    {
//        System.out.println("Caching groups."+userID);

        this.userID = userID;
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        groupCache = new HashMap();
        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement("SELECT * FROM TAG_GROUPS G, GROUP_MEMBERS M WHERE M.USER_ID = ? AND M.GROUP_ID = G.GROUP_ID");
            pstmt.setString(1, userID);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                TagGroup tg = fillgroup(rs);
                if(deep)
                {
                    //Get tags
                    TagBroker tagBroker = new TagBroker();
                    Tag[] tags = tagBroker.getTags(tg);
                    TimeComp comp = new TimeComp();
                    Arrays.sort(tags, comp);
                    tg.setTags(tags);

                    //Get members

                    Member[] members = getGroupMembers(tg);
                    tg.setMembers(members);
                }

                groupCache.put(rs.getString("group_id"), tg);
            }
        }
        finally
        {
            close(rs);
            close(pstmt);
            replace(con);
        }
    }

    public Member[] getGroupMembers(TagGroup group)
        throws Exception
    {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList membersid = new ArrayList();
        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement("SELECT upc.email, upc.user_profile_id, upc.title, upc.first_name, upc.last_name, cm.date_added FROM GROUP_MEMBERS cm, USER_PROFILE_CONTRACT upc WHERE cm.GROUP_ID = ? AND upc.user_profile_id = cm.user_id order by cm.date_added asc");
            pstmt.setString(1, group.getGroupID());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                membersid.add(new Member(rs.getString("user_profile_id"), rs.getString("email"), rs.getString("title"), rs.getString("first_name"), rs.getString("last_name")));
            }
        }
        finally
        {
            close(rs);
            close(pstmt);
            replace(con);
        }
        return (Member[])membersid.toArray(new Member[membersid.size()]);
    }



    public TagGroup[] getGroups(String _userID,
                                boolean deep)
        throws Exception
    {
        if(groupCache == null || (this.userID != null && !this.userID.equals(_userID)))
        {
//            System.out.println("Gettting groups");

            cacheGroups(_userID,
                        deep);

        }

        Collection groups = groupCache.values();
        TagGroup[] groupsArray = (TagGroup[])groups.toArray(new TagGroup[groups.size()]);
        Arrays.sort(groupsArray, new GroupCreateComp());
        return groupsArray;
    }

    public TagGroup getGroup(String groupID,
                             boolean deep)
        throws Exception
    {
        if(groupCache != null)
        {
            return (TagGroup)groupCache.get(groupID);
        }
        else
        {
            this.userID = userID;
            ConnectionBroker broker = null;
            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            groupCache = new HashMap();
            try
            {
                broker = ConnectionBroker.getInstance();
                con = broker.getConnection(DatabaseConfig.SESSION_POOL);
                pstmt = con.prepareStatement("SELECT * FROM TAG_GROUPS WHERE GROUP_ID = ?");
                pstmt.setString(1, groupID);
                rs = pstmt.executeQuery();
                TagGroup tg = null;
                if(rs.next())
                {
                    tg = fillgroup(rs);
                    if(deep)
                    {
                        //Get tags
                        TagBroker tagBroker = new TagBroker();
                        Tag[] tags = tagBroker.getTags(tg);
                        tg.setTags(tags);

                        //Get members

                        Member[] members = getGroupMembers(tg);
                        tg.setMembers(members);
                    }

                    return tg;
                }
            }
            finally
            {
                close(rs);
                close(pstmt);
                replace(con);
            }
        }

        return null;
    }

    private TagGroup fillgroup(ResultSet rset)
        throws Exception
    {
        TagGroup group = new TagGroup();
        group.setGroupID(rset.getString("group_id"));
        group.setTitle(rset.getString("group_title").trim());
        group.setDescription(rset.getString("description"));
        group.setDatefounded(rset.getLong("date_founded"));
        group.setColor(rset.getString("color"));
        group.setOwnerid(rset.getString("user_id"));

        return group;
    }


    public int updateGroup(TagGroup group,
                           String[] members)
        throws Exception
    {
        ConnectionBroker broker = null;
        Connection con = null;
        CallableStatement pstmt = null;
        int result = 0;
        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareCall("{ call TAGS_EDITGROUP(?,?,?)}");

            int intStmtIndex = 1;
            pstmt.setString(intStmtIndex++, group.getGroupID());
            pstmt.setString(intStmtIndex++, AlphaNumericFilter.alphaNumeric(group.getDescription()));
            pstmt.setString(intStmtIndex++, group.getColor());
            pstmt.executeUpdate();
            result = 1;
            String groupID = group.getGroupID();
            if(members != null)
            {
                removeMembers(groupID, members);
            }
        }
        finally
        {
            close(pstmt);
            replace(con);
        }
        return result;
    }

    public int removeMembers(String groupID,
                             String[] members)
        throws Exception
    {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        int result = 0;
        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            StringBuffer buf = new StringBuffer("delete from group_members where group_id = ? and user_id in (");
            buf.append(getMemberString(members));
            buf.append(")");
            pstmt = con.prepareStatement(buf.toString());
            pstmt.setString(1, groupID);
            pstmt.executeUpdate();
            result = 1;
        }
        finally
        {
            close(pstmt);
            replace(con);
        }
        return result;
    }


    public String addGroup(TagGroup cg)
        throws Exception
    {
        ConnectionBroker broker = null;
        Connection con = null;
        CallableStatement pstmt = null;
        String gid = null;
        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareCall("{ call TAGS_ADDGROUP(?,?,?,?,?,?)}");
            int intStmtIndex = 1;
            gid = new GUID().toString();
            cg.setGroupID(gid);
            pstmt.setString(intStmtIndex++, gid);
            pstmt.setString(intStmtIndex++, AlphaNumericFilter.alphaNumeric(cg.getTitle().trim()));
            pstmt.setString(intStmtIndex++, cg.getOwnerid().trim());
            pstmt.setString(intStmtIndex++, AlphaNumericFilter.alphaNumeric(cg.getDescription().trim()));
            pstmt.setLong(intStmtIndex++, cg.getDatefounded());
            pstmt.setString(intStmtIndex++, cg.getColor().trim());
            pstmt.executeUpdate();
            addMember(gid, cg.getOwnerid().trim());
        }
        finally
        {
            close(pstmt);
            replace(con);
        }
        return gid;
    }


    public int addMember(String groupID, String userID)
        throws Exception
    {
        ConnectionBroker broker = null;
        Connection con = null;
        CallableStatement pstmt = null;
        int result = 0;
        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareCall("{ call TAGS_ADDGROUPMEMBERS(?,?,?)}");
            long curTime = System.currentTimeMillis();
            int intStmtIndex = 1;
            pstmt.setString(intStmtIndex++, groupID);
            pstmt.setString(intStmtIndex++, userID.trim());
            pstmt.setLong(intStmtIndex++, curTime);
            pstmt.executeUpdate();
            result = 1;
        }
        catch(Exception e)
        {
			String message = e.getMessage();
			if(message.indexOf("unique constraint") == -1)
			{
				throw e;
			}
			else
			{
				e.printStackTrace();
			}
		}
        finally
        {
            close(pstmt);
            replace(con);
        }
        return result;
    }

    public int removeGroup(String groupID)
        throws Exception
    {
        ConnectionBroker broker = null;
        Connection con = null;
        CallableStatement pstmt = null;
        int result = 0;
        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareCall("{ call TAGS_DELETEGROUP(?)}");
            pstmt.setString(1, groupID);
            pstmt.executeUpdate();
            result = 1;
        }
        finally
        {
            close(pstmt);
            replace(con);
        }
        return result;
    }


    private String getMemberString(String[] members)
    {
        StringBuffer buf = new StringBuffer();
        for(int i=0; i<members.length; i++)
        {
            if(i > 0)
            {
                buf.append(",");
            }
            buf.append("'");
            buf.append(members[i]);
            buf.append("'");
        }

        return buf.toString();
    }



    private void close(ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void close(PreparedStatement pstmt)
    {
        if (pstmt != null)
        {
            try
            {
                pstmt.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void close(Statement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void replace(Connection con)
    {
        if (con != null)
        {
            try
            {
                ConnectionBroker broker = ConnectionBroker.getInstance();
                broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
            }
            catch (Exception cpe)
            {
                cpe.printStackTrace();
            }
        }
    }

}
