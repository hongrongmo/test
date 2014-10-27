package org.ei.stripes.action.autoComplete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;

public class AutoCompleteDAO {

	
	private static final String TERM_QUERY = "select MAIN_TERM_DISPLAY, MAIN_TERM_SEARCH, USE_TERMS FROM ";
	private static final String WHERE_QUERY = " WHERE MAIN_TERM_SEARCH like ?";
	private static final HashMap<String, String> dbs = new HashMap<String, String>();
	public static final String GEOTH = "GEO_THESAURUS";
	public static final String GRFTH = "GRF_THESAURUS";
	public static final String CPXTH = "CPX_THESAURUS";
	public static final String INSTH = "INS_THESAURUS";
	public static final String EPTTH = "EPT_THESAURUS";
	private final static Logger log4j = Logger.getLogger(AutoCompleteDAO.class);	
	
	 public AutoCompleteDAO(){}
	    
	
    /**
    * This method for getting the connection from the connection pool.
    * @ return : connection.
    */
    protected Connection getConnection() throws AutoCompleteException
    {
        ConnectionBroker broker = null;
        Connection con = null;
        try
        {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
        }
        catch(Exception e)
        {
            throw new AutoCompleteException(e);
        }
        return con;
    }
    /**
    * This method is for replacing the connection into the connection pool.
     * @throws AutoCompleteException 
    * @ param : connection.
    */
    protected void replaceConnection(Connection con) throws  AutoCompleteException
    {
        ConnectionBroker broker = null;
        try
        {
            broker = ConnectionBroker.getInstance();
            broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
        }
        catch(Exception e)
        {
            throw new AutoCompleteException(e);
        }
    }
    
   
    public JSONArray getAllJSONTerms(ArrayList<String> dbList) throws AutoCompleteException{
    	Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        JSONArray objList = new JSONArray();
        
        if(dbList == null){
        	//return empty list
        	return objList;
        }else if(dbList != null && dbList.isEmpty()){
        	//we will just use compendex
        	dbList.add(AutoCompleteDAO.CPXTH);
        }
        
        try
        {
            con = getConnection();
            
            String qry = TERM_QUERY + " ";
            
            
            for(String db : dbList){
            	//run a query for each db sent in
                pstmt = con.prepareStatement(qry + db);
                rs = pstmt.executeQuery();
                //object to hold all the objects
                
                while(rs.next())
                {
                	JSONObject jsonObj = new JSONObject();
                	
                	jsonObj.put(AutoCompleteConfig.MT_DISPLAY, rs.getString("MAIN_TERM_DISPLAY"));
                	jsonObj.put(AutoCompleteConfig.MT_SEARCH, rs.getString("MAIN_TERM_SEARCH"));
                	jsonObj.put(AutoCompleteConfig.USE_TERMS, rs.getString("USE_TERMS"));
                	objList.put(jsonObj);
                }                
            }
            



        }
        catch(Exception e)
        {
            throw new AutoCompleteException(e);
        }
        finally
        {

            if(rs != null)
            {
                try
                {
                    rs.close();
                    rs = null;
                }
                catch(Exception e)
                {
                }
            }

            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                    pstmt = null;
                }
                catch(Exception e)
                {
                }
            }

            if(con != null)
            {
                replaceConnection(con);
            }
        }

        
    	return objList;
    }
    
    public JSONArray getJSONTerms(String term, ArrayList<String> dbList, int rows) throws AutoCompleteException{
    	Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        JSONArray objList = new JSONArray();

        
        if(StringUtils.isBlank(term) || dbList == null){
        	//return empty list
        	return objList;
        }else if(dbList != null && dbList.isEmpty()){
        	//we will just use compendex
        	dbList.add(AutoCompleteDAO.CPXTH);
        }
        
        try
        {
            con = getConnection();
            
            String qry = TERM_QUERY + " ";
            
            
            for(String db : dbList){
            	qry = qry + db + WHERE_QUERY;
            	if(rows > 0){
            		qry +=  " AND ROWNUM <= " + rows;
            	}
            	//run a query for each db sent in
                pstmt = con.prepareStatement(qry);
                pstmt.setString(1, term+"%");
                rs = pstmt.executeQuery();
                //object to hold all the objects
                
                while(rs.next())
                {
                	JSONObject jsonObj = new JSONObject();
                	
                	jsonObj.put(AutoCompleteConfig.MT_DISPLAY, rs.getString("MAIN_TERM_DISPLAY"));
                	jsonObj.put(AutoCompleteConfig.MT_SEARCH, rs.getString("MAIN_TERM_SEARCH"));
                	
                	String useTerms = rs.getString("USE_TERMS");
                	//if this has a use term then we should add it as a seperate entry to render nicely.
                	
                	if(StringUtils.isNotBlank(useTerms)){
                		jsonObj.put(AutoCompleteConfig.HAS_USE_TERMS, true);
                		
                		//create a use term object for type ahead to use.
                		JSONObject useObj = new JSONObject();
                		useObj.put(AutoCompleteConfig.MT_DISPLAY, useTerms);
                		useObj.put(AutoCompleteConfig.MT_SEARCH, useTerms);
                		useObj.put(AutoCompleteConfig.IS_USE_TERMS, true);
                		useObj.put(AutoCompleteConfig.HAS_USE_TERMS, false);
                		jsonObj.put(AutoCompleteConfig.USE_TERMS, useObj);
                		objList.put(jsonObj);
                		
                	}else{
                		jsonObj.put(AutoCompleteConfig.HAS_USE_TERMS, false);
                		jsonObj.put(AutoCompleteConfig.IS_USE_TERMS, false);
                		objList.put(jsonObj);
                	}
                	
                	
                }                
            }
            



        }
        catch(Exception e)
        {
            throw new AutoCompleteException(e);
        }
        finally
        {

            if(rs != null)
            {
                try
                {
                    rs.close();
                    rs = null;
                }
                catch(Exception e)
                {
                }
            }

            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                    pstmt = null;
                }
                catch(Exception e)
                {
                }
            }

            if(con != null)
            {
                replaceConnection(con);
            }
        }

        
    	return objList;
    }
    
    
}
