package org.ei.connectionpool;

import java.sql.Connection;

public class DBConnection {

	private	String poolConf = "E:/EI/chembundle/jakarta-tomcat-3.2.2/webapps/userservice/WEB-INF/pools.xml";
	private String authCon="ChemPool";
	ConnectionBroker broker;

	public DBConnection () {
		try {
			broker = ConnectionBroker.getInstance(poolConf);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		Connection con = null;
		try {
			con=broker.getConnection(authCon);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	public void replaceConnection(Connection w,String name)throws ConnectionPoolException{
			broker.replaceConnection(w,authCon);
	}

	public void closeConnection(Connection con){
	    try{
	        con.close();
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
}