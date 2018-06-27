package org.ei.gui;

import java.util.Iterator;
import java.util.Vector;

public class ListBox extends Component {

	private Object arrOptions[];
	private Object arrDisplayVals[];
	private int selectedIndex = -1;
	private String size;
	private String classType;
	private String title;
	Vector vNbsps = new Vector();
	String javaScript;
	

	public void setClassType(String classType) {
		this.classType = classType;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setJavaScript(String javaScript){
		
		this.javaScript = javaScript;
	}
	public void setOptions(Object arrOptions[]) {
		this.arrOptions = arrOptions;
	}
	public void setValues(Object arrDisplayVals[]) {
		this.arrDisplayVals = arrDisplayVals;
	}
	public void setSize(String sSize) {
		this.size = sSize;
	}
	
	public void addNbsp(int count) {

		for (int i = 0; i < count; i++) {
			vNbsps.add("&nbsp;");
		}
	}
	public void setDefaultChoice(String sChoice) {

		if (arrDisplayVals != null) {
			int iSize = arrDisplayVals.length;
			for (int index = 0; index < iSize; index++)
				if (arrDisplayVals[index].equals(sChoice))
					selectedIndex = index;
		} else {
			int iSize = arrOptions.length;
			for (int index = 0; index < iSize; index++)
				if (arrOptions[index].equals(sChoice))
					selectedIndex = index;

		}

	}
	
	public String render() {

		StringBuffer toHtmlBuff = new StringBuffer();

		if (vNbsps.size() > 0) {
			for (Iterator iter = vNbsps.iterator(); iter.hasNext();) {
				String nbsp = (String) iter.next();
				toHtmlBuff.append(nbsp);
			}
		}
		toHtmlBuff.append("<SELECT NAME=\"").append(name).append("\"");

		if (title != null) {
			toHtmlBuff.append(" title=\"" + title + "\"");
		}
		
		if (classType != null) {
			toHtmlBuff.append(" class=\"" + classType + "\"");
		}
		
		if(javaScript != null){
			toHtmlBuff.append(" onChange=\"").append(javaScript).append("\"");
		}

		if (size != null && !size.trim().equals(""))
			toHtmlBuff.append(" SIZE=" + size);
			
			
		toHtmlBuff.append(" >");
		if (arrOptions != null) {
			int szOptions = arrOptions.length;
			for (int index = 0; index < szOptions; index++) {
				toHtmlBuff.append("<OPTION");
				if (selectedIndex == index)
					toHtmlBuff.append(" SELECTED");

				if (arrDisplayVals == null)
					toHtmlBuff.append(" VALUE=\"").append(arrOptions[index]).append("\">").append(arrOptions[index]);
				else
					toHtmlBuff.append(" VALUE=\"").append(arrDisplayVals[index]).append("\">").append(arrOptions[index]);
				toHtmlBuff.append("</OPTION>");
			}
		}
		toHtmlBuff.append("</SELECT>");

		return toHtmlBuff.toString();
	}
}