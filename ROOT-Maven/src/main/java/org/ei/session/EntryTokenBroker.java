package org.ei.session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;
import org.ei.util.GUID;
import org.ei.util.MD5Digester;

public class EntryTokenBroker {
    private Connection con;
    public static final String DEFAULT_CREDENTIALS = "CPX;NOP;RSS;OBO;THS";
    private static final String AUTH_POOL = DatabaseConfig.SESSION_POOL;

    public EntryTokenBroker(Connection con) throws Exception {
        if (con != null) {
            this.con = con;
        } else {
            this.con = getConnection(AUTH_POOL);
        }
    }

    public static void main(String[] args) throws Exception {
        String driver = args[0];
        String url = args[1];
        String username = args[2];
        String password = args[3];
        Connection con = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            EntryTokenBroker ebroker = new EntryTokenBroker(con);

            /*
             * EntryToken[] tokens = ebroker.getTokensByAccountID("accountID1"); for(int i=0; i<tokens.length; i++) { EntryToken et = tokens[i];
             * System.out.println("Token ID:"+et.getTokenID()); System.out.println("Campaign:"+Integer.toString(et.getCampaign()));
             * System.out.println("Account ID:"+et.getAccountID()); System.out.println("Credentials"+ et.getCredentials());
             * System.out.println("Create Time:"+Long.toString(et.getCreateTime())); System.out.println("Start Time:"+Long.toString(et.getStartTime()));
             * System.out.println("Expire Time:"+Long.toString(et.getExpireTime())); System.out.println("Session length:"+Long.toString(et.getSessionLength()));
             * System.out.println("Status:"+Integer.toString(et.getStatus())); System.out.println("==================================="); }
             */

            /*
             * EntryToken et = ebroker.getTokenByID("b927b51ff7a0ee8549e96aeb8ae14416"); System.out.println("Token ID:"+et.getTokenID());
             * System.out.println("Campaign:"+et.getCampaign()); System.out.println("Account ID:"+et.getAccountID()); System.out.println("Credentials"+
             * et.getCredentials()); System.out.println("Create Time:"+Long.toString(et.getCreateTime()));
             * System.out.println("Start Time:"+Long.toString(et.getStartTime())); System.out.println("Expire Time:"+Long.toString(et.getExpireTime()));
             * System.out.println("Session length:"+Long.toString(et.getSessionLength())); System.out.println("Status:"+Integer.toString(et.getStatus())); et =
             * ebroker.startToken("b927b51ff7a0ee8549e96aeb8ae14416"); System.out.println("Token ID:"+et.getTokenID());
             * System.out.println("Campaign:"+et.getCampaign()); System.out.println("Account ID:"+et.getAccountID()); System.out.println("Credentials"+
             * et.getCredentials()); System.out.println("Create Time:"+Long.toString(et.getCreateTime()));
             * System.out.println("Start Time:"+Long.toString(et.getStartTime())); System.out.println("Expire Time:"+Long.toString(et.getExpireTime()));
             * System.out.println("Session length:"+Long.toString(et.getSessionLength())); System.out.println("Status:"+Integer.toString(et.getStatus()));
             */

            long createTime = System.currentTimeMillis();
            System.out.println("Create time:" + createTime);
            long startTime = -1L;
            long inter = (1000L * 60L * 60L * 24L * 30L);
            // long inter = (1000L*60L*10);

            System.out.println("*****inter:" + inter);
            long expireTime = createTime + inter; // Thirty days after creation
            System.out.println("Expire time:" + expireTime);
            long sessionLength = 1000L * 60L * 60L * 24L; // One day
            String credentials = EntryTokenBroker.DEFAULT_CREDENTIALS;
            int campaign = 11111;
            String accountID = "accountID1";
            EntryToken et = ebroker.createToken(credentials, campaign, accountID, createTime, startTime, expireTime, sessionLength, EntryToken.STATUS_ON);

        } finally {
            con.close();
        }
    }

    private Connection getConnection(String authCon) throws Exception {
        ConnectionBroker broker = ConnectionBroker.getInstance();
        return broker.getConnection(authCon);
    }

    public void deleteOldTokens(String profileID) throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement("delete from entry_token where account_id = ?");
            pstmt.setString(1, profileID);
            rs = pstmt.executeQuery();
            con.commit();

        }

        finally {

            close(rs);
            close(pstmt);
        }

        return;
    }

    public EntryToken createToken(String profileID, int campaign) throws SessionException, SQLException {
        // first delete any token created for this user
        deleteOldTokens(profileID);

        EntryToken et = new EntryToken();
        PreparedStatement pstmt = null;
        long createTime = System.currentTimeMillis();
        System.out.println("Create time:" + createTime);
        long startTime = -1L;
        long inter = (1000L * 60L * 60L * 24L * 30L);
        // long inter = (1000L*60L*10);

        System.out.println("*****inter:" + inter);
        long expireTime = createTime + inter; // Thirty days after creation
        System.out.println("Expire time:" + expireTime);
        long sessionLength = 1000L * 60L * 60L * 24L; // One day

        try {
            String tokenID = createTokenID();
            et.setTokenID(tokenID);
            et.setCampaign(campaign);
            et.setAccountID(profileID);
            et.setCredentials(DEFAULT_CREDENTIALS);
            et.setCreateTime(createTime);
            et.setStartTime(startTime);
            et.setExpireTime(expireTime);
            et.setSessionLength(sessionLength);
            et.setStatus(EntryToken.STATUS_ON);
            pstmt = con.prepareStatement("insert into entry_token values (?,?,?,?,?,?,?,?,?)");
            pstmt.setString(1, tokenID);
            pstmt.setString(2, profileID);
            pstmt.setInt(3, campaign);
            pstmt.setString(4, DEFAULT_CREDENTIALS);
            pstmt.setLong(5, createTime);
            pstmt.setLong(6, startTime);
            pstmt.setLong(7, expireTime);
            pstmt.setLong(8, sessionLength);
            pstmt.setInt(9, EntryToken.STATUS_ON);
            int row = pstmt.executeUpdate();
            con.commit();
            System.out.println("*****rows added: " + row);
        } catch (Exception e) {
            throw new SessionException(SystemErrorCodes.ENTRY_TOKEN_ERROR, e);
        } finally {
            close(pstmt);
        }

        return et;
    }

    public EntryToken createToken(String credentials, int campaign, String accountID, long createTime, long startTime, long expireTime, long sessionLength,
        int status) throws SessionException {
        EntryToken et = new EntryToken();
        PreparedStatement pstmt = null;

        try {
            String tokenID = createTokenID();
            et.setTokenID(tokenID);
            et.setCampaign(campaign);
            et.setAccountID(accountID);
            et.setCredentials(credentials);
            et.setCreateTime(createTime);
            et.setStartTime(startTime);
            et.setExpireTime(expireTime);
            et.setSessionLength(sessionLength);
            et.setStatus(status);
            pstmt = con.prepareStatement("insert into entry_token values (?,?,?,?,?,?,?,?,?)");
            pstmt.setString(1, tokenID);
            pstmt.setString(2, accountID);
            pstmt.setInt(3, campaign);
            pstmt.setString(4, credentials);
            pstmt.setLong(5, createTime);
            pstmt.setLong(6, startTime);
            pstmt.setLong(7, expireTime);
            pstmt.setLong(8, sessionLength);
            pstmt.setInt(9, status);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new SessionException(SystemErrorCodes.ENTRY_TOKEN_ERROR, e);
        } finally {
            close(pstmt);
        }

        return et;
    }

    public EntryToken getTokenByID(String tokenID) throws SessionException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        EntryToken et = null;

        try {
            pstmt = con.prepareStatement("select * from entry_token where token_id = ?");
            pstmt.setString(1, tokenID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                et = fillEntryToken(rs);
            }
        } catch (Exception e) {
            throw new SessionException(SystemErrorCodes.ENTRY_TOKEN_ERROR, e);
        } finally {
            close(rs);
            close(pstmt);
        }

        return et;
    }

    public EntryToken startToken(String tokenID) throws SessionException {
        PreparedStatement pstmt = null;
        EntryToken et = null;

        try {
            pstmt = con.prepareStatement("update entry_token set start_time = ? where token_id = ? and start_time < 0");
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setString(2, tokenID);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new SessionException(SystemErrorCodes.ENTRY_TOKEN_ERROR, e);
        } finally {
            close(pstmt);
        }

        return getTokenByID(tokenID);
    }

    public void updateToken(EntryToken entryToken) throws SessionException {
        PreparedStatement pstmt = null;
        try {
            String tokenID = entryToken.getTokenID();
            int campaign = entryToken.getCampaign();
            String accountID = entryToken.getAccountID();
            String credentials = entryToken.getCredentials();
            long createTime = entryToken.getCreateTime();
            long startTime = entryToken.getStartTime();
            long expireTime = entryToken.getExpireTime();
            long sessionLength = entryToken.getSessionLength();
            int status = entryToken.getStatus();
            pstmt = con
                .prepareStatement("update entry_token set campaign = ?, account_id = ?, credentials = ?, create_time = ?, start_time = ?, expire_time = ?, session_length = ?, status = ? where token_id = ?");
            pstmt.setInt(1, campaign);
            pstmt.setString(2, accountID);
            pstmt.setString(3, credentials);
            pstmt.setLong(4, createTime);
            pstmt.setLong(5, startTime);
            pstmt.setLong(6, expireTime);
            pstmt.setLong(7, sessionLength);
            pstmt.setLong(8, status);
            pstmt.setString(9, tokenID);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new SessionException(SystemErrorCodes.ENTRY_TOKEN_ERROR, e);
        } finally {
            close(pstmt);
        }
    }

    public EntryToken[] getTokensByCampaign(String campaign) throws SessionException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        EntryToken[] ets = null;

        try {
            ArrayList<EntryToken> list = new ArrayList<EntryToken>();
            pstmt = con.prepareStatement("select * from entry_token where campaign = ?");
            pstmt.setString(1, campaign);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                EntryToken et = fillEntryToken(rs);
                list.add(et);
            }

            ets = (EntryToken[]) list.toArray(new EntryToken[list.size()]);
        } catch (Exception e) {
            throw new SessionException(SystemErrorCodes.ENTRY_TOKEN_ERROR, e);
        } finally {
            close(rs);
            close(pstmt);
        }

        return ets;
    }

    public EntryToken[] getTokensByCreateTime(long beginTime, long endTime) throws SessionException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        EntryToken[] ets = null;
        try {
            ArrayList<EntryToken> list = new ArrayList<EntryToken>();
            pstmt = con.prepareStatement("select * from entry_token where create_time >= ? and create_time<= ?");
            pstmt.setLong(1, beginTime);
            pstmt.setLong(2, endTime);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                EntryToken et = fillEntryToken(rs);
                list.add(et);
            }

            ets = (EntryToken[]) list.toArray(new EntryToken[list.size()]);
        } catch (Exception e) {
            throw new SessionException(SystemErrorCodes.ENTRY_TOKEN_ERROR, e);
        } finally {
            close(rs);
            close(pstmt);
        }

        return ets;
    }

    public EntryToken[] getTokensByAccountID(String accountID) throws SessionException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        EntryToken[] ets = null;

        try {
            ArrayList<EntryToken> list = new ArrayList<EntryToken>();
            pstmt = con.prepareStatement("select * from entry_token where account_id = ? order by create_time desc");
            pstmt.setString(1, accountID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                EntryToken et = fillEntryToken(rs);
                list.add(et);
            }

            ets = (EntryToken[]) list.toArray(new EntryToken[list.size()]);
        } catch (Exception e) {
            throw new SessionException(SystemErrorCodes.ENTRY_TOKEN_ERROR, e);
        } finally {
            close(rs);
            close(pstmt);
        }

        return ets;
    }

    private String createTokenID() throws Exception {
        MD5Digester dig = new MD5Digester();
        GUID guid = new GUID();
        return dig.asHex(dig.digest(guid.toString()));
    }

    private EntryToken fillEntryToken(ResultSet rs) throws Exception {
        EntryToken et = new EntryToken();
        et.setTokenID(rs.getString("token_id"));
        et.setAccountID(rs.getString("account_id"));
        et.setCredentials(rs.getString("credentials"));
        et.setCreateTime(rs.getLong("create_time"));
        et.setCampaign(rs.getInt("campaign"));
        et.setStartTime(rs.getLong("start_time"));
        et.setExpireTime(rs.getLong("expire_time"));
        et.setSessionLength(rs.getLong("session_length"));
        et.setStatus(rs.getInt("status"));
        return et;
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

    private void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}