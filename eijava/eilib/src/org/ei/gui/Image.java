package org.ei.gui;

public class Image extends Component {

	public int size;
	public String source;

	public Image(String source) {
		this.source = source;
	}
	
	public void setSource(String source){
		this.source = source;
	}
	public String getSource(){
		return source;
	}
	public String render() {

		StringBuffer toHtmlBuffer = new StringBuffer();

		toHtmlBuffer.append("<img src=\"" + source + "\"");

		if (border != null)
			toHtmlBuffer.append(" border=\"" + border + "\"");
		if (height != null)
			toHtmlBuffer.append(" height=\"" + height + "\"");
		
		toHtmlBuffer.append("></img>");
		
		return toHtmlBuffer.toString();

	}
}