package org.ei.gui;

public class Button extends Component {

	private String source;
	
	public Button() {
		super();
	}
	public void setSource(String source){
		this.source = source;
	}
	public String render()  {
			
		StringBuffer toHtmlBuffer = new StringBuffer();
		
		
		toHtmlBuffer.append("<input type=\"image\" name=\"go\" src=\""+source+"\" border=\""+border+"\"/>");
		
		
		
		return toHtmlBuffer.toString();
	}
}