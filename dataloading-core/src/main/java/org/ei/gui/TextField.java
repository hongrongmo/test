package org.ei.gui;

public class TextField extends Component{

	public int size;

	public TextField(int size){
		this.size = size;
	}
	public String render()  {

		StringBuffer toHtmlBuffer = new StringBuffer();
		
		if(value == null)
			value = "";

		toHtmlBuffer.append("<input type = \"text\" name = \""+name+"\" size = \""+size+"\" value = \""+value+"\" />"); 
	
		return toHtmlBuffer.toString();
	
	}
}