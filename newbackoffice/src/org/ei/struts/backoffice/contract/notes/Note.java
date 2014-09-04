/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/notes/Note.java-arc   1.1   Apr 01 2009 11:55:00   johna  $
 * $Revision:   1.1  $
 * $Date:   Apr 01 2009 11:55:00  $
 *
 */
package org.ei.struts.backoffice.contract.notes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.contract.Contract;
import org.ei.struts.backoffice.util.date.BODate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Note {

	private static Log log = LogFactory.getLog("Note");

	private String m_strNoteID = null;
	private String m_strContractID = null;
	private String m_strItemID = null;
	private String m_strUserID = null;
	private String m_strNote = null;
	private BODate m_dteCreatedDate = new BODate();

	// ----------------------------------------------------------- Properties

	public String getNoteID() {
		return m_strNoteID;
	}
	public void setNoteID(String note) {
		m_strNoteID = note;
	}

	public String getItemID() {
		return m_strItemID;
	}
	public void setItemID(String itemid) {
		m_strItemID = itemid;
	}

	public String getUserID() {
		return m_strUserID;
	}
	public void setUserID(String user) {
		m_strUserID = user;
	}

	public String getContractID() {
		return m_strContractID;
	}
	public void setContractID(String contractid) {
		m_strContractID = contractid;
	}

	public String getNote() {
		return m_strNote;
	}

	public void setNote(String note) {
		this.m_strNote = (note != null) ? note.trim() : null;
	}

	public BODate getCreatedDate() { return m_dteCreatedDate; }
	public void setCreatedDate(BODate bodate) { m_dteCreatedDate = bodate; }
	public void setCreatedDate(String strdate) { m_dteCreatedDate.setDate(strdate);	}

	// -----------------------------------------------------------


	public Map getLinkParams() {
		Map params = new HashMap();
		return params;
	}

	// -----------------------------------------------------------

  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
    buffer.append("noteId").append("='").append(getNoteID()).append("' ");
    buffer.append("contractId").append("='").append(getContractID()).append("' ");
    buffer.append("itemId").append("='").append(getItemID()).append("' ");
    buffer.append("user").append("='").append(getUserID()).append("' ");
    buffer.append("date").append("='").append(getCreatedDate()).append("' ");
    buffer.append("note").append("='").append(getNote()).append("' ");
    buffer.append("]");

    return buffer.toString();
  }

}
