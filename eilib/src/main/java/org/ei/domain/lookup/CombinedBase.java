package org.ei.domain.lookup;

import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.LookupIndexException;

public abstract class CombinedBase implements LookupIndexSearcher {

    private LookupSkipper skipper;
    private boolean dynamic;
    private DatabaseConfig dconfig = DatabaseConfig.getInstance();

    protected boolean isCBF = false;

    public CombinedBase(String sessionID, int indexesPerPage, boolean dynamic) {
        this.skipper = new LookupSkipper(sessionID);
        this.dynamic = dynamic;
    }

    protected String getSqlIN(Database[] databases) {
        StringBuffer instatement = new StringBuffer();
        for (int i = 0; i < databases.length; ++i) {
            if (instatement.length() > 0) {
                instatement.append(", ");
            }

            String dbid = databases[i].getIndexName().substring(0, 3);
            instatement.append("'").append(dbid).append("'");
        }

        return instatement.toString();
    }

    public void lookupIndexXML(String lookupSearchID, String searchword, Database[] databases, int pageNumber, Writer out) throws LookupIndexException {
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String sql = null;
        String displayName = "-";
        String dbName = "-";
        searchword = searchword.toUpperCase().replaceAll("'", "''");
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();

            sql = getSQLHook(searchword, databases);

            rset = stmt.executeQuery(sql.toString());

            String dyn = "DYNAMIC";

            if (!dynamic) {
                dyn = "STATIC";
            }

            out.write("<LOOKUP-INDEXES TYPE=\"" + dyn + "\">");

            int skipCount = 0;
            int skip = 0;

            if (pageNumber > 1) {
                skip = skipper.getSkipCount(lookupSearchID, pageNumber);
            } else {
                skip = 0;
            }

            while ((skipCount < skip) && rset.next()) {
                skipCount++;
            }

            DeDuper dups = new DeDuper();

            while (rset.next() && dups.size() < 100) {

                displayName = getDisplayNameHook(rset);
                String lookupValue = rset.getString("lookup_value");
                if (lookupValue.equals("QQ")) {
                    lookupValue = displayName;
                }

                dbName = ((String) rset.getString("dbase")).trim();
                dups.put(displayName, lookupValue, dconfig.getDatabase(dbName));

                ++skipCount;
            }

            int z = 0;
            while (rset.next() && z < (databases.length - 1)) {
                displayName = getDisplayNameHook(rset);
                String lookupValue = rset.getString("lookup_value");

                if (lookupValue.equals("QQ")) {
                    lookupValue = displayName;
                }

                dbName = ((String) rset.getString("dbase")).trim();

                if (dups.contains(displayName)) {

                    dups.put(displayName, lookupValue, dconfig.getDatabase(dbName));
                    ++skipCount;
                }
                ++z;
            }

            skipper.putSkipCount(lookupSearchID, pageNumber + 1, skipCount);
            List<IndexItem> items = dups.getIndexItems();
            int databasesPassedIn = countDatabases(databases);

            for (int k = 0; k < items.size(); ++k) {
                IndexItem ii = (IndexItem) items.get(k);
                List<Database> databases2 = ii.getDatabases();

                out.write("<LOOKUP-INDEX>");
                if (databasesPassedIn > 1) {
                    out.write("<LOOKUP-DBS>");
                    for (int j = 0; j < databases2.size(); ++j) {
                        Database db = (Database) databases2.get(j);
                        out.write("<DB>");
                        out.write(db.getName());
                        out.write("</DB>");
                    }

                    out.write("</LOOKUP-DBS>");
                }
                out.write("<LOOKUP-NAME>");
                out.write("<![CDATA[" + ii.getName() + "]]>");
                out.write("</LOOKUP-NAME>");
                out.write("<LOOKUP-VALUE>");
                out.write("<![CDATA[" + ii.getValue() + "]]>");
                out.write("</LOOKUP-VALUE>");
                out.write("</LOOKUP-INDEX>");
            }

            out.write("</LOOKUP-INDEXES>");
        } catch (Exception e) {
            e.printStackTrace();
            throw new LookupIndexException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }

            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);

                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        }
    }

    public int countDatabases(Database[] databases) {
        int c = 0;

        for (int i = 0; i < databases.length; i++) {
            if (!databases[i].isBackfile()) {
                c++;
            }
        }
        return c;
    }

    protected String getDisplayNameHook(ResultSet rs) throws Exception {
        return rs.getString("lookup_name");
    }

    protected abstract String getSQLHook(String searchword, Database[] databases);
}