package org.ei.gui;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class Form extends Container  {

	public static final String POST_METHOD = "POST";
	protected String actionUrl;
	protected Button submitButton;
	protected Button resetButton;
	protected String name;
	String cid;
	String cidVal;
	String hiddenField;
	StringBuffer toHtmlBuffer = new StringBuffer();
	List vHiddenFields = new Vector();
	
	public void setName(String name){
		this.name = name;
	}
	public Form() {
	}
	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}
	public void setCID(String cidVal){
		this.cidVal = cidVal;
	}
	public void addHiddenField(String name,String value){
		
		StringBuffer htmlBuffer = new StringBuffer();
		
		htmlBuffer.append("<INPUT TYPE=\"HIDDEN\" NAME=\"").append(name).append("\" VALUE=\"").append(value).append("\"/>");
		
		vHiddenFields.add(htmlBuffer.toString());
	
	}
	public String render()   {
		
		toHtmlBuffer.append("<FORM NAME=\"").append(name).append("\" ACTION=\"").append(actionUrl).append("\" METHOD=\"").append(POST_METHOD).append("\"");
		toHtmlBuffer.append(">");
		
		for (Iterator iter = vHiddenFields.iterator(); iter.hasNext();) {
			String hfField = (String) iter.next();
			toHtmlBuffer.append(hfField);
		}
		
		toHtmlBuffer.append("<INPUT TYPE=\"HIDDEN\" NAME=\"").append("CID").append("\" VALUE=\"").append(cidVal).append("\"/>");
		toHtmlBuffer.append(super.render());
		toHtmlBuffer.append("</FORM>");
		
		return toHtmlBuffer.toString();
	}
}
