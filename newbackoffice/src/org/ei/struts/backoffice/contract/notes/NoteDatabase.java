/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/notes/NoteDatabase.java-arc   1.0   Mar 25 2009 11:23:20   johna  $
 * $Revision:   1.0  $
 * $Date:   Mar 25 2009 11:23:20  $
 *
 */
package org.ei.struts.backoffice.contract.notes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.contract.Contract;
import org.ei.struts.backoffice.util.uid.UID;

public class NoteDatabase {

  private static Log log = LogFactory.getLog("NoteDatabase");

  public Note createNote() {
    Note aNote = null;
    aNote  = new Note();
    return aNote ;
  }

  private long getNextNoteID()  throws SQLException {
    return UID.getNextId();
  }

  public Note findNote(String noteid) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Note aItem = null;


    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
      int idx = 1;

      conn = ds.getConnection();
      pstmt = conn.prepareStatement("SELECT * FROM CONTRACT_NOTES WHERE NOTES_ID=?");
      pstmt.setString(idx++, noteid);
      rs = pstmt.executeQuery();

      if(rs.next()) {
        aItem = new Note();
        aItem.setNoteID(rs.getString("NOTES_ID"));
        aItem.setContractID(rs.getString("CONTRACT_ID"));
        aItem.setItemID(rs.getString("ITEM_ID"));
        aItem.setUserID(rs.getString("USER_ID"));
        aItem.setCreatedDate(rs.getString("CREATED_DATE"));
        aItem.setNote(rs.getString("NOTES"));
      }
    } catch(SQLException e) {
      log.error("findNote ",e);

    } catch(NamingException e) {
      log.error("findNote ",e);

    } finally {

      try {
        if(rs != null)
          rs.close();
        if(pstmt != null)
          pstmt.close();
        if(conn != null)
          conn.close();
      } catch(SQLException e) {
        log.error("findNote ",e);
      }
    }

    return aItem;
  }

    // added test to only return contract items whose item # is > 0
  public Collection getNotes(String contractid, String itemid) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Collection allItems = new ArrayList();

    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
      int idx = 1;

      conn = ds.getConnection();
      pstmt = conn.prepareStatement("SELECT * FROM CONTRACT_NOTES WHERE CONTRACT_ID=? AND ITEM_ID=? ORDER BY CREATED_DATE ASC");
      pstmt.setString(idx++, contractid);
      pstmt.setString(idx++, itemid);

      rs = pstmt.executeQuery();

      Note aItem = null;
      while(rs.next()) {
        aItem = new Note();
        aItem.setNoteID(rs.getString("NOTES_ID"));
        aItem.setContractID(rs.getString("CONTRACT_ID"));
        aItem.setUserID(rs.getString("USER_ID"));
        aItem.setCreatedDate(rs.getString("CREATED_DATE"));
        aItem.setNote(rs.getString("NOTES"));

        allItems.add(aItem);
      }

    } catch(SQLException e) {
      log.error("getNotes ",e);

    } catch(NamingException e) {
      log.error("getNotes ",e);

    } finally {

      try {
        if(rs != null)
          rs.close();
        if(pstmt != null)
          pstmt.close();
        if(conn != null)
          conn.close();
      } catch(SQLException e) {
        log.error("getNotes  ",e);
      }
    }

    return allItems;
  }

  public boolean saveNote(Note note) {

    boolean result = false;

    try {
      if(findNote(note.getNoteID()) != null) {
        result = updateNote(note);
      } else {
        result = insertNote(note);
      }
    } catch(Exception e) {
      log.error("saveNote ",e);
    }

    return result;
  }


  private boolean updateNote(Note item) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    int result = 0;

    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
      int idx = 1;

      conn = ds.getConnection();
      pstmt = conn.prepareStatement("UPDATE CONTRACT_NOTES SET NOTES=?, CREATED_DATE=SYSDATE, USER=? WHERE NOTES_ID=?");

      pstmt.setString(idx++, item.getNote());
      pstmt.setString(idx++, item.getUserID());
      pstmt.setString(idx++, item.getNoteID());

      result = pstmt.executeUpdate();

    } catch(SQLException e) {
      log.error("updateNote ",e);

    } catch(NamingException e) {
      log.error("updateNote ",e);

    } finally {

      try {
        if(pstmt != null)
          pstmt.close();
        if(conn != null){
			conn.commit();
          conn.close();
	  }
      } catch(SQLException e) {
        log.error("updateNote ",e);
      }
    }

    return (result == 1);
  }

  private boolean insertNote(Note item) {

    Connection conn = null;
    PreparedStatement pstmt = null;
    int result = 0;

    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);
      DataSource ds = (DataSource) envCtx.lookup(Constants.OFFICE_DBCP_POOL);
      int idx = 1;

      conn = ds.getConnection();
      pstmt = conn.prepareStatement("INSERT INTO CONTRACT_NOTES (NOTES_ID, CONTRACT_ID, ITEM_ID, CREATED_DATE, USER_ID, NOTES) VALUES(?,?,?,SYSDATE,?,?)");

      if(item.getNoteID() == null)
      {
        item.setNoteID(String.valueOf(UID.getNextId()));
      }
      pstmt.setString(idx++, item.getNoteID());
      pstmt.setString(idx++, item.getContractID());
      pstmt.setString(idx++, item.getItemID());
      // CREATED_DATE is put in with SYSDATE
      pstmt.setString(idx++, item.getUserID());
      pstmt.setString(idx++, item.getNote());

      result = pstmt.executeUpdate();

    } catch(SQLException e) {
      log.error("insertNote ",e);

    } catch(NamingException e) {
      log.error("insertNote ",e);

    } finally {

      try {
        if(pstmt != null)
          pstmt.close();
        if(conn != null){
			conn.commit();
          conn.close();
	  }
      } catch(SQLException e) {
        log.error("insertNote ",e);
      }
    }

    return (result == 1);
  }
}
