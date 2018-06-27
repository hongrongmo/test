package org.ei.tags;

import java.io.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * This is basic Tag class which holds the setters and getters of the tag
 * fields.
 */
// ///////////////////////////////////////////////////////////////////////////////////////////////////////

public class Tag {

	public static final String COLOR_PUBLIC = "#002D68";
	public static final String COLOR_PRIVATE = "#A3B710";
	public static final String COLOR_INSTITUTION = "#A56500";

	private String tID;
	private String groupID = "";
	private TagGroup group = null;
	private String groupTitle = null;
	private String tag;
	private String tagSearchValue;
	private long timestamp = -1L;
	private int scope = Scope.SCOPE_PUBLIC;
	private String userID;
	private String custID;
	private int count;
	private String docID;
	private int dmask = 1;
	private String collection;
	private String size;

	/**
	 * No args Constructor
	 */

	public Tag() {

	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return this.count;
	}

	public String getTagName() {
		return this.tag;
	}

	public void setTagName(String tag) {
		this.tag = tag;
	}

	/**
	 * set method to set the fields for the tag.
	 *
	 * @param : int (tagid ).
	 */

	public void setTagID(String tid) {
		this.tID = tid;
	}

	public void setGroup(TagGroup group) {
		this.group = group;
	}

	public TagGroup getGroup() {
		return this.group;
	}

	/**
	 * set method to set the groupid to the document.
	 *
	 * @param : int(group id).
	 */
	public void setGroupID(String groupid) {
		this.groupID = groupid;
	}

	/**
	 * set method to set the grouptitle to the document.
	 *
	 * @param : int(group id).
	 */
	public void setGroupTitle(String grouptitle) {
		this.groupTitle = grouptitle;
	}

	/**
	 * set method to set the tag of the document.
	 *
	 * @param : String(tag).
	 */
	public void setTag(String tag) {
		this.tag = tag;
		this.tagSearchValue = tag.toUpperCase().trim();
	}

	/**
	 * set method to set the id of the document.
	 *
	 * @param : String(document id).
	 */
	public void setDocID(String docid) {
		this.docID = docid;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getCollection() {
		return this.collection;
	}

	/**
	 * set method to set the timestamp of the document.
	 *
	 * @param : int(timestamp).
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		if (this.timestamp < 0) {
			return System.currentTimeMillis();
		} else {
			return this.timestamp;
		}
	}

	/**
	 * set method to set the visibility/access of the document.
	 *
	 * @param : String(visibility).
	 */
	public void setScope(int scope) {

		this.scope = scope;
	}

	public String getColor() {
		if (this.scope == Scope.SCOPE_PUBLIC) {
			// System.out.println("Scope is public.");
			return COLOR_PUBLIC;
		} else if (this.scope == Scope.SCOPE_PRIVATE) {
			// System.out.println("Scope is private.");
			return COLOR_PRIVATE;
		} else if (this.scope == Scope.SCOPE_GROUP) {
			// System.out.println("Scope is group.");
			if (this.group != null) return this.group.getColorByID().getCode();
			else return COLOR_PRIVATE;
		} else if (this.scope == Scope.SCOPE_INSTITUTION) {
			// System.out.println("Scope is group.");
			return COLOR_INSTITUTION;
		}

		return "";
	}

	/**
	 * set method to set the database in which the document contain.
	 *
	 * @param : int (database).
	 */
	public void setMask(int dmask) {
		this.dmask = dmask;
	}

	/**
	 * set method to set the userid of the tag created.
	 *
	 * @param : String(userid).
	 */
	public void setUserID(String userid) {
		this.userID = userid;
	}

	/**
	 * set method to set the custid of the tag created.
	 *
	 * @param : int(custid).
	 */
	public void setCustID(String custid) {
		this.custID = custid;
	}

	/**
	 * get method to get the fields for the tag.
	 *
	 * @param : int (tagid ).
	 */
	public String getTagID() {
		return tID;
	}

	/**
	 * get method to get the groupid to the document.
	 *
	 * @param : int(group id).
	 */
	public String getGroupID() {
		return groupID;
	}

	/**
	 * get method to get the grouptitle to the document.
	 *
	 * @param : int(group id).
	 */
	public String getGroupTitle() {
		return groupTitle;
	}

	/**
	 * get method to get the tag of the document.
	 *
	 * @param : String(tag).
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * get method to get the tagSearchvalue of the document.
	 *
	 * @param : String(tagSearchvalue).
	 */
	public String getTagSearchValue() {
		return tagSearchValue;
	}

	/**
	 * get method to get the docid of the document.
	 *
	 * @param : String(document id).
	 */
	public String getDocID() {
		return docID;
	}

	/**
	 * get method to get the visibility/access of the document.
	 *
	 * @param : String(visibility).
	 */
	public int getScope() {
		return this.scope;
	}

	/**
	 * get method to get the database in which the document contain.
	 *
	 * @param : int (database).
	 */
	public int getMask() {
		return dmask;
	}

	/**
	 * get method to get the userid of the tag created.
	 *
	 * @param : String(userid).
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * get method to get the custid of the tag created.
	 *
	 * @param : int(custid).
	 */
	public String getCustID() {
		return custID;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void toXML(Writer out) throws IOException {

		out.write("<TAG>");
		out.write("<TAGID>");
		out.write(getTagID());
		out.write("</TAGID>");
		if (group != null) {
			// System.out.println("There is a group");
			group.toXML(out);
		}

		out.write("<TAGNAME><![CDATA[");
		out.write(getTag());
		out.write("]]></TAGNAME>");

		out.write("<TAGVALUE><![CDATA[");
		out.write(getTagSearchValue());
		out.write("]]></TAGVALUE>");
		if (this.docID != null) {
			out.write("<DOCID>");
			out.write(this.docID);
			out.write("</DOCID>");
		}
		out.write("<TIMESTAMP>");
		out.write(Long.toString(getTimestamp()));
		out.write("</TIMESTAMP>");
		out.write("<SCOPE>");
		out.write(String.valueOf(scope));
		out.write("</SCOPE>");
		out.write("<DBMASK>");
		out.write(Integer.toString(getMask()));
		out.write("</DBMASK>");
		out.write("<USERID>");
		out.write(getUserID());
		out.write("</USERID>");
		out.write("<CUSTID>");
		out.write(getCustID());
		out.write("</CUSTID>");
		out.write("</TAG>");
	}
}
