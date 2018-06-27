package org.ei.dataloading;

import java.io.PrintStream;
import java.sql.Connection;

public abstract class CombinerTimestamp extends Combiner {

    public CombinerTimestamp(CombinedWriter writer) {

        super(writer);

    }

    public void writeCombinedByTimestamp(String connectionURL, String driver, String username, String password, long timestamp) throws Exception {

        Connection con = null;
        PrintStream out = null;

        try {
            con = getConnection(connectionURL, driver, username, password);

            writeCombinedByTimestampHook(con, timestamp);
        } finally {

            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {

                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public abstract void writeCombinedByTimestampHook(Connection con, long timestamp) throws Exception;

}
