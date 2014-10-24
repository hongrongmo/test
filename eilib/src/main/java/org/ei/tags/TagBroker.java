/*
 * Created on 21.09.2006
 *
 */
package org.ei.tags;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.domain.Page;
import org.ei.domain.PageEntry;
import org.ei.domain.PageEntryBuilder;
import org.ei.util.MD5Digester;

public class TagBroker {
    private TagGroupBroker groupBroker;

    public static final int DROPDOWNTERMS = 20;
    public static final String DELIM = ";";
    public static final int CONSOLIDATED_TAG_RETRIEVAL = 75;

    // Constructor from TagGroupBroker
    public TagBroker(TagGroupBroker groupBroker) {
        this.groupBroker = groupBroker;
    }

    // Default constructor...
    public TagBroker() {
    }

    /**
     * 
     * @param tag
     * @param scope
     * @param groupID
     * @param customerID
     * @param userID
     * @return
     * @throws Exception
     */
    public String autocompleteTags(String tag, int scope, String groupID, String customerID, String userID) throws Exception {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        StringBuffer tags = new StringBuffer();
        ResultSet rs = null;
        int counter = 0;
        String groupId = null;
        StringBuffer qbuf = null;

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            if (scope == Scope.SCOPE_PUBLIC) {
                qbuf = new StringBuffer("SELECT * FROM CONSOLIDATED_TAGS WHERE COUNT>0 AND TAG_SEARCH LIKE '" + tag.toUpperCase() + "%'  AND SCOPE = 1 ");
            } else if (scope == Scope.SCOPE_PRIVATE) {
                qbuf = new StringBuffer("SELECT * FROM CONSOLIDATED_TAGS WHERE COUNT>0 AND TAG_SEARCH LIKE '" + tag.toUpperCase()
                    + "%'  AND SCOPE = 2 AND USER_ID = '");
                qbuf.append(userID);
                qbuf.append("'");
            } else if (scope == Scope.SCOPE_INSTITUTION) {

                qbuf = new StringBuffer("SELECT * FROM CONSOLIDATED_TAGS WHERE COUNT>0 AND TAG_SEARCH LIKE '" + tag.toUpperCase()
                    + "%'  AND SCOPE = 4 AND CUSTOMER_ID = '");
                qbuf.append(customerID);
                qbuf.append("'");

            } else if (scope == Scope.SCOPE_GROUP) {
                qbuf = new StringBuffer("SELECT * FROM CONSOLIDATED_TAGS WHERE COUNT>0 AND TAG_SEARCH LIKE '" + tag.toUpperCase()
                    + "%'  AND SCOPE = 3 AND GROUP_ID = '");
                qbuf.append(groupID);
                qbuf.append("'");
            }

            qbuf.append(" order by TAG_SEARCH asc");

            pstmt = con.prepareStatement(qbuf.toString());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String tirm = rs.getString("TAG_SEARCH");
                if (tirm.length() < TagBroker.DROPDOWNTERMS) {
                    tags.append(DELIM).append(tirm.toLowerCase().trim());
                    if (counter++ > 20) {
                        break;
                    }
                }
            }
        } finally {
            close(rs);
            close(pstmt);
            replace(con);
        }
        return tags.toString();
    }

    /**
     * 
     * @param tag
     * @return
     * @throws Exception
     */
    public String autocompleteTags(String tag) throws Exception {
        return autocompleteTags(tag, Scope.SCOPE_PUBLIC, null, null, null);
    }

    /**
     * 
     * @param count
     * @param scope
     * @param userID
     * @param customerID
     * @param groupID
     * @return
     * @throws Exception
     */
    public Tag[] getConsolidatedTags(int count, int scope, String userID, String customerID, String groupID) throws Exception {
        ConnectionBroker broker = null;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<Tag> list = new ArrayList<Tag>();

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);

            StringBuffer sqlbuf = new StringBuffer("select * from consolidated_tags where count > 0 AND scope = ");
            sqlbuf.append(scope);
            sqlbuf.append(" ");
            if (scope == Scope.SCOPE_GROUP) {
                sqlbuf.append("AND group_id = '");
                sqlbuf.append(groupID);
                sqlbuf.append("'");
            } else if (scope == Scope.SCOPE_PRIVATE) {
                sqlbuf.append("AND user_id = '");
                sqlbuf.append(userID);
                sqlbuf.append("'");
            } else if (scope == Scope.SCOPE_INSTITUTION) {
                sqlbuf.append("AND customer_id = '");
                sqlbuf.append(customerID);
                sqlbuf.append("'");
            }

            sqlbuf.append(" order by count desc");
            stmt = con.createStatement();
            rs = stmt.executeQuery(sqlbuf.toString());
            int i = 0;
            while (rs.next() && i < count) {
                Tag tag = fillTag(rs, null, true);
                list.add(tag);
                i++;
            }
        } finally {
            close(rs);
            close(stmt);
            replace(con);
        }

        return (Tag[]) list.toArray(new Tag[list.size()]);

    }

    /**
     * 
     * @param tag
     * @param scope
     * @param userID
     * @param customerID
     * @param groupID
     * @param index
     * @param sessionID
     * @param dataformat
     * @param credentials
     * @return
     * @throws Exception
     */
    public PageEntry getPageEntry(String tag, int scope, String userID, String customerID, String groupID, int index, String sessionID, String dataformat,
        String[] credentials) throws Exception {
        Page opage = getPage(tag, scope, userID, customerID, groupID, index, 1, sessionID, dataformat, credentials);
        PageEntry entry = opage.entryAt(0);
        return entry;
    }

    /**
     * 
     * @param tag
     * @param scope
     * @param userID
     * @param customerID
     * @param groupID
     * @param credentials
     * @return
     * @throws Exception
     */
    public int count(String tag, int scope, String userID, String customerID, String groupID, String[] credentials) throws Exception {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = -1;
        StringBuffer sqlbuf = new StringBuffer("select count(*) co from tags where tag_search = ? and scope = ?");

        if (scope == Scope.SCOPE_GROUP) {
            if (groupID != null) {
                sqlbuf.append(" AND group_id = ?");
            } else {
                throw new Exception("Group ID must be set");
            }
        } else if (scope == Scope.SCOPE_INSTITUTION) {
            if (customerID != null) {
                sqlbuf.append(" AND customer_id = ?");
            } else {
                throw new Exception("Customer ID must be set");
            }
        } else if (scope == Scope.SCOPE_PRIVATE) {
            if (userID != null) {
                sqlbuf.append(" AND user_id = ?");
            } else {
                throw new Exception("User ID must be set");
            }
        }

        sqlbuf.append(credentialsFilter(credentials));

        String sql = sqlbuf.toString();

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, tag.trim().toUpperCase());
            pstmt.setInt(2, scope);
            if (scope == Scope.SCOPE_GROUP) {
                if (groupID != null) {
                    pstmt.setString(3, groupID);
                }
            } else if (scope == Scope.SCOPE_INSTITUTION) {
                if (customerID != null) {
                    pstmt.setString(3, customerID);
                }
            } else if (scope == Scope.SCOPE_PRIVATE) {
                if (userID != null) {
                    pstmt.setString(3, userID);
                }
            }

            rs = pstmt.executeQuery();
            rs.next();
            count = rs.getInt("co");
        } finally {
            close(rs);
            close(pstmt);
            replace(con);
        }

        return count;

    }

    /**
     * 
     * @param tag
     * @param scope
     * @param userID
     * @param customerID
     * @param groupID
     * @param index
     * @param pagesize
     * @param sessionID
     * @param dataformat
     * @param credentials
     * @return
     * @throws Exception
     */
    public Page getPage(String tag, int scope, String userID, String customerID, String groupID, int index, int pagesize, String sessionID, String dataformat,
        String[] credentials) throws Exception {

        Page page = new Page();
        List<DocID> docIDs = getDocIDs(tag, scope, userID, customerID, groupID, index, pagesize, credentials);

        MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
        List<?> docs = builder.buildPage(docIDs, dataformat);
        PageEntryBuilder peb = new PageEntryBuilder(sessionID);
        List<PageEntry> pageEntries = peb.buildPageEntryList(docs);
        page.addAll(pageEntries);
        return page;
    }

    /**
     * 
     * @param tag
     * @param scope
     * @param userID
     * @param customerID
     * @param groupID
     * @param index
     * @param pagesize
     * @param credentials
     * @return
     * @throws Exception
     */
    public List<DocID> getDocIDs(String tag, int scope, String userID, String customerID, String groupID, int index, int pagesize, String[] credentials)
        throws Exception {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<DocID> docIDs = null;

        StringBuffer sqlbuf = new StringBuffer("select * from tags where tag_search = ? and scope = ?");

        if (scope == Scope.SCOPE_GROUP) {
            if (groupID != null) {
                sqlbuf.append(" AND group_id = ?");
            } else {
                throw new Exception("Group ID must be set");
            }
        } else if (scope == Scope.SCOPE_INSTITUTION) {
            if (customerID != null) {
                sqlbuf.append(" AND customer_id = ?");
            } else {
                throw new Exception("Customer ID must be set");
            }
        } else if (scope == Scope.SCOPE_PRIVATE) {
            if (userID != null) {
                sqlbuf.append(" AND user_id = ?");
            } else {
                throw new Exception("User ID must be set");
            }
        }

        sqlbuf.append(credentialsFilter(credentials));
        sqlbuf.append(" ORDER BY last_touched desc");

        String sql = sqlbuf.toString();

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            DatabaseConfig dconfig = DatabaseConfig.getInstance();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, tag.trim().toUpperCase());
            pstmt.setInt(2, scope);
            if (scope == Scope.SCOPE_GROUP) {
                if (groupID != null) {
                    pstmt.setString(3, groupID);
                }
            } else if (scope == Scope.SCOPE_INSTITUTION) {
                if (customerID != null) {
                    pstmt.setString(3, customerID);
                }
            } else if (scope == Scope.SCOPE_PRIVATE) {
                if (userID != null) {
                    pstmt.setString(3, userID);
                }
            }

            rs = pstmt.executeQuery();
            for (int i = 1; i < index; i++) {
                if (!rs.next()) {
                    break;
                }
            }

            docIDs = new ArrayList<DocID>();
            GregorianCalendar calendar = new GregorianCalendar();
            for (int i = 0; i < pagesize; i++) {
                if (rs.next()) {
                    String did = rs.getString("doc_id");
                    long timestamp = rs.getLong("LAST_TOUCHED");
                    String MMDDYYYY = getMMDDYYYY(timestamp, calendar);
                    DocID docID = new DocID(index, did, dconfig.getDatabase(did.substring(0, 3)));
                    docID.setTagDate(MMDDYYYY);
                    docIDs.add(docID);
                    index++;
                } else {
                    break;
                }
            }
        } finally {
            close(rs);
            close(pstmt);
            replace(con);
        }

        return docIDs;
    }

    /**
     * 
     * @param credentials
     * @return
     */
    private String credentialsFilter(String credentials[]) {
        StringBuffer buf = new StringBuffer();
        boolean comma = false;
        boolean book = false;
        boolean perpetual = false;
        buf.append(" AND (DB in (");
        DatabaseConfig config = DatabaseConfig.getInstance();

        for (int i = 0; i < credentials.length; i++) {
            String cred = credentials[i];
            if (cred.equals("BPE")) {
                perpetual = true;
            }

            Database database = config.getDatabase(cred.toLowerCase());
            if (database != null) {
                int mask = database.getMask();
                if (mask == 131072) {
                    book = true;
                } else {
                    if (comma) {
                        buf.append(",");
                    }
                    buf.append(Integer.toString(mask));
                    comma = true;
                }
            }
        }
        buf.append(")");
        comma = false;
        if (book) {
            buf.append(" OR (DB = 131072 AND COLLECTION in (");
            for (int i = 0; i < credentials.length; i++) {
                if (credentials[i].toLowerCase().indexOf("ele") == 0 || credentials[i].toLowerCase().indexOf("che") == 0
                    || credentials[i].toLowerCase().indexOf("mat") == 0 || credentials[i].toLowerCase().indexOf("sec") == 0
                    || credentials[i].toLowerCase().indexOf("com") == 0 || credentials[i].toLowerCase().indexOf("civ") == 0) {
                    String cred = credentials[i];
                    if (comma) {
                        buf.append(",");
                    }
                    buf.append("'");
                    buf.append(cred);
                    buf.append("'");
                    if (!perpetual) {
                        buf.append(",'");
                        buf.append(cred);
                        buf.append("1");
                        buf.append("'");
                        buf.append(",'");
                        buf.append(cred);
                        buf.append("2");
                        buf.append("'");
                        buf.append(",'");
                        buf.append(cred);
                        buf.append("3");
                        buf.append("'");
                    }
                    comma = true;
                }
            }
            buf.append("))");
        }
        buf.append(")");
        return buf.toString();
    }

    /**
     * 
     * @param timeStamp
     * @param calendar
     * @return
     */
    public String getMMDDYYYY(long timeStamp, GregorianCalendar calendar) {
        StringBuffer MMDDYYYY = new StringBuffer();
        calendar.setTimeInMillis(timeStamp);
        MMDDYYYY.append(Integer.toString(calendar.get(Calendar.MONTH) + 1));
        MMDDYYYY.append("/");
        MMDDYYYY.append(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
        MMDDYYYY.append("/");
        MMDDYYYY.append(Integer.toString(calendar.get(Calendar.YEAR)));
        return MMDDYYYY.toString();
    }

    public Tag[] getTags(String docID, String userID, String customerID, Comparator<Tag> comp) throws Exception {
        Tag[] tagsArray = null;
        ArrayList<Tag> tags = new ArrayList<Tag>();

        ConnectionBroker broker = null;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            StringBuffer sql = new StringBuffer("select * from tags where  doc_id = '");
            sql.append(docID);
            sql.append("' and user_id != '1' and (scope = 1");
            sql.append(" OR (scope = 4 and customer_id ='");
            sql.append(customerID);
            sql.append("')");

            if (userID != null) {
                sql.append(" OR (scope = 2 and user_id ='");
                sql.append(userID);
                sql.append("')");
            }

            TagGroup[] groupArray = null;

            if (groupBroker != null) {
                groupArray = groupBroker.getGroups(userID, false);
            }

            if (groupArray != null && groupArray.length > 0) {
                sql.append(" OR ( scope = 3 AND group_id in (");

                for (int i = 0; i < groupArray.length; i++) {
                    TagGroup group = groupArray[i];
                    String groupID = group.getGroupID();
                    if (i > 0) {
                        sql.append(",");
                    }
                    sql.append("'");
                    sql.append(groupID);
                    sql.append("'");
                }
                sql.append("))");
            }
            sql.append(")");

            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                tags.add(fillTag(rs));
            }

            tagsArray = (Tag[]) tags.toArray(new Tag[tags.size()]);
            Arrays.sort(tagsArray, comp);
        } finally {
            close(rs);
            close(stmt);
            replace(con);

        }

        return tagsArray;
    }

    /**
     * 
     * @param group
     * @return
     * @throws Exception
     */
    public Tag[] getTags(TagGroup group) throws Exception {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Tag> groupTags = new ArrayList<Tag>();

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement("select * from consolidated_tags where group_id = ? and count > 0 order by last_touched desc");
            pstmt.setString(1, group.getGroupID());
            rs = pstmt.executeQuery();
            TagGroup shallowCopy = group.shallowCopy();
            while (rs.next()) {
                groupTags.add(fillTag(rs, shallowCopy, true));
            }
        }

        finally {
            close(rs);
            close(pstmt);
            replace(con);
        }

        return (Tag[]) groupTags.toArray(new Tag[groupTags.size()]);
    }

    /**
     * 
     * @param rset
     * @return
     * @throws Exception
     */
    private Tag fillTag(ResultSet rset) throws Exception {
        return fillTag(rset, null, false);
    }

    /**
     * 
     * @param rset
     * @param group
     * @param consolidated
     * @return
     * @throws Exception
     */
    private Tag fillTag(ResultSet rset, TagGroup group, boolean consolidated) throws Exception {
        Tag tag = null;
        tag = new Tag();
        tag.setTagID(rset.getString("T_ID"));
        String groupID = rset.getString("GROUP_ID");
        if (groupID != null) {
            if (group == null) {
                tag.setGroupID(rset.getString("GROUP_ID"));
                if (this.groupBroker != null) {
                    TagGroup g = groupBroker.getGroup(groupID, false);
                    tag.setGroup(g);
                }
            } else {
                tag.setGroup(group);
            }
        }

        tag.setTag(rset.getString("TAG"));
        if (!consolidated) {
            tag.setDocID(rset.getString("DOC_ID"));
        }
        tag.setScope(rset.getInt("SCOPE"));
        if (!consolidated) {
            tag.setMask(rset.getInt("DB"));
        }
        tag.setUserID(rset.getString("USER_ID"));
        tag.setCustID(rset.getString("CUSTOMER_ID"));
        tag.setTimestamp(rset.getLong("LAST_TOUCHED"));
        if (consolidated) {
            tag.setCount(rset.getInt("count"));
        }

        return tag;
    }

    /**
     * 
     * @param scope
     * @param userID
     * @param customerID
     * @param groupID
     * @return
     * @throws Exception
     */
    public String[] getUserTagNames(int scope, String userID, String customerID, String groupID) throws Exception {

        List<String> usertags = new ArrayList<String>();
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        int sc = 0;
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            StringBuffer buf = new StringBuffer();
            buf.append("SELECT DISTINCT(TAG_SEARCH) FROM tags  WHERE SCOPE = ? AND USER_ID = ?");
            if (scope == Scope.SCOPE_GROUP) {
                buf.append(" AND  GROUP_ID = ? ");
            } else if (scope == Scope.SCOPE_INSTITUTION) {
                buf.append(" AND  CUSTOMER_ID = ? ");
            }

            buf.append(" ORDER BY TAG_SEARCH ");

            pstmt = con.prepareStatement(buf.toString());
            pstmt.setInt(1, scope);
            pstmt.setString(2, userID);

            if (scope == Scope.SCOPE_GROUP) {
                pstmt.setString(3, groupID);
            } else if (scope == Scope.SCOPE_INSTITUTION) {
                pstmt.setString(3, customerID);
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                usertags.add(rs.getString("tag_search"));
            }
        } finally {
            close(rs);
            close(pstmt);
            replace(con);
        }

        return (String[]) usertags.toArray(new String[usertags.size()]);
    }

    /**
     * 
     * @param tag
     * @param scope
     * @param user
     * @param customerID
     * @param groupID
     * @param docID
     */
    public void deleteTag(String tag, int scope, String user, String customerID, String groupID, String docID) {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;

        StringBuffer deleteBuf = new StringBuffer("delete tags where TAG_SEARCH = ? AND USER_ID = ? AND SCOPE = ?");
        if (scope == Scope.SCOPE_GROUP) {
            deleteBuf.append(" AND GROUP_ID ='");
            deleteBuf.append(groupID);
            deleteBuf.append("'");
        } else if (scope == Scope.SCOPE_INSTITUTION) {
            deleteBuf.append(" AND CUSTOMER_ID ='");
            deleteBuf.append(customerID);
            deleteBuf.append("'");
        }

        if (docID != null) {
            deleteBuf.append(" AND doc_id = '");
            deleteBuf.append(docID);
            deleteBuf.append("'");
        }

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            con.setAutoCommit(false);
            pstmt = con.prepareStatement(deleteBuf.toString());
            pstmt.setString(1, tag.toUpperCase().trim());
            pstmt.setString(2, user);
            pstmt.setInt(3, scope);
            int count = pstmt.executeUpdate();
            Operation operation = new Operation("-", count);
            updateConsolidatedTag(tag.trim().toUpperCase(), scope, user, groupID, customerID, operation, con);
            con.commit();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception re) {
                re.printStackTrace();
            }
        } finally {
            close(pstmt);
            replace(con);
        }
    }

    /**
     * 
     * @param oldtag
     * @param newtag
     * @param userID
     * @param customerID
     * @param groupID
     * @param scope
     * @throws Exception
     */
    public void updateTagName(String oldtag, String newtag, String userID, String customerID, String groupID, int scope) throws Exception {

        TagFilter badInput = new TagFilter();
        newtag = badInput.filterParameter(newtag);
        if ((newtag == null) || newtag.trim().length() == 0) {
            return;
        }

        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;

        StringBuffer updatebuf = new StringBuffer("update tags set TAG = ? , TAG_SEARCH = ? where TAG_SEARCH = ? AND USER_ID = ? AND SCOPE = ?");

        if (groupID != null && scope == Scope.SCOPE_GROUP) {
            updatebuf.append(" AND GROUP_ID = ?");
        } else if (customerID != null && scope == Scope.SCOPE_INSTITUTION) {
            updatebuf.append(" AND CUSTOMER_ID = ?");
        }

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            con.setAutoCommit(false);
            pstmt = con.prepareStatement(updatebuf.toString());
            pstmt.setString(1, newtag);
            pstmt.setString(2, newtag.toUpperCase().trim());
            pstmt.setString(3, oldtag.toUpperCase().trim());
            pstmt.setString(4, userID);
            pstmt.setInt(5, scope);
            if (groupID != null && scope == Scope.SCOPE_GROUP) {
                pstmt.setString(6, groupID);
            } else if (customerID != null && scope == Scope.SCOPE_INSTITUTION) {
                pstmt.setString(6, customerID);
            }

            int count = pstmt.executeUpdate();
            Operation addOperation = new Operation("+", count);
            Operation subtractOperation = new Operation("-", count);

            Tag nTag = new Tag();
            fillTag(nTag, newtag, userID, customerID, groupID, scope);

            addConsolidatedTag(nTag, addOperation, con);

            Tag oTag = new Tag();
            fillTag(oTag, oldtag, userID, customerID, groupID, scope);

            addConsolidatedTag(oTag, subtractOperation, con);
            con.commit();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception re) {
                re.printStackTrace();
            }
        } finally {
            close(pstmt);
            replace(con);
        }

        // Clean out any dups that the user might have created when updating tag names.
        removeDuplicates(userID);
    }

    /**
     * 
     * @param tag
     * @param atag
     * @param userID
     * @param customerID
     * @param groupID
     * @param scope
     * @throws TagException
     */
    private void fillTag(Tag tag, String atag, String userID, String customerID, String groupID, int scope) throws TagException {
        tag.setTag(atag);
        tag.setScope(scope);
        tag.setGroupID(groupID);
        tag.setCustID(customerID);
        tag.setUserID(userID);
    }

    /**
     * 
     * @param tag
     * @throws Exception
     */
    public void addTag(Tag tag) throws Exception {
        TagFilter badInput = new TagFilter();
        String filteredValue = badInput.filterParameter(tag.getTag());
        if ((filteredValue == null) || filteredValue.trim().length() == 0) {
            return;
        } else {
            tag.setTag(filteredValue);
        }

        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            con.setAutoCommit(false);
            pstmt = con.prepareStatement("insert into tags values (?,?,?,?,?,?,?,?,?,?,?,?)");
            int intStmtIndex = 1;
            String tagID = getTagID(tag);
            pstmt.setString(intStmtIndex++, tagID);
            pstmt.setString(intStmtIndex++, tag.getTag().trim());
            pstmt.setString(intStmtIndex++, tag.getTag().toUpperCase().trim());
            pstmt.setString(intStmtIndex++, tag.getDocID());
            pstmt.setLong(intStmtIndex++, tag.getTimestamp());
            pstmt.setInt(intStmtIndex++, tag.getScope());
            pstmt.setInt(intStmtIndex++, tag.getMask());
            pstmt.setString(intStmtIndex++, tag.getUserID());
            pstmt.setString(intStmtIndex++, tag.getCustID());
            pstmt.setString(intStmtIndex++, tag.getGroupID());
            pstmt.setString(intStmtIndex++, tag.getCollection());
            pstmt.setString(intStmtIndex++, "");
            pstmt.executeUpdate();
            addConsolidatedTag(tag, new Operation("+", 1), con);
            con.commit();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception re) {
                re.printStackTrace();
            }

            String message = e.getMessage();
            if (message.indexOf("unique constraint") == -1) {
                throw e;
            } else {
                e.printStackTrace();
            }
        } finally {
            close(pstmt);
            replace(con);
        }
    }

    /**
     * 
     * @param tag
     * @return
     * @throws Exception
     */
    private String getTagID(Tag tag) throws Exception {
        int scope = tag.getScope();
        String docID = tag.getDocID();
        String searchTag = tag.getTagSearchValue();
        String userID = tag.getUserID();
        String groupID = tag.getGroupID();
        String customerID = tag.getCustID();
        StringBuffer id = new StringBuffer();

        id.append(Integer.toString(scope));
        id.append("::");
        id.append(searchTag);
        id.append("::");
        id.append(docID);

        if (scope == Scope.SCOPE_PRIVATE) {
            id.append("::");
            id.append(userID);
        } else if (scope == Scope.SCOPE_GROUP) {
            id.append("::");
            id.append(groupID);
        } else if (scope == Scope.SCOPE_INSTITUTION) {
            id.append("::");
            id.append(customerID);
        }

        String sid = id.toString();
        MD5Digester digester = new MD5Digester();
        return digester.asHex(digester.digest(sid));
    }

    /**
     * 
     * @param tag
     * @param operation
     * @param con
     * @throws Exception
     */
    private void addConsolidatedTag(Tag tag, Operation operation, Connection con) throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int result = 0;
        try {
            int scope = tag.getScope();
            if (scope == Scope.SCOPE_PUBLIC) {
                pstmt = con.prepareStatement("select * from CONSOLIDATED_TAGS where TAG_SEARCH = ? and SCOPE = ?");
                int intStmtIndex = 1;
                pstmt.setString(intStmtIndex++, tag.getTagSearchValue());
                pstmt.setInt(intStmtIndex++, tag.getScope());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    updateConsolidatedTag(tag.getTagSearchValue(), scope, null, null, null, operation, con);
                } else {
                    insertConsolidatedTag(tag, operation.getCount(), con);
                }
            } else if (scope == Scope.SCOPE_PRIVATE) {
                pstmt = con.prepareStatement("select * from CONSOLIDATED_TAGS where TAG_SEARCH = ? and SCOPE = ? and USER_ID = ?");
                int intStmtIndex = 1;
                pstmt.setString(intStmtIndex++, tag.getTagSearchValue().trim());
                pstmt.setInt(intStmtIndex++, tag.getScope());
                pstmt.setString(intStmtIndex++, tag.getUserID());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    updateConsolidatedTag(tag.getTagSearchValue(), scope, tag.getUserID(), null, null, operation, con);
                } else {
                    insertConsolidatedTag(tag, operation.getCount(), con);
                }

            } else if (scope == Scope.SCOPE_INSTITUTION) {
                pstmt = con.prepareStatement("select * from CONSOLIDATED_TAGS where TAG_SEARCH = ? and SCOPE = ? and CUSTOMER_ID = ?");
                int intStmtIndex = 1;
                pstmt.setString(intStmtIndex++, tag.getTagSearchValue().trim());
                pstmt.setInt(intStmtIndex++, tag.getScope());
                pstmt.setString(intStmtIndex++, tag.getCustID());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    updateConsolidatedTag(tag.getTagSearchValue(), scope, tag.getUserID(), null, tag.getCustID(), operation, con);
                } else {
                    insertConsolidatedTag(tag, operation.getCount(), con);
                }
            } else if (scope == Scope.SCOPE_GROUP) {

                pstmt = con.prepareStatement("select * from CONSOLIDATED_TAGS where TAG_SEARCH = ? and SCOPE = ? and GROUP_ID = ?");

                int intStmtIndex = 1;
                pstmt.setString(intStmtIndex++, tag.getTagSearchValue());
                pstmt.setInt(intStmtIndex++, tag.getScope());
                pstmt.setString(intStmtIndex++, tag.getGroupID());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    updateConsolidatedTag(tag.getTagSearchValue(), scope, null, tag.getGroupID(), null, operation, con);
                } else {
                    insertConsolidatedTag(tag, operation.getCount(), con);
                }
            }
        } finally {
            close(rs);
            close(pstmt);
        }
    }

    /**
     * 
     * @param tagSearch
     * @param scope
     * @param userID
     * @param groupID
     * @param customerID
     * @param operation
     * @param con
     * @throws Exception
     */
    private void updateConsolidatedTag(String tagSearch, int scope, String userID, String groupID, String customerID, Operation operation, Connection con)
        throws Exception {

        PreparedStatement pstmt = null;
        try {

            boolean touch = false;
            StringBuffer sql = new StringBuffer("update CONSOLIDATED_TAGS set count = count");

            sql.append(operation.toString());
            if (operation.getOperation().equals("+")) {
                long currentTime = System.currentTimeMillis();
                String timeStamp = Long.toString(currentTime);
                touch = true;
                sql.append(", last_touched =");
                sql.append(timeStamp);
            }

            sql.append(" where TAG_SEARCH = ? and SCOPE = ?");

            if (scope == Scope.SCOPE_PUBLIC) {
                pstmt = con.prepareStatement(sql.toString());
                pstmt.setString(1, tagSearch);
                pstmt.setInt(2, scope);
                pstmt.executeUpdate();
            } else if (scope == Scope.SCOPE_PRIVATE) {
                sql.append(" and USER_ID = ?");
                pstmt = con.prepareStatement(sql.toString());
                pstmt.setString(1, tagSearch);
                pstmt.setInt(2, scope);
                pstmt.setString(3, userID);
                pstmt.executeUpdate();
            } else if (scope == Scope.SCOPE_INSTITUTION) {
                sql.append(" and CUSTOMER_ID = ?");
                pstmt = con.prepareStatement(sql.toString());
                pstmt.setString(1, tagSearch);
                pstmt.setInt(2, scope);
                pstmt.setString(3, customerID);
                pstmt.executeUpdate();
            } else if (scope == Scope.SCOPE_GROUP) {
                sql.append(" and GROUP_ID = ?");
                pstmt = con.prepareStatement(sql.toString());
                pstmt.setString(1, tagSearch);
                pstmt.setInt(2, scope);
                pstmt.setString(3, groupID);
                pstmt.executeUpdate();
            }
        } finally {
            close(pstmt);
        }
    }

    /**
     * 
     * @param tag
     * @param count
     * @param con
     * @throws Exception
     */
    private void insertConsolidatedTag(Tag tag, int count, Connection con) throws Exception {
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("insert into CONSOLIDATED_TAGS values (?,?,?,?,?,?,?,?,?)");
            int intStmtIndex = 1;
            String tagid = getConsolidatedTagID(tag);
            pstmt.setString(intStmtIndex++, tagid);
            pstmt.setString(intStmtIndex++, tag.getTag().trim());
            pstmt.setString(intStmtIndex++, tag.getTag().toUpperCase().trim());
            pstmt.setLong(intStmtIndex++, tag.getTimestamp());
            pstmt.setInt(intStmtIndex++, tag.getScope());
            pstmt.setString(intStmtIndex++, tag.getUserID());
            pstmt.setString(intStmtIndex++, tag.getCustID());
            pstmt.setString(intStmtIndex++, tag.getGroupID());
            pstmt.setInt(intStmtIndex++, count);
            pstmt.executeUpdate();
        } finally {
            close(pstmt);
        }
    }

    /**
     * 
     * @param tag
     * @return
     * @throws Exception
     */
    private String getConsolidatedTagID(Tag tag) throws Exception {
        int scope = tag.getScope();
        String searchTag = tag.getTagSearchValue();
        String userID = tag.getUserID();
        String groupID = tag.getGroupID();
        String customerID = tag.getCustID();
        StringBuffer id = new StringBuffer();

        id.append(Integer.toString(scope));
        id.append("::");
        id.append(searchTag);

        if (scope == Scope.SCOPE_PRIVATE) {
            id.append("::");
            id.append(userID);
        } else if (scope == Scope.SCOPE_GROUP) {
            id.append("::");
            id.append(groupID);
        } else if (scope == Scope.SCOPE_INSTITUTION) {
            id.append("::");
            id.append(customerID);
        }

        String sid = id.toString();
        MD5Digester digester = new MD5Digester();
        return digester.asHex(digester.digest(sid));
    }

    /**
     * A general method for placing housekeeping tasks.
     **/

    public void houseKeeping() throws Exception {
        removeDuplicates();
    }

    /**
     * 
     * @throws Exception
     */
    private void removeDuplicates() throws Exception {
        removeDuplicates(null);
    }

    /**
     * 
     * @param userID
     * @throws Exception
     */
    private void removeDuplicates(String userID) throws Exception {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer("select * from tags ");
        if (userID != null) {
            buf.append("where user_id = '");
            buf.append(userID);
            buf.append("'");
        } else {
            buf.append("where customer_id != '1'");
        }

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement(buf.toString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String tagid = rs.getString("t_id");
                Tag tag = fillTag(rs);
                String calctagid = getTagID(tag);
                if (!tagid.equals(calctagid)) {
                    try {
                        updateTagID(tagid, calctagid, con);
                    } catch (Exception e) {
                        /**
                         * In the case of an exception check to see if it was a unique constraint problem. If it is then we have found a dup, so remove it.
                         **/

                        String message = e.getMessage();
                        if (message != null && message.indexOf("unique constraint") != -1) {
                            System.out.println("Removing duplicate tag:" + tag.getTag() + " " + tagid);
                            deleteTagByID(tagid, tag.getTag(), tag.getScope(), tag.getUserID(), tag.getCustID(), tag.getGroupID());
                        }
                    }
                }
            }
        } finally {
            close(rs);
            close(pstmt);
            replace(con);
        }
    }

    /**
     * 
     * @param oldID
     * @param newID
     * @param con
     * @throws Exception
     */
    private void updateTagID(String oldID, String newID, Connection con) throws Exception {
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("update tags set T_ID = ? where T_ID = ?");
            pstmt.setString(1, newID);
            pstmt.setString(2, oldID);
            pstmt.executeUpdate();
        } finally {
            close(pstmt);
        }
    }

    /**
     * 
     * @param tagID
     * @param tag
     * @param scope
     * @param user
     * @param customerID
     * @param groupID
     */
    private void deleteTagByID(String tagID, String tag, int scope, String user, String customerID, String groupID) {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            con.setAutoCommit(false);
            pstmt = con.prepareStatement("delete from tags where T_ID = ?");
            pstmt.setString(1, tagID);
            int count = pstmt.executeUpdate();
            if (count > 0) {
                Operation operation = new Operation("-", 1);
                updateConsolidatedTag(tag.trim().toUpperCase(), scope, user, groupID, customerID, operation, con);
            }
            con.commit();
        } catch (Exception e1) {
            try {
                con.rollback();
            } catch (Exception ce) {
                ce.printStackTrace();
            }
        } finally {
            close(pstmt);
            replace(con);
        }
    }

    //
    // Close methods
    //

    private void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void close(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void replace(Connection con) {
        if (con != null) {
            try {
                ConnectionBroker broker = ConnectionBroker.getInstance();
                con.setAutoCommit(true);
                broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
            } catch (Exception cpe) {
                cpe.printStackTrace();
            }
        }
    }

    /**
     * 
     * Inner class - Operation
     * 
     */
    class Operation {
        private String operation;

        private int count;

        public Operation(String operation, int count) {
            this.operation = operation;
            this.count = count;
        }

        public int getCount() {
            return this.count;
        }

        public String getOperation() {
            return this.operation;
        }

        public String toString() {
            return operation + Integer.toString(count);
        }
    }

}
