package org.ei.tags;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.common.AuthorStream;
import org.ei.domain.DatabaseConfig;

public class TagLoader {

    public static void main(String args[]) throws Exception {
        try {
            openConnectionPool();
            String start = args[0];
            String end = args[1];
            loadTags(start, end);
        } finally {
            closeConnectionPool();
        }
    }

    public static void openConnectionPool() throws Exception {
        ConnectionBroker.getInstance("pools.xml");
    }

    public static void closeConnectionPool() throws Exception {
        ConnectionBroker broker = ConnectionBroker.getInstance();
        broker.closeConnections();
    }

    public static void loadTags(String start, String end) throws Exception {
        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TagBroker tagBroker = new TagBroker();
        AuthorStream flstream = null;
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            pstmt = con.prepareStatement("select m_id, fls from cpx_master where load_number >= ? and load_number <= ?");
            pstmt.setString(1, start);
            pstmt.setString(2, end);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String fl = rs.getString("fls");
                if (fl != null) {
                    System.out.println(fl);
                    String m_id = rs.getString("m_id");
                    flstream = new AuthorStream(new ByteArrayInputStream(fl.getBytes()));
                    String tagname = null;

                    while ((tagname = flstream.readAuthor()) != null) {
                        Tag tag = new Tag();
                        tag.setTag(tagname);
                        tag.setDocID(m_id);
                        tag.setScope(1);
                        tag.setMask(1);
                        tag.setUserID("1");
                        tag.setCustID("1");
                        tagBroker.addTag(tag);
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (flstream != null) {
                    flstream.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                broker = ConnectionBroker.getInstance();
                broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
            } catch (Exception cpe) {
                cpe.printStackTrace();
            }
        }
    }
}
