package org.ei.gui;

import java.util.Iterator;
import java.util.Vector;

public class ListBox extends Component {

	private String arrOptions[];
	private String arrDisplayVals[];
	private int selectedIndex = -1;
	private String size;
	private String classType;
	Vector vNbsps = new Vector();

	public void setClassType(String classType) {
		this.classType = classType;
	}
	public void setOptions(String arrOptions[]) {
		this.arrOptions = arrOptions;
	}
	public void setValues(String arrDisplayVals[]) {
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

		if (classType != null)
			toHtmlBuff.append("<a class=\"" + classType + "\" colspan=\"2\">");

		if (vNbsps.size() > 0) {
			for (Iterator iter = vNbsps.iterator(); iter.hasNext();) {
				String nbsp = (String) iter.next();
				toHtmlBuff.append(nbsp);
			}
		}
		toHtmlBuff.append("<SELECT NAME=\"").append(name).append("\"");

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

		if (classType != null)
			toHtmlBuff.append("</a>");

		return toHtmlBuff.toString();
	}
}