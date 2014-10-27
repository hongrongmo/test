<%@ page session="false" %>
<%@ page import="org.ei.connectionpool.*" %>
<%@ page import="org.ei.config.*" %>
<%@ page import="java.sql.*" %>

<root>
    <![CDATA[
<%

	ConnectionBroker m_broker = null;
	Connection con = null;
	Statement stmt = null;
	ResultSet rset = null;

	try {
		m_broker = ConnectionBroker.getInstance("session");

		con = m_broker.getConnection("session");
		stmt = con.createStatement();
		rset = stmt.executeQuery("SELECT * FROM USER_TABLES");
		if(rset.next()) {
			out.print("Up And Running");
		}
		else {
			out.print("Error DB TEST Exception ");
		}
	} catch(Exception e) {
		out.print("Error DB TEST Exception ");
	} finally {
		if(rset != null) {
			try {
				rset.close();
			} catch(SQLException e1) {
				out.print("Error DB TEST Exception ");
			}
		}

		if(stmt != null) {
			try {
				stmt.close();
			} catch(SQLException sqle) {
				out.print("Error DB TEST Exception ");
			}
		}
		if(con != null) {
			try {
				m_broker.replaceConnection(con,"session");
			} catch(Exception cpe) {
				out.print("Error DB TEST Exception ");
			}
		}
	 }

%>
]]>
</root>