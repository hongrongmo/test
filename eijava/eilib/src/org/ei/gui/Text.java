package org.ei.gui;

public class Text extends Component {

	public int size;
	public String source;
	String classType;
	String text;
	boolean isBold = false;

	
	public void setClassType(String classType) {
		this.classType = classType;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSource() {
		return source;
	}
	public void setBold(boolean bold) {
		this.isBold = bold;
	}
	public String render() {

		StringBuffer toHtmlBuffer = new StringBuffer();

		toHtmlBuffer.append("<a class=\"" + classType + "\">");

		if (isBold) {
			toHtmlBuffer.append("<b>" + text + "</b>");
			toHtmlBuffer.append("</a>");
		} else {
			toHtmlBuffer.append(text);
			toHtmlBuffer.append("</a>");
		}

		return toHtmlBuffer.toString();
	}
}