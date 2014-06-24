package org.ei.service.properties.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;

public class RuntimePropertiesDAO {

    private static final String SESSION_POOL = "session";

    public String getSSOCoreRedirectFlag() throws RuntimePropertiesDAOException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String ssoCoreRedirectFlag = null;

        try {
            ConnectionBroker broker = ConnectionBroker.getInstance();
            con = broker.getConnection(SESSION_POOL);
            con.setAutoCommit(false);
            pstmt = con.prepareStatement("select value from RUNTIME_PROPERTIES where name = 'SSO_CORE_REDIRECT_FLAG'");

            rs = pstmt.executeQuery();

            if (rs.next()) {
                ssoCoreRedirectFlag = rs.getString(1);
            }

        } catch (ConnectionPoolException e) {
            throw new RuntimePropertiesDAOException("Runtime Properties access problem from database", e);
        } catch (NoConnectionAvailableException e) {
            throw new RuntimePropertiesDAOException("Runtime Properties access problem from database", e);
        } catch (SQLException e) {
            throw new RuntimePropertiesDAOException("Runtime Properties access problem from database", e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    ConnectionBroker broker = ConnectionBroker.getInstance();
                    broker.replaceConnection(con, SESSION_POOL);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        return ssoCoreRedirectFlag;
    }

}