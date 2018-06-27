package org.ei.connectionpool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.ei.xml.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConnectionBroker {
    private Logger log4j = Logger.getLogger(ConnectionBroker.class);

    private static ConnectionBroker instance = null;
    private static Context envcontext;
    private Hashtable<String, DataSource> datasources = new Hashtable<String, DataSource>();

    // Deprecated fields
    private Hashtable<String, ConnectionPool> poolsTable = new Hashtable<String, ConnectionPool>();
    public static void main(String args[]) throws Exception {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        ConnectionBroker broker = ConnectionBroker.getInstance();
        try {
            con = broker.getConnection("ChemPool");
            stmt = con.createStatement();
            rs = stmt.executeQuery("Select * from tab");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            broker.closeConnections();
        }
    }

    /**
     * Constructor for ConnectionBroker
     * 
     * @throws ConnectionPoolException
     */
    private ConnectionBroker() throws ConnectionPoolException {
        try {
            Context initialcontext = new InitialContext();
            envcontext = (Context) initialcontext.lookup("java:comp/env");
        } catch (NamingException e) {
            throw new ConnectionPoolException(e);
        }
    }

    @Deprecated
    private ConnectionBroker(String configFileURI) throws ConnectionPoolException {
        try {

            DOMParser parser = new DOMParser();
            Document doc = parser.parse(configFileURI);

            List<Node> pools = new ArrayList<Node>();
            Element e = doc.getDocumentElement();
            NodeList childNodeList = e.getChildNodes();
            for (int i = 0; i < childNodeList.getLength(); i++) {
                if (!(childNodeList.item(i).getNodeType() == (Node.TEXT_NODE))) {
                    pools.add(childNodeList.item(i));
                }
            }

            for (int x = 0; x < pools.size(); ++x) {
                Node pool = (Node) pools.get(x);
                ConnectionPool cpool = new ConnectionPool();
                NamedNodeMap map = pool.getAttributes();
                String name = map.getNamedItem("NAME").getNodeValue();
                System.out.println("NAME:" + name);
                String url = map.getNamedItem("URL").getNodeValue();
                System.out.println("URL:" + url);
                String driver = map.getNamedItem("DRIVER").getNodeValue();
                System.out.println("DRIVER:" + driver);
                String username = map.getNamedItem("USERNAME").getNodeValue();
                System.out.println("USERNAME:" + username);
                String password = map.getNamedItem("PASSWORD").getNodeValue();
                System.out.println("PASSWORD:" + password);
                String logFile = map.getNamedItem("LOGFILE").getNodeValue();
                System.out.println("LOGFILE:" + logFile);
                String mincon = map.getNamedItem("MINCONNECTIONS").getNodeValue();
                System.out.println("MINCON:" + mincon);
                String maxcon = map.getNamedItem("MAXCONNECTIONS").getNodeValue();
                System.out.println("MAXCON:" + maxcon);
                String refresh = map.getNamedItem("REFRESHINTERVAL").getNodeValue();
                System.out.println("REFRESH:" + refresh);
                cpool.setUrl(url);
                cpool.setDriver(driver);
                cpool.setPassword(password);
                cpool.setUsername(username);
                cpool.setLogFile(logFile);
                cpool.setRefreshInterval(Double.parseDouble(refresh));
                cpool.setMaxConnections(Integer.parseInt(maxcon));
                cpool.setMinConnections(Integer.parseInt(mincon));
                cpool.initialize();
                poolsTable.put(name, cpool);
            }
        } catch (Exception e) {
            throw new ConnectionPoolException(e);
        }
    }

    @Deprecated
    public static synchronized ConnectionBroker getInstance(String configFileURI) throws ConnectionPoolException {
        return getInstance();
    }

    /**
     * Create an instance of the broker.
     * 
     * @return
     * @throws ConnectionPoolException
     */
    public static ConnectionBroker getInstance() throws ConnectionPoolException {
        Object lock = new Object();
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ConnectionBroker();
                }
            }
        }
        return instance;
    }

    /**
     * Get a new DB connection. This relies on a JNDI resource being present. Currently this should be in the META-INF/context.xml file for the current webapp.
     * 
     * @param name
     * @return
     * @throws NoConnectionAvailableException
     */
    public Connection getConnection(String name) throws NoConnectionAvailableException {
        // Ensure we have a Context
        if (envcontext == null) {
            log4j.error("No env context object is created!");
            throw new NoConnectionAvailableException("No env context object is created!");
        }

        // Get the datasource via JNDI lookup OR from the Hashtable
        DataSource ds = null;
        try {
            if (datasources.containsKey(name)) {
                log4j.info("Using datasource from hashtable for '" + name + "'");
                ds = datasources.get(name);
            } else {
                log4j.info("JNDI lookup for '" + name + "'");
                ds = (DataSource) envcontext.lookup("jdbc/" + name);
                datasources.put(name, ds);
            }
            return ds.getConnection();
        } catch (NamingException e) {
            log4j.error("Unable to create connection to: jdbc/" + name, e);
            throw new NoConnectionAvailableException("Unable to find datasource by JNDI: jdbc/" + name);
        } catch (SQLException e) {
            log4j.error("Unable to create connection to: jdbc/" + name, e);
            throw new NoConnectionAvailableException("Unable to create connection!");
        }
    }

    @Deprecated
    public void closeConnections() throws ConnectionPoolException {
        String message = "Call to deprecated closeConnections() method!";
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length > 0) {
            message += "  Caller = " + stackTraceElements[stackTraceElements.length - 1].getFileName() + ":"
                + stackTraceElements[stackTraceElements.length - 1].getClassName() + "." + stackTraceElements[stackTraceElements.length - 1].getMethodName()
                + ", line " + stackTraceElements[stackTraceElements.length - 1].getLineNumber();
        }
        log4j.warn(message);

        /*
         * try { if(poolsTable.size() > 0) { // Let the connections that are out there in use get checked back in. Thread.sleep(2000); Enumeration en =
         * poolsTable.keys(); while(en.hasMoreElements()) { String poolID = (String)en.nextElement(); ConnectionPool pool =
         * (ConnectionPool)poolsTable.get(poolID); pool.closeConnections(); poolsTable.remove(en); } } } catch(Exception e) { throw new
         * ConnectionPoolException(e); } finally { instance = null; poolsTable = new Hashtable(); }
         */
    }

    public void replaceConnection(Connection w, String name) throws ConnectionPoolException {
        try {
            w.commit();
            w.close();
            w = null;
        } catch (SQLException e) {

        }
    }

}
